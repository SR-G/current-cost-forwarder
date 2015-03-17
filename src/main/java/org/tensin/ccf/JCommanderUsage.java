package org.tensin.ccf;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.Strings;
import com.beust.jcommander.WrappedParameter;
import com.beust.jcommander.internal.Lists;

public class JCommanderUsage {

    private final JCommander jc;

    private int m_columnSize = 320;

    private Comparator<? super ParameterDescription> m_parameterDescriptionComparator = new Comparator<ParameterDescription>() {
        @Override
        public int compare(final ParameterDescription p0, final ParameterDescription p1) {
            return p0.getLongestName().compareTo(p1.getLongestName());
        }
    };

    public JCommanderUsage(final JCommander jc) {
        this.jc = jc;
    }

    private JCommander findCommandByAlias(final String commandOrAlias) {
        // Wops, not public and return ProgramName class that neither is public.
        // No way to get around this.
        // PROBLEM : JCommander.ProgramName progName = jc.findProgramName(commandOrAlias);

        // So, then it turns out we cannot mimic the functionality implemented in usage for
        // printing command usage :(
        /*
         * if(progName == null) {
         * return null;
         * } else {
         * JCommander jc = this.findCommand(progName);
         * if(jc == null) {
         * throw new IllegalStateException("There appears to be inconsistency in the internal command database.  This is likely a bug. Please report.");
         * } else {
         * return jc;
         * }
         * }
         */
        // Lets go for the solution which is available to us and ignore the logic implemented in
        // JCommander for this lookup.
        return jc.getCommands().get(commandOrAlias);
    }

    public int getColumnSize() {
        return m_columnSize;
    }

    public String getNames(final ParameterDescription pd) {
        StringBuilder sb = new StringBuilder();
        String[] names = pd.getParameter().names();
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(names[i]);
        }
        return sb.toString();
    }

    private Comparator<? super ParameterDescription> getParameterDescriptionComparator() {
        return m_parameterDescriptionComparator;
    }

    /**
     * @return n spaces
     */
    private String s(final int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(" ");
        }

        return result.toString();
    }

    public void seColumnSize(final int m_columnSize) {
        this.m_columnSize = m_columnSize;
    }

    public void setParameterDescriptionComparator(final Comparator<? super ParameterDescription> c) {
        m_parameterDescriptionComparator = c;
    }

    /**
     * Display the usage for this command.
     */
    public void usage(final String commandName) {
        StringBuilder sb = new StringBuilder();
        usage(commandName, sb);
        JCommander.getConsole().println(sb.toString());
    }

    /**
     * Store the help for the command in the passed string builder.
     */
    public void usage(final String commandName, final StringBuilder out) {
        usage(commandName, out, "");
    }

    /**
     * Store the help for the command in the passed string builder, indenting
     * every line with "indent".
     */
    public void usage(final String commandName, final StringBuilder out, final String indent) {
        String description = null;
        try {
            description = jc.getCommandDescription(commandName);
        } catch (ParameterException e) {
            // Simplest way to handle problem with fetching descriptions for the main command.
            // In real implementations we would have done this another way.
        }

        if (description != null) {
            out.append(indent).append(description);
            out.append("\n");
        }
        // PROBLEM : JCommander jcc = jc.findCommandByAlias(commandName); // Wops, not public!
        JCommander jcc = findCommandByAlias(commandName);
        if (jcc != null) {
            jcc.usage(out, indent);
        }
    }

    public void usage(final StringBuilder out, final String indent) {

        // Why is this done on this stage of the process?
        // Looks like something that should have been done earlier on?
        // Anyway the createDescriptions() method is private and not possible to
        // trigger from the outside.
        // I haven't spend time on considering the consequences of not executing the method.
        /* PROBLEM : if (m_descriptions == null) createDescriptions(); */

        boolean hasCommands = !jc.getCommands().isEmpty();

        //
        // First line of the usage
        //

        // The JCommander does not provide a getProgramName() method and therefore
        // makes it impossible for other usage implementations to use it.
        // My first idea was to use the reflection api to change the access level of
        // the m_programName attribute, but then I saw that the ProgramName class is
        // private as well :(
        // Of course it is possible to set an alternative program name in the usage
        // implementation, but that is kind of a second best solution.

        /* PROBLEM : String programName = m_programName != null ? m_programName.getDisplayName() : "<main class>"; */
        String programName = "<main class>";

        out.append(indent).append("Usage: ").append(programName).append(" [options]");
        if (hasCommands) {
            out.append(indent).append(" [command] [command options]");
        }
        if (jc.getMainParameterDescription() != null) {
            out.append(" ").append(jc.getMainParameterDescription());
        }
        out.append("\n");

        //
        // Align the descriptions at the "longestName" column
        //
        int longestName = 0;
        List<ParameterDescription> sorted = Lists.newArrayList();
        for (ParameterDescription pd : jc.getParameters()) {
            if (!pd.getParameter().hidden()) {
                sorted.add(pd);
                // + to have an extra space between the name and the description
                int length = getNames(pd).length() + 4;
                if (length > longestName) {
                    longestName = length;
                }
            }
        }

        //
        // Sort the options
        //
        Collections.sort(sorted, getParameterDescriptionComparator());

        //
        // Display all the names and descriptions
        //
        int descriptionIndent = 6;
        if (sorted.size() > 0) {
            out.append(indent).append("  Options:\n");
        }
        for (ParameterDescription pd : sorted) {
            WrappedParameter parameter = pd.getParameter();
            out.append(indent).append("  ");
            final String s = (parameter.required() ? "* " : "  ") + getNames(pd);
            out.append(s);
            out.append(StringUtils.repeat(" ", longestName - s.length() - 2));
            out.append(s(descriptionIndent));
            int indentCount = indent.length() + descriptionIndent;
            wrapDescription(out, indentCount, pd.getDescription());
            Object def = pd.getDefault();
            if (pd.isDynamicParameter()) {
                out.append("\n").append(s(indentCount + 1)).append("Syntax: ").append(parameter.names()[0]).append("key").append(parameter.getAssignment())
                .append("value");
            }
            if (def != null) {
                String displayedDef = Strings.isStringEmpty(def.toString()) ? "<empty string>" : def.toString();
                out.append(". Default: ").append(parameter.password() ? "********" : displayedDef);
            }
            out.append("\n");
        }

        //
        // If commands were specified, show them as well
        //
        if (hasCommands) {
            out.append("  Commands:\n");
            // The magic value 3 is the number of spaces between the name of the option
            // and its description
            for (Map.Entry<String, JCommander> commands : jc.getCommands().entrySet()) {
                Object arg = commands.getValue().getObjects().get(0);
                Parameters p = arg.getClass().getAnnotation(Parameters.class);
                // I'm not sure why, but this simply doesn't work in my test project.
                // But this is not important in this POC
                // if (!p.hidden()) {
                String dispName = commands.getKey();
                out.append(indent).append("    " + dispName); // + s(spaceCount) + getCommandDescription(progName.name) + "\n");

                // Options for this command
                usage(dispName, out, "      ");
                out.append("\n");
                // }
            }
        }
    }

    private void wrapDescription(final StringBuilder out, final int indent, final String description) {
        int max = getColumnSize();
        String[] words = description.split(" ");
        int current = indent;
        int i = 0;
        while (i < words.length) {
            String word = words[i];
            if ((word.length() > max) || ((current + word.length()) <= max)) {
                out.append(" ").append(word);
                current += word.length() + 1;
            } else {
                out.append("\n").append(s(indent + 1)).append(word);
                current = indent;
            }
            i++;
        }
    }
}
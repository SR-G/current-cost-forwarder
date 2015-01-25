package org.tensin.ccf.boot;

import org.tensin.ccf.CCFException;

/**
 * The Class Mirror4J.
 */
public class CurrentCostForwarder {

    /**
     * Gestion du classpath.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String args[]) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            final ClasspathBooter cb = new ClasspathBooter(CURRENT_COST_FORWARDER_BOOT_JAR, "Mirror4J");
            cb.addAllJars();
            System.out.println(FAKE_LOG_LABEL + "Classpath :" + cb.displayClasspath(LINE_SEPARATOR));
            System.out.println(FAKE_LOG_LABEL + "Manifest :" + cb.getManifest(CURRENT_COST_FORWARDER_BOOT_JAR, LINE_SEPARATOR));
            cb.execute(CCF_MAIN_CLASS, "main", new Class[] { args.getClass() }, new Object[] { args });
        } catch (final CCFException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** The Constant MIRROR4J_MAIN_CLASS. */
    private static final String CCF_MAIN_CLASS = org.tensin.ccf.CurrentCostForwarder.class.getName();

    /** The Constant MIRROR4J_BOOT_JAR. */
    private static final String CURRENT_COST_FORWARDER_BOOT_JAR = "current-cost-forwarder-.*\\.jar";

    /** The Constant FAKE_LOG_LABEL. */
    private static final String FAKE_LOG_LABEL = "0 [main] INFO org.tensin.ccf.boot.CurrentCostForwarder - ";

    /** The Constant LINE_SEPARATOR. */
    private static final String LINE_SEPARATOR = "\n     ";
}
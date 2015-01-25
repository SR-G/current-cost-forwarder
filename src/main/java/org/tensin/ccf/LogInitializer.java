package org.tensin.ccf;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * The Class LogInitializer.
 */
public class LogInitializer {

    /**
     * Change logger level.
     *
     * @param module
     *            the module
     * @param level
     *            the level
     */
    public static void changeLoggerLevel(final String module, final Level level) {
        final String moduleRenamed = module.replaceAll("/", ".");
        final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.AbstractConfiguration configuration = (org.apache.logging.log4j.core.config.AbstractConfiguration) ctx
                .getConfiguration();
        if (configuration.getLogger(moduleRenamed) != null) {
            final LoggerConfig loggerConfig = configuration.getLoggerConfig(moduleRenamed);
            loggerConfig.setLevel(level);
        } else {
            final LoggerConfig loggerConfig = new LoggerConfig(moduleRenamed, level, true);
            configuration.addLogger(moduleRenamed, loggerConfig);
        }
        ctx.updateLoggers(configuration);
    }

    /**
     * Sets the debug.
     *
     * @param debug
     *            the new debug
     */
    public static void setDebug(final boolean debug) {
        if (debug) {
            changeLoggerLevel("org.tensin", Level.DEBUG);
        } else {
            changeLoggerLevel("org.tensin", Level.INFO);
        }
    }
}

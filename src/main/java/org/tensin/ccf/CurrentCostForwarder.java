package org.tensin.ccf;

import java.io.File;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.ccf.forwarder.IForwarder;
import org.tensin.ccf.forwarder.console.ForwarderConsole;
import org.tensin.ccf.forwarder.mqtt.ForwarderMQTT;
import org.tensin.ccf.forwarder.mqtt.MQTTBrokerDefinition;
import org.tensin.ccf.reader.CurrentCostReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Joiner;

/**
 * The Class CurrentCostForwarder.
 */
public class CurrentCostForwarder {

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws Exception
     *             the exception
     */
    public static void main(final String[] args) {
        try {
            final CurrentCostForwarder starter = new CurrentCostForwarder();
            starter.parseArguments(args);
            starter.initPid();
            starter.start();
        } catch (final Exception e) {
            LOGGER.error("Internal current cost forwarder error", e);
            System.exit(1);
        }
    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The Constant DEFAULT_PID_FILENAME. */
    private static final String DEFAULT_PID_FILENAME = "current-cost-forwarder.pid";

    /** The Constant DEFAULT_BROKER_DATA_DIR. */
    private static final String DEFAULT_BROKER_DATA_DIR = "/var/tmp/";

    /** The reconnection timeout. */
    @Parameter(names = { "--reconnection-timeout" }, description = "When expected device is not found (or was found previously but not anymore), we'll wait this timeout before trying to reconnect. In milliseconds.", required = false)
    private final int reconnectionTimeout = CurrentCostReader.DEFAULT_DEVICE_RECONNECTION_TIMEOUT;

    /** The debug. */
    @Parameter(names = "--debug", description = "Debug mode", required = false)
    private boolean debug;

    /** The usage. */
    @Parameter(names = { "-h", "--usage", "--help" }, description = "Shows available commands", required = false)
    private boolean usage;

    /** The device name. */
    @Parameter(names = { "--device", "-d" }, description = "Device name to use, e.g., /dev/ttyUSB0. If not provided, the first /dev/ttyUSB* will be used", required = false)
    private String deviceName;

    /** The pid file name. */
    @Parameter(names = { "--pid" }, description = "The PID filename. Default is current directory, file current-cost-forwarder.pid", required = false)
    private String pidFileName = DEFAULT_PID_FILENAME;

    /** The broker topic. */
    @Parameter(names = { "--broker-topic" }, description = "The broker topic to publish on", required = true)
    private String brokerTopic;

    /** The broker url. */
    @Parameter(names = { "--broker-url" }, description = "The MQTT broker URL to publish on", required = true)
    private String brokerUrl;

    /** The broker auth. */
    @Parameter(names = { "--broker-auth" }, description = "Is the broker auth (true|false)", required = false)
    private boolean brokerAuth;

    /** The broker username. */
    @Parameter(names = { "--broker-username" }, description = "The MQTT broker username (if authed)", required = false)
    private String brokerUsername;

    /** The broker password. */
    @Parameter(names = { "--broker-password" }, description = "The MQTT broker password (if authed)", required = false)
    private String brokerPassword;

    /** The broker data dir. */
    @Parameter(names = { "--broker-data-dir" }, description = "The MQTT broker data dir (for lock files)", required = false)
    private String brokerDataDir = DEFAULT_BROKER_DATA_DIR;

    /** The forwarders. */
    private final Collection<IForwarder> forwarders = new ArrayList<IForwarder>();

    /** The reader. */
    private CurrentCostReader reader;

    /**
     * Activate forwarder console.
     *
     * @throws CCFException
     */
    private void activateForwarderConsole() throws CCFException {
        final ForwarderConsole forwarderConsole = new ForwarderConsole();
        forwarderConsole.start();
        forwarders.add(forwarderConsole);
    }

    /**
     * Activate forwarder mqtt.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void activateForwarderMQTT() throws CCFException {
        final ForwarderMQTT forwarderMQTT = new ForwarderMQTT();
        final MQTTBrokerDefinition mqttBrokerDefinition = MQTTBrokerDefinition.Builder.build();
        mqttBrokerDefinition.setBrokerAuth(isBrokerAuth());
        mqttBrokerDefinition.setBrokerPassword(getBrokerPassword());
        mqttBrokerDefinition.setBrokerUrl(getBrokerUrl());
        mqttBrokerDefinition.setBrokerUsername(getBrokerUsername());

        forwarderMQTT.setBrokerTopic(brokerTopic);
        forwarderMQTT.setBrokerDataDir(brokerDataDir);
        forwarderMQTT.setMqttBrokerDefinition(mqttBrokerDefinition);
        forwarderMQTT.start();
        forwarders.add(forwarderMQTT);
    }

    /**
     * Activate forwarder.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void activateForwarders() throws CCFException {
        if (isDebug()) {
            activateForwarderConsole();
        }
        activateForwarderMQTT();
        dumpActivatedForwarders();
    }

    /**
     * Activate reader.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void activateReader() throws CCFException {
        LOGGER.info("Now starting reader");
        reader = new CurrentCostReader(forwarders);
        if (StringUtils.isEmpty(deviceName)) {
            deviceName = reader.detectDevice();
        } else {
            LOGGER.info("Using provided device name [" + deviceName + "]");
        }
        reader.setDeviceName(deviceName);
        reader.setReconnectionTimeout(reconnectionTimeout);
        reader.start();
    }

    /**
     * Dump activated forwarders.
     */
    private void dumpActivatedForwarders() {
        final Collection<String> names = new TreeSet<String>();
        for (final IForwarder forwarder : forwarders) {
            names.add(forwarder.type());
        }
        LOGGER.info("Activated forwarders are [" + Joiner.on(", ").join(names) + "]");
    }

    /**
     * Gets the broker data dir.
     *
     * @return the broker data dir
     */
    public String getBrokerDataDir() {
        return brokerDataDir;
    }

    /**
     * Gets the broker password.
     *
     * @return the broker password
     */
    public String getBrokerPassword() {
        return brokerPassword;
    }

    /**
     * Gets the broker topic.
     *
     * @return the broker topic
     */
    public String getBrokerTopic() {
        return brokerTopic;
    }

    /**
     * Gets the broker url.
     *
     * @return the broker url
     */
    public String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * Gets the broker username.
     *
     * @return the broker username
     */
    public String getBrokerUsername() {
        return brokerUsername;
    }

    /**
     * Gets the device name.
     *
     * @return the device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Gets the pid file name.
     *
     * @return the pid file name
     */
    public String getPidFileName() {
        return pidFileName;
    }

    /**
     * Inits the pid.
     *
     * @throws CCFException
     *             the mirror4 j exception
     */
    private void initPid() throws CCFException {
        if (StringUtils.isNotEmpty(pidFileName)) {
            int pid = 0;
            try {
                final RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
                final Field jvm = runtime.getClass().getDeclaredField("jvm");
                jvm.setAccessible(true);
                final sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
                final Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
                pid_method.setAccessible(true);

                pid = (Integer) pid_method.invoke(mgmt);
                LOGGER.info("Writing retrieved PID [" + pid + "] in PID file [" + pidFileName + "]");
                FileUtils.writeStringToFile(new File(pidFileName), String.valueOf(pid));
            } catch (NoSuchFieldException e) {
                throw new CCFException("Can't retrieve PID", e);
            } catch (SecurityException e) {
                throw new CCFException("Can't retrieve PID", e);
            } catch (IllegalAccessException e) {
                throw new CCFException("Can't retrieve PID", e);
            } catch (IllegalArgumentException e) {
                throw new CCFException("Can't retrieve PID", e);
            } catch (InvocationTargetException e) {
                throw new CCFException("Can't retrieve PID", e);
            } catch (NoSuchMethodException e) {
                throw new CCFException("Can't retrieve PID", e);
            } catch (IOException e) {
                throw new CCFException("Can't store PID [" + pid + "] in file [" + pidFileName + "]", e);
            }
        } else {
            LOGGER.debug("PID file name is empty, won't store PID");
        }
    }

    /**
     * Checks if is broker auth.
     *
     * @return true, if is broker auth
     */
    public boolean isBrokerAuth() {
        return brokerAuth;
    }

    /**
     * Checks if is debug.
     *
     * @return true, if is debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Parses the arguments.
     *
     * @param args
     *            the args
     * @return the j commander
     */
    public JCommander parseArguments(final String[] args) {
        JCommander jCommander = null;
        try {
            jCommander = new JCommander(this, args);
        } catch (final ParameterException e) {
            LOGGER.error("Unrecognized options : " + e.getMessage());
            jCommander = new JCommander(this);
            usage(jCommander);
        }
        if (usage) {
            usage(jCommander);
        }
        if (debug) {
            LOGGER.info("Debug activated");
        }
        return jCommander;
    }

    /**
     * Sets the broker auth.
     *
     * @param brokerAuth
     *            the new broker auth
     */
    public void setBrokerAuth(final boolean brokerAuth) {
        this.brokerAuth = brokerAuth;
    }

    /**
     * Sets the broker data dir.
     *
     * @param brokerDataDir
     *            the new broker data dir
     */
    public void setBrokerDataDir(final String brokerDataDir) {
        this.brokerDataDir = brokerDataDir;
    }

    /**
     * Sets the broker password.
     *
     * @param brokerPassword
     *            the new broker password
     */
    public void setBrokerPassword(final String brokerPassword) {
        this.brokerPassword = brokerPassword;
    }

    /**
     * Sets the broker topic.
     *
     * @param brokerTopic
     *            the new broker topic
     */
    public void setBrokerTopic(final String brokerTopic) {
        this.brokerTopic = brokerTopic;
    }

    /**
     * Sets the broker url.
     *
     * @param brokerUrl
     *            the new broker url
     */
    public void setBrokerUrl(final String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * Sets the broker username.
     *
     * @param brokerUsername
     *            the new broker username
     */
    public void setBrokerUsername(final String brokerUsername) {
        this.brokerUsername = brokerUsername;
    }

    /**
     * Sets the debug.
     *
     * @param debug
     *            the new debug
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    /**
     * Sets the device name.
     *
     * @param deviceName
     *            the new device name
     */
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Sets the pid file name.
     *
     * @param pidFileName
     *            the new pid file name
     */
    public void setPidFileName(final String pidFileName) {
        this.pidFileName = pidFileName;
    }

    /**
     * Start.
     *
     * @throws CCFException
     *             the mirror exception
     */
    private void start() throws CCFException {
        LogInitializer.setDebug(debug);
        final long start = System.currentTimeMillis();
        LOGGER.info("Now starting CurrentCostForwarder");
        activateForwarders();
        activateReader();
        LOGGER.info("CurrentCostForwarder started in [" + (System.currentTimeMillis() - start) + "ms]");
    }

    /**
     * Stop.
     *
     * @throws CCFException
     *             the CCF exception
     */
    public void stop() throws CCFException {
        LOGGER.info("Now stopping CurrentCostForwarder");
        stopReader();
        stopFrowarder();
    }

    /**
     * Stop frowarder.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void stopFrowarder() throws CCFException {
        for (final IForwarder forwarder : forwarders) {
            forwarder.stop();
        }
    }

    /**
     * Stop reader.
     */
    private void stopReader() {
        if (reader != null) {
            reader.setActive(false);
        }
    }

    /**
     * Usage.
     *
     * @param jCommander
     *            the j commander
     */
    private void usage(final JCommander jCommander) {
        final StringBuilder sb = new StringBuilder();
        jCommander.usage(sb);
        System.out.println(sb.toString());
        System.exit(0);
    }
}
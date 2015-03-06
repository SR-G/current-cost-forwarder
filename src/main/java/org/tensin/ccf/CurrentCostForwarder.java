package org.tensin.ccf;

import java.io.File;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.ccf.forwarder.ForwarderService;
import org.tensin.ccf.forwarder.IForwarder;
import org.tensin.ccf.forwarder.console.ForwarderConsole;
import org.tensin.ccf.forwarder.mqtt.ForwarderMQTT;
import org.tensin.ccf.forwarder.mqtt.MQTTBrokerDefinition;
import org.tensin.ccf.reader.CurrentCostReader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ServiceManager;

/**
 * The Class CurrentCostForwarder.
 */
public class CurrentCostForwarder {

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        Thread.currentThread().setName(Constants.THREAD_NAME + "-MAIN");
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

    /** The Constant DEFAULT_START_STOP_TIMEOUT_IN_SECONDS. */
    private static final CCFTimeUnit DEFAULT_START_STOP_TIMEOUT = CCFTimeUnit.parseTime("60s");

    /** The Constant DEFAULT_BROKER_RECONNECT_TIMEOUT. */
    private static final CCFTimeUnit DEFAULT_BROKER_RECONNECT_TIMEOUT = CCFTimeUnit.parseTime("5s");

    /** The reconnection timeout. */
    @Parameter(names = { "--device-reconnect-timeout" }, description = "When expected device is not found (or was found previously but not anymore), we'll wait this timeout before trying to reconnect. Example values : '2s', '500ms', aso", required = false)
    private final CCFTimeUnit deviceReconnectTimeout = CurrentCostReader.DEFAULT_DEVICE_RECONNECTION_TIMEOUT;

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
    private String brokerTopic = "/metrics/current-cost/";

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

    /** The broker data dir. */
    @Parameter(names = { "--broker-reconnect-timeout" }, description = "The timeout between each reconnect on the broker. Example values : '30s', '1m', '500ms', aso", required = false)
    private CCFTimeUnit brokerReconnectTimeout = DEFAULT_BROKER_RECONNECT_TIMEOUT;

    /** The start stop timeout. */
    @Parameter(names = { "--timeout" }, description = "Start/stop timeout. Example values : '30s', '1m', '500ms', aso", required = false)
    private CCFTimeUnit startStopTimeout = DEFAULT_START_STOP_TIMEOUT;

    /** The reader. */
    private CurrentCostReader reader; // TODO(serge) switch as a service

    /** The forwarder service. */
    private ForwarderService forwarderService;

    /** The service manager. */
    private ServiceManager serviceManager;

    /**
     * Activate forwarder.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void activateForwarders() throws CCFException {
        final Collection<IForwarder> forwarders = new ArrayList<IForwarder>();

        if (isDebug()) {
            forwarders.add(ForwarderConsole.build());
        }

        final MQTTBrokerDefinition mqttBrokerDefinition = MQTTBrokerDefinition.Builder.build();
        mqttBrokerDefinition.setBrokerAuth(isBrokerAuth());
        mqttBrokerDefinition.setBrokerPassword(getBrokerPassword());
        mqttBrokerDefinition.setBrokerUrl(getBrokerUrl());
        mqttBrokerDefinition.setBrokerUsername(getBrokerUsername());

        forwarders.add(ForwarderMQTT.build(mqttBrokerDefinition, brokerTopic, brokerDataDir, brokerReconnectTimeout));
        forwarderService = ForwarderService.build(forwarders);
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
     * Gets the broker reconnect timeout.
     *
     * @return the broker reconnect timeout
     */
    public CCFTimeUnit getBrokerReconnectTimeout() {
        return brokerReconnectTimeout;
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
     * Gets the start stop timeout.
     *
     * @return the start stop timeout
     */
    public CCFTimeUnit getStartStopTimeout() {
        return startStopTimeout;
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
            if (!usage) {
                LOGGER.error(e.getMessage());
            }
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
     * Sets the broker reconnect timeout.
     *
     * @param brokerReconnectTimeout
     *            the new broker reconnect timeout
     */
    public void setBrokerReconnectTimeout(final CCFTimeUnit brokerReconnectTimeout) {
        this.brokerReconnectTimeout = brokerReconnectTimeout;
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
     * Sets the start stop timeout.
     *
     * @param startStopTimeout
     *            the new start stop timeout
     */
    public void setStartStopTimeout(final CCFTimeUnit startStopTimeout) {
        this.startStopTimeout = startStopTimeout;
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
        startReader();
        startServices();
        LOGGER.info("CurrentCostForwarder started in [" + (System.currentTimeMillis() - start) + "ms]");
    }

    /**
     * Activate reader.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void startReader() throws CCFException {
        LOGGER.info("Now starting reader");
        reader = new CurrentCostReader(forwarderService);
        if (StringUtils.isEmpty(deviceName)) {
            deviceName = reader.detectDevice();
        } else {
            LOGGER.info("Using provided device name [" + deviceName + "]");
        }
        reader.setDeviceName(deviceName);
        reader.setReconnectionTimeout(deviceReconnectTimeout);
        reader.start();
    }

    /**
     * Start services.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void startServices() throws CCFException {
        serviceManager = new ServiceManager(ImmutableList.of(forwarderService));
        serviceManager.startAsync();
        // try {
        // serviceManager.awaitHealthy(startStopTimeout, TimeUnit.SECONDS);
        // } catch (TimeoutException e) {
        // throw new CCFException("Can't start services in allowed timeout", e);
        // }
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
        stopServices();
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
     * Stop frowarder.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void stopServices() throws CCFException {
        try {
            serviceManager.stopAsync();
            serviceManager.awaitStopped(startStopTimeout.getDuration(), startStopTimeout.getTimeUnit());
        } catch (TimeoutException e) {
            throw new CCFException("Can't stop services in allowed timeout", e);
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
        JCommanderUsage u = new JCommanderUsage(jCommander);
        u.usage(sb, "");

        // jCommander.setColumnSize(1024);
        // jCommander.usage(sb, "");
        System.out.println(sb.toString());
        System.exit(0);
    }
}
package org.tensin.ccf.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.CCFTimeUnit;
import org.tensin.ccf.Constants;
import org.tensin.ccf.TimeHelper;
import org.tensin.ccf.events.EventHistory;
import org.tensin.ccf.events.EventTemperature;
import org.tensin.ccf.events.EventWatts;
import org.tensin.ccf.forwarder.ForwarderService;
import org.tensin.ccf.model.history.CurrentCostHistoryData;
import org.tensin.ccf.model.history.CurrentCostHistoryDataConverter;
import org.tensin.ccf.model.history.CurrentCostHistoryMessage;
import org.tensin.ccf.model.history.ICurrentCostHistoryItem;
import org.tensin.ccf.model.message.AbstractCurrentCostChannel;
import org.tensin.ccf.model.message.CurrentCostMessage;

/**
 * The Class CurrentCostReader.
 */
public class CurrentCostReader extends Thread {

    /**
     * Builds the.
     *
     * @param forwarderService
     *            the forwarder service
     * @param deviceName
     *            the device name
     * @param deviceReconnectTimeout
     *            the device reconnect timeout
     * @return the current cost reader
     * @throws CCFException
     *             the CCF exception
     */
    public static CurrentCostReader build(final ForwarderService forwarderService, String deviceName, final CCFTimeUnit deviceReconnectTimeout)
            throws CCFException {
        final CurrentCostReader reader = new CurrentCostReader(forwarderService);
        if (StringUtils.isEmpty(deviceName)) {
            deviceName = reader.detectDevice();
        } else {
            LOGGER.info("Using provided device name [" + deviceName + "]");
        }
        reader.deviceName = deviceName;
        reader.reconnectionTimeout = deviceReconnectTimeout;
        reader.serializer = buildSerializer();
        return reader;
    }

    /**
     * Builds the serializer.
     *
     * @return the serializer
     * @throws CCFException
     *             the CCF exception
     */
    public static Serializer buildSerializer() throws CCFException {
        try {
            final Registry registry = new Registry();
            final Strategy strategy = new RegistryStrategy(registry);
            registry.bind(CurrentCostHistoryData.class, CurrentCostHistoryDataConverter.class);

            final Serializer serializer = new Persister(strategy);
            return serializer;
        } catch (Exception e) {
            throw new CCFException("Can't build serializer", e);
        }
    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The device. */
    private String deviceName;

    /** The active. */
    private boolean active;

    /** The show error messages on init. */
    private boolean showErrorMessagesOnInit;

    /** The Constant DEVICE_RECONNECTION_TIMEOUT. */
    public static final CCFTimeUnit DEFAULT_DEVICE_RECONNECTION_TIMEOUT = CCFTimeUnit.parseTime("2s");

    /** The reconnection timeout. */
    private CCFTimeUnit reconnectionTimeout = DEFAULT_DEVICE_RECONNECTION_TIMEOUT;

    /** The forwarders. */
    private final ForwarderService forwarderService;

    /** The serializer. */
    private Serializer serializer;

    /**
     * Instantiates a new current cost reader.
     *
     * @param forwarderService
     *            the forwarders
     */
    private CurrentCostReader(final ForwarderService forwarderService) {
        this.forwarderService = forwarderService;
    }

    /**
     * Builds the event history.
     *
     * @param m
     *            the m
     * @return the collection
     */
    public Collection<EventHistory> buildEventHistory(final CurrentCostHistoryMessage m) {
        final Collection<EventHistory> results = new ArrayList<EventHistory>();
        for (final CurrentCostHistoryData data : m.getHistory().getData()) {
            // At this time we only forward the first (most recent) value
            for (final ICurrentCostHistoryItem current : data.getItems()) {
                if (current.isNotEmpty()) {
                    final EventHistory e = new EventHistory(data.getSensor(), current.getType(), current.getSeed(), current.getValue());
                    results.add(e);
                }
            }
        }
        return results;
    }

    /**
     * Builds the event temperature.
     *
     * @param m
     *            the m
     * @return the double
     */
    public EventTemperature buildEventTemperature(final CurrentCostMessage m) {
        final EventTemperature result = new EventTemperature(m.getSensor(), m.getId(), m.getTemperature());
        return result;
    }

    /**
     * Builds the event watts.
     *
     * @param m
     *            the m
     * @return the event watts
     */
    public Collection<EventWatts> buildEventWatts(final CurrentCostMessage m) {
        final Collection<EventWatts> results = new ArrayList<EventWatts>();
        for (final AbstractCurrentCostChannel channel : m.getChannels()) {
            final int watts = channel.getWatts();
            final String channelId = channel.getChannel();
            final EventWatts result = new EventWatts(m.getSensor(), m.getId(), channelId, watts);
            results.add(result);
        }
        return results;
    }

    /**
     * Decode as hist message.
     *
     * @param xml
     *            the xml
     * @return true, if successful
     */
    private boolean decodeAndForwardAsHistMessage(final String xml) {
        try {
            final CurrentCostHistoryMessage m = serializer.read(CurrentCostHistoryMessage.class, xml);
            if (m != null) {
                for (final EventHistory h : buildEventHistory(m)) {
                    forwardHistory(h);
                }
            }
            return true;
        } catch (CCFException e) {
            LOGGER.error("Can'forward hist message", e);
            return false;
        } catch (Exception e) {
            LOGGER.error("Can't deserialize xml as hist message [" + xml + "] : " + e.getMessage());
            return false;
        }
    }

    /**
     * Decode as raw message.
     *
     * @param xml
     *            the xml
     * @return true, if successful
     */
    private boolean decodeAndForwardAsRawMessage(final String xml) {
        try {
            final CurrentCostMessage m = serializer.read(CurrentCostMessage.class, xml);
            if (m != null) {
                forwardTemperature(buildEventTemperature(m));
                for (final EventWatts e : buildEventWatts(m)) {
                    forwardWatts(e);
                }
            }
            return true;
        } catch (CCFException e) {
            LOGGER.error("Can'forward raw message", e);
            return false;
        } catch (Exception e) {
            LOGGER.debug("Can't deserialize xml as raw message [" + xml + "] : " + e.getMessage());
            return false;
        }
    }

    /**
     * Detect device.
     *
     * @return the string
     * @throws CCFException
     *             the CCF exception
     */
    public String detectDevice() throws CCFException {
        LOGGER.info("Trying to autodect EnviR device in [" + Constants.DEFAULT_DEVICE_PATH + "] with pattern [" + Constants.DEFAULT_DEVICE_PATTERN + "]");
        final IOFileFilter nameFilter = new RegexFileFilter(Constants.DEFAULT_DEVICE_PATTERN);
        final Collection<File> items = FileUtils.listFiles(new File(Constants.DEFAULT_DEVICE_PATH), nameFilter, TrueFileFilter.TRUE);

        if (items.size() > 0) {
            for (final File f : items) {
                final String name = f.getAbsolutePath();
                LOGGER.info("Auto-detected current cost device [" + name + "]");
                return name;
            }
        }
        LOGGER.info("No auto-detect current cost device, will use default name [" + Constants.DEFAULT_DEVICE_NAME + "]");
        return Constants.DEFAULT_DEVICE_NAME;
    }

    /**
     * Forward history.
     *
     * @param eventHistory
     *            the event history
     * @throws CCFException
     *             the CCF exception
     */
    private void forwardHistory(final EventHistory eventHistory) throws CCFException {
        forwarderService.enqueue(eventHistory);
    }

    /**
     * Forward temperature.
     *
     * @param eventTemperature
     *            the event temperature
     * @throws CCFException
     *             the CCF exception
     */
    private void forwardTemperature(final EventTemperature eventTemperature) throws CCFException {
        forwarderService.enqueue(eventTemperature);
    }

    /**
     * Forward watts.
     *
     * @param eventWatts
     *            the event watts
     * @throws CCFException
     *             the CCF exception
     */
    private void forwardWatts(final EventWatts eventWatts) throws CCFException {
        forwarderService.enqueue(eventWatts);
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
     * Gets the reconnection timeout.
     *
     * @return the reconnection timeout
     */
    public CCFTimeUnit getReconnectionTimeout() {
        return reconnectionTimeout;
    }

    /**
     * Inits the device.
     *
     * @param currentDeviceName
     *            the current device name
     * @return the file input stream
     */
    public FileInputStream initFileInputStreamDevice(final String currentDeviceName) {
        final File device = new File(currentDeviceName);
        FileInputStream fis = null;
        if (!device.exists()) {
            if (showErrorMessagesOnInit) {
                LOGGER.error("Specified device not found [" + currentDeviceName + "]");
                showErrorMessagesOnInit = false;
            }
        } else if (!device.canRead()) {
            if (showErrorMessagesOnInit) {
                LOGGER.error("Specified device can't be read [" + currentDeviceName + "], please check permissions (e.g., 'sudo chmod a+r " + currentDeviceName
                        + "')");
                showErrorMessagesOnInit = false;
            }
        } else {
            try {
                fis = new FileInputStream(device);
                LOGGER.info("Now connected on specified device [" + currentDeviceName + "]");
            } catch (final IOException e) {
                LOGGER.error("Error while opening device [" + currentDeviceName + "]", e);
                showErrorMessagesOnInit = true;
                fis = null;
            }
        }
        return fis;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Checks if is trame ended.
     *
     * @param r
     *            the r
     * @param sb
     *            the sb
     * @return true, if is trame ended
     */
    private boolean isTrameEnded(final int r, final StringBuilder sb) {
        // Old current cost send 13 as line ending
        if (r == 13) {
            return true;
        }
        // new ones seems to send 10 as shown under http://pastebin.com/Qy10NNJc
        if (r == 10) {
            return true;
        }
        // if (sb.toString().endsWith("</msg>")) {
        // return true;
        // }
        return false;
    }

    /**
     * Process.
     *
     * @param xml
     *            the xml
     */
    private void process(final String xml) {
        // New current cost may send 2 EoL characters, so we don't want to process empty string
        if (StringUtils.isNotEmpty(xml)) {
            LOGGER.debug("Now processing XML [" + xml + "]");

            // We can receive different XML structures, so we are
            // trying first the most common one (raw values) and if it fails
            // we are trying the history decoding
            // A better way to proceed would be to try to detect what the messages are before deserializing
            if (xml.contains("<hist>")) {
                decodeAndForwardAsHistMessage(xml);
            } else {
                decodeAndForwardAsRawMessage(xml);
            }
        }
    }

    /**
     * Read.
     *
     * @param fis
     *            the fis
     */
    private void read(final FileInputStream fis) {
        LOGGER.debug("Now starting acquisition from device");
        try {
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = fis.read()) != -1) {
                LOGGER.trace("Received character [" + (char) r + "] (" + r + ")");
                if (isTrameEnded(r, sb)) {
                    process(sb.toString());
                    sb = new StringBuilder();
                } else {
                    sb.append((char) r);
                }
            }
        } catch (final IOException e) {
            LOGGER.error("I/O Error while reading device [" + deviceName + "]", e);
        } catch (final Exception e) {
            LOGGER.error("Error while reading device [" + deviceName + "]", e);
        } finally {
            IOUtils.closeQuietly(fis);
            showErrorMessagesOnInit = true;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    /**
     * {@inheritDoc}
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        super.run();
        FileInputStream fis = null;
        showErrorMessagesOnInit = true;
        // Reconnection mechanism
        while (active) {
            try {
                if (fis != null) {
                    read(fis);
                    fis = null; // important (the read() method is a continuous loop, will only exit if/when there is a problem (device unplugged > I/O exception
                    // internally catched into read())
                } else {
                    fis = initFileInputStreamDevice(deviceName);
                    if (fis == null) {
                        TimeHelper.wait(getReconnectionTimeout());
                    }
                }
            } catch (final Throwable t) {
                LOGGER.error("Unexpected exception while opening device or reading data", t);
            }
        }
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        active = false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        setName(Constants.THREAD_NAME + "-READER");

        // final ByteOrder byteOrder = java.nio.ByteOrder.nativeOrder();
        // LOGGER.info("Detected Byte Order [" + byteOrder.toString() + "]");

        LOGGER.info("Starting CurrentCostForwarder reader thread on device [" + deviceName + "]");
        active = true;
        super.start();
    }
}

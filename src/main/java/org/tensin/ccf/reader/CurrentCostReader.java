package org.tensin.ccf.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.VisitorStrategy;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.Constants;
import org.tensin.ccf.events.EventTemperature;
import org.tensin.ccf.events.EventWatts;
import org.tensin.ccf.forwarder.IForwarder;
import org.tensin.ccf.model.CurrentCostVisitor;
import org.tensin.ccf.model.history.CurrentCostHistoryMessage;
import org.tensin.ccf.model.message.CurrentCostMessage;

/**
 * The Class CurrentCostReader.
 */
public class CurrentCostReader extends Thread {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The device. */
    private String deviceName;

    /** The active. */
    private boolean active;

    /** The show error messages on init. */
    private boolean showErrorMessagesOnInit;

    /** The Constant DEVICE_RECONNECTION_TIMEOUT. */
    public static final int DEFAULT_DEVICE_RECONNECTION_TIMEOUT = 2000;

    /** The reconnection timeout. */
    private int reconnectionTimeout = DEFAULT_DEVICE_RECONNECTION_TIMEOUT;

    /** The forwarders. */
    private final Collection<IForwarder> forwarders;

    /** The serializer. */
    private Serializer serializer;

    /**
     * Instantiates a new current cost reader.
     *
     * @param forwarders
     *            the forwarders
     */
    public CurrentCostReader(final Collection<IForwarder> forwarders) {
        this.forwarders = forwarders;
    }

    /**
     * Decode as hist message.
     *
     * @param xml
     *            the xml
     * @return true, if successful
     */
    private boolean decodeAsHistMessage(final String xml) {
        try {
            final CurrentCostHistoryMessage m = serializer.read(CurrentCostHistoryMessage.class, xml);
            if (m != null) {
                // Nothing to do for the now
            }
            return true;
        } catch (Exception e) {
            LOGGER.debug("Can't deserialize xml as hist message [" + xml + "]");
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
    private boolean decodeAsRawMessage(final String xml) {
        try {
            final CurrentCostMessage m = serializer.read(CurrentCostMessage.class, xml);
            if (m != null) {
                forwardTemperature(m.getTemperature());
                forwardWatts(m.getChannels().iterator().next().getWatts());
            }
            return true;
        } catch (Exception e) {
            LOGGER.debug("Can't deserialize xml as raw message [" + xml + "]");
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
        LOGGER.info("Trying to autodect mirror4j device in [" + Constants.DEFAULT_DEVICE_PATH + "] with pattern [" + Constants.DEFAULT_DEVICE_PATTERN + "]");
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
     * Forward temperature.
     *
     * @param temperature
     *            the temperature
     * @throws CCFException
     *             the CCF exception
     */
    private void forwardTemperature(final double temperature) throws CCFException {
        for (final IForwarder forwarder : forwarders) {
            forwarder.forward(new EventTemperature(temperature));
        }
    }

    /**
     * Forward watts.
     *
     * @param watts
     *            the watts
     * @throws CCFException
     *             the CCF exception
     */
    private void forwardWatts(final int watts) throws CCFException {
        for (final IForwarder forwarder : forwarders) {
            forwarder.forward(new EventWatts(watts));
        }
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
    public int getReconnectionTimeout() {
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
     * Process.
     *
     * @param xml
     *            the xml
     */
    private void process(final String xml) {
        LOGGER.debug("Now processing XML [" + xml + "]");

        // We can receive different XML structures, so we are
        // trying first the most common one (raw values) and if it fails
        // we are trying the history decoding
        // A better way to proceed would be to try to detect what the messages are before deserializing
        if (!decodeAsRawMessage(xml)) {
            if (!decodeAsHistMessage(xml)) {
                LOGGER.error("Can't decode XML [" + xml + "] (neither raw nor history messages)");
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
        try {
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = fis.read()) != -1) {
                if (r == 13) {
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
                        sleepAFewMilliseconds(getReconnectionTimeout());
                    }
                }
            } catch (final Throwable t) {
                LOGGER.error("Unexpected exception while opening device or reading data", t);
            }
        }
    }

    /**
     * Sets the active.
     *
     * @param active
     *            the new active
     */
    public void setActive(final boolean active) {
        this.active = active;
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
     * Sets the reconnection timeout.
     *
     * @param reconnectionTimeout
     *            the new reconnection timeout
     */
    public void setReconnectionTimeout(final int reconnectionTimeout) {
        this.reconnectionTimeout = reconnectionTimeout;
    }

    /**
     * Sleep a few milliseconds.
     *
     * @param ms
     *            the ms
     */
    private void sleepAFewMilliseconds(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
        }
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

        final Strategy strategy = new VisitorStrategy(new CurrentCostVisitor());
        serializer = new Persister(strategy);

        LOGGER.info("Starting CurrentCostForwarder reader thread on device [" + deviceName + "]");
        active = true;
        super.start();
    }
}

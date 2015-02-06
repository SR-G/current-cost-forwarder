package org.tensin.ccf.forwarder.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.events.IEvent;
import org.tensin.ccf.forwarder.IForwarder;

/**
 * The Class ForwarderConsole.
 */
public class ForwarderConsole implements IForwarder {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#forward(org.tensin.ccf.events.IEvent)
     */
    @Override
    public void forward(final IEvent event) throws CCFException {
        LOGGER.info("Recevied event [" + event.toString() + "]");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#start()
     */
    @Override
    public void start() throws CCFException {
        LOGGER.info("Starting ForwarderConsole");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#stop()
     */
    @Override
    public void stop() throws CCFException {
        LOGGER.info("Stopping ForwarderConsole");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.ccf.forwarder.IForwarder#type()
     */
    @Override
    public String type() {
        return "FORWARDER-CONSOLE";
    }
}
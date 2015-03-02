package org.tensin.ccf.forwarder;

import org.tensin.ccf.CCFException;
import org.tensin.ccf.events.IEvent;

/**
 * The Interface IForwarder.
 */
public interface IForwarder {

    /**
     * Forward.
     *
     * @param event
     *            the event
     * @throws CCFException
     *             the CCF exception
     */
    void forward(final IEvent event) throws CCFException;

    /**
     * Nb threads.
     *
     * @return the int
     */
    int nbThreads();

    /**
     * Start.
     *
     * @throws CCFException
     *             the CCF exception
     */
    void start() throws CCFException;

    /**
     * Stop.
     *
     * @throws CCFException
     *             the CCF exception
     */
    void stop() throws CCFException;

    /**
     * Type.
     *
     * @return the string
     */
    String type();
}

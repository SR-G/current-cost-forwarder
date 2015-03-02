package org.tensin.ccf.forwarder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.Constants;
import org.tensin.ccf.events.IEvent;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.AbstractIdleService;

/**
 * The Class ForwarderService.
 *
 * Forwarders are separated and threaded : this would allow to have one forwarder still working (let's say console one for debugging purpose) while the other forwarder is stuck somewhere.
 */
public class ForwarderService extends AbstractIdleService {

    /**
     * Builds the.
     *
     * @param forwarders
     *            the forwarders
     * @return the forwarder service
     */
    public static ForwarderService build(final Collection<IForwarder> forwarders) {
        final ForwarderService result = new ForwarderService();
        result.setForwarders(forwarders);
        return result;
    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The forwarders. */
    private Collection<IForwarder> forwarders;

    private final Map<String, ExecutorService> executors = new HashMap<String, ExecutorService>();

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
     * Enqueue.
     *
     * @param event
     *            the event
     * @throws CCFException
     */
    public void enqueue(final IEvent event) throws CCFException {
        for (final IForwarder forwarder : forwarders) {
            final ExecutorService executorService = executors.get(forwarder.type());
            executorService.submit(new Runnable() {

                /**
                 * Run.
                 */
                @Override
                public void run() {
                    try {
                        forwarder.forward(event);
                    } catch (CCFException e) {
                        LOGGER.error("Can't forward event to forwarder [" + forwarder.type() + "]", e);
                    }
                }
            });
        }
    }

    /**
     * Gets the forwarders.
     *
     * @return the forwarders
     */
    public Collection<IForwarder> getForwarders() {
        return forwarders;
    }

    /**
     * Sets the forwarders.
     *
     * @param forwarders
     *            the new forwarders
     */
    public void setForwarders(final Collection<IForwarder> forwarders) {
        this.forwarders = forwarders;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.google.common.util.concurrent.AbstractIdleService#shutDown()
     */
    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down ForwarderService");
        for (final IForwarder forwarder : forwarders) {
            final ExecutorService executorService = executors.get(forwarder.type());
            if ((executorService != null) && !executorService.isShutdown() && !executorService.isTerminated()) {
                executorService.shutdown();
            }
        }
        for (final IForwarder forwarder : forwarders) {
            forwarder.stop();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see com.google.common.util.concurrent.AbstractIdleService#startUp()
     */
    @Override
    protected void startUp() throws Exception {
        Thread.currentThread().setName(Constants.THREAD_NAME + "-FORWARDERS");
        for (final IForwarder forwarder : forwarders) {
            forwarder.start();
        }
        for (final IForwarder forwarder : forwarders) {
            final ExecutorService executorService = Executors.newFixedThreadPool(forwarder.nbThreads(), new ThreadFactory() {

                @Override
                public Thread newThread(final Runnable r) {
                    final Thread t = new Thread(r);
                    t.setName(Constants.THREAD_NAME + "-FORWARDER-" + forwarder.type().toUpperCase());
                    return t;
                }

            });
            executors.put(forwarder.type(), executorService);
        }

        dumpActivatedForwarders();
    }
}
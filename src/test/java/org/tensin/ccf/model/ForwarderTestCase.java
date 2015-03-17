package org.tensin.ccf.model;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.CCFTimeUnit;
import org.tensin.ccf.events.EventHistory;
import org.tensin.ccf.forwarder.ForwarderService;
import org.tensin.ccf.forwarder.IForwarder;
import org.tensin.ccf.forwarder.console.ForwarderConsole;
import org.tensin.ccf.model.history.CurrentCostHistoryMessage;
import org.tensin.ccf.reader.CurrentCostReader;

import com.google.common.collect.ImmutableList;

/**
 * The Class ForwarderTestCase.
 */
public class ForwarderTestCase extends AbstractReaderTestCase {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Test current cost message history.
     *
     * @param filename
     *            the filename
     * @throws CCFException
     *             the CCF exception
     */
    public void testCurrentCostMessageHistory(final String filename) throws CCFException {
        final IForwarder forwarderConsole = new ForwarderConsole();
        final ForwarderService service = ForwarderService.build(ImmutableList.of(forwarderConsole));
        try {
            final CurrentCostHistoryMessage m = CurrentCostReader.buildSerializer().read(CurrentCostHistoryMessage.class, new File(filename));
            service.startAsync();
            service.awaitRunning();
            final CurrentCostReader reader = CurrentCostReader.build(service, "/dev/ttyUBS0", CCFTimeUnit.parseTime("30s"));
            for (final EventHistory e : reader.buildEventHistory(m)) {
                final String topic = e.enhanceTopicWithInternalValues("metrics/current-cost/${sensor}/history/${type}/${seed}");
                LOGGER.info("Destination topic would be [" + topic + "]");
                service.enqueue(e);
            }
        } catch (Exception e) {
            throw new CCFException(e);
        } finally {
            service.stopAsync();
            service.awaitTerminated();
        }
    }

    /**
     * Test current cost message history daily.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessageHistoryDaily() throws CCFException {
        testCurrentCostMessageHistory("src/test/java/org/tensin/ccf/model/message-hist-daily.xml");
    }

    /**
     * Test current cost message history hourly.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessageHistoryHourly() throws CCFException {
        testCurrentCostMessageHistory("src/test/java/org/tensin/ccf/model/message-hist-hourly.xml");
    }

    /**
     * Test current cost message.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessageHistoryMonthly() throws CCFException {
        testCurrentCostMessageHistory("src/test/java/org/tensin/ccf/model/message-hist-monthly.xml");
    }
}
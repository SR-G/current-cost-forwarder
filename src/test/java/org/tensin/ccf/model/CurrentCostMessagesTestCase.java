package org.tensin.ccf.model;

import java.io.File;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.VisitorStrategy;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.model.history.CurrentCostHistoryMessage;
import org.tensin.ccf.model.message.CurrentCostMessage;

/**
 * The Class CurrentCostMessagesTestCase.
 */
public class CurrentCostMessagesTestCase {

    /**
     * Builds the serializer.
     *
     * @return the serializer
     */
    private Serializer buildSerializer() {
        final Strategy strategy = new VisitorStrategy(new CurrentCostVisitor());
        final Serializer serializer = new Persister(strategy);
        return serializer;

    }

    /**
     * Test current cost message.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessage() throws CCFException {
        try {
            final CurrentCostMessage m = buildSerializer().read(CurrentCostMessage.class, new File("src/test/java/org/tensin/ccf/model/message-raw.xml"));
            System.out.println(m.toString());
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }

    /**
     * Test current cost message.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessageHistory() throws CCFException {
        try {
            final CurrentCostHistoryMessage m = buildSerializer().read(CurrentCostHistoryMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-hist.xml"));
            System.out.println(m.toString());
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }

    /**
     * Test current cost message.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessageMultipleChannels() throws CCFException {
        try {
            final CurrentCostMessage m = buildSerializer().read(CurrentCostMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-raw-multiple-channels.xml"));
            System.out.println(m.toString());
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }
}
package org.tensin.ccf.model;

import java.io.File;

import org.junit.Test;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.model.history.CurrentCostHistoryMessage;
import org.tensin.ccf.model.message.CurrentCostMessage;
import org.tensin.ccf.reader.CurrentCostReader;

/**
 * The Class CurrentCostMessagesTestCase.
 */
public class CurrentCostMessagesTestCase extends AbstractReaderTestCase {

    /**
     * Test current cost message.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testCurrentCostMessage() throws CCFException {
        try {
            final CurrentCostMessage m = CurrentCostReader.buildSerializer().read(CurrentCostMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-raw.xml"));
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
            final CurrentCostHistoryMessage m = CurrentCostReader.buildSerializer().read(CurrentCostHistoryMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-hist-monthly.xml"));
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
            final CurrentCostMessage m = CurrentCostReader.buildSerializer().read(CurrentCostMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-raw-multiple-channels.xml"));
            System.out.println(m.toString());
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }
}
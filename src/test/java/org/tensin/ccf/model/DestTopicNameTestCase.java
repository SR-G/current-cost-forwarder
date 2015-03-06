package org.tensin.ccf.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.events.EventWatts;
import org.tensin.ccf.model.message.CurrentCostMessage;

/**
 * The Class DestTopicNameTestCase.
 */
public class DestTopicNameTestCase extends AbstractReaderTestCase {

    /**
     * Test topic name.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testTopicName() throws CCFException {
        try {
            final CurrentCostMessage m = buildSerializer().read(CurrentCostMessage.class, new File("src/test/java/org/tensin/ccf/model/message-raw.xml"));
            final EventWatts w = new EventWatts(m.getSensor(), m.getId(), m.getChannels().iterator().next().getWatts());
            Assert.assertEquals("/metrics/current-cost/0/watts", w.enhanceTopicWithInternalValues("/metrics/current-cost/${sensor}/watts"));
            Assert.assertEquals("/metrics/current-cost/00077/watts", w.enhanceTopicWithInternalValues("/metrics/current-cost/${id}/watts"));
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }
}
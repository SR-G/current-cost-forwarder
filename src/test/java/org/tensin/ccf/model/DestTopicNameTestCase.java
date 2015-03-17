package org.tensin.ccf.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.events.EventWatts;
import org.tensin.ccf.model.message.AbstractCurrentCostChannel;
import org.tensin.ccf.model.message.CurrentCostMessage;
import org.tensin.ccf.reader.CurrentCostReader;

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
    public void testTopicNameRawMessage() throws CCFException {
        try {
            final CurrentCostMessage m = CurrentCostReader.buildSerializer().read(CurrentCostMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-raw.xml"));
            final AbstractCurrentCostChannel channel = m.getChannels().iterator().next();
            final EventWatts w = new EventWatts(m.getSensor(), m.getId(), channel.getChannel(), channel.getWatts());
            Assert.assertEquals("metrics/current-cost/0/watts", w.enhanceTopicWithInternalValues("metrics/current-cost/${sensor}/watts"));
            Assert.assertEquals("metrics/current-cost/ch1/watts", w.enhanceTopicWithInternalValues("metrics/current-cost/${channel}/watts"));
            Assert.assertEquals("metrics/current-cost/00077/watts", w.enhanceTopicWithInternalValues("metrics/current-cost/${id}/watts"));
            Assert.assertEquals("metrics/current-cost/00077/ch1/watts", w.enhanceTopicWithInternalValues("metrics/current-cost/${id}/${channel}/watts"));
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }

    /**
     * Test topic name raw multiple channels message.
     *
     * @throws CCFException
     *             the CCF exception
     */
    @Test
    public void testTopicNameRawMultipleChannelsMessage() throws CCFException {
        try {
            final CurrentCostMessage m = CurrentCostReader.buildSerializer().read(CurrentCostMessage.class,
                    new File("src/test/java/org/tensin/ccf/model/message-raw-multiple-channels.xml"));
            int i = 1;
            for (final AbstractCurrentCostChannel channel : m.getChannels()) {
                final EventWatts w = new EventWatts(m.getSensor(), m.getId(), channel.getChannel(), channel.getWatts());
                Assert.assertEquals("metrics/current-cost/0/watts", w.enhanceTopicWithInternalValues("metrics/current-cost/${sensor}/watts"));
                Assert.assertEquals("metrics/current-cost/00077/watts", w.enhanceTopicWithInternalValues("metrics/current-cost/${id}/watts"));

                final String topic = w.enhanceTopicWithInternalValues("metrics/current-cost/${id}/${channel}/watts");
                System.out.println(topic);
                Assert.assertEquals("metrics/current-cost/00077/ch" + i++ + "/watts", topic);
            }
        } catch (Exception e) {
            throw new CCFException(e);
        }
    }

}
package org.tensin.ccf.events;

import org.tensin.ccf.StringHelper;
import org.tensin.ccf.bean.BeanField;

/**
 * The Class Event.
 */
public class EventWatts extends AbstractEvent implements IEvent {

    /** The watts. */
    @BeanField
    private int watts;

    /** The channel. */
    @BeanField
    private String channel;

    /**
     * Instantiates a new event watts.
     *
     * @param sensor
     *            the sensor
     * @param id
     *            the id
     * @param channel
     *            the channel
     * @param watts
     *            the watts
     */
    public EventWatts(final String sensor, final String id, final String channel, final int watts) {
        super(sensor, id);
        this.watts = watts;
        this.channel = channel;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.AbstractEvent#enhanceTopicWithInternalValues(java.lang.String)
     */
    @Override
    public String enhanceTopicWithInternalValues(final String brokerTopic) {
        String result = super.enhanceTopicWithInternalValues(brokerTopic);
        result = StringHelper.replace(result, "${channel}", getChannel());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#format()
     */
    @Override
    public String format() {
        return String.valueOf(watts);
    }

    /**
     * Gets the channel.
     *
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the watts.
     *
     * @return the watts
     */
    public int getWatts() {
        return watts;
    }

    /**
     * Sets the channel.
     *
     * @param channel
     *            the new channel
     */
    public void setChannel(final String channel) {
        this.channel = channel;
    }

    /**
     * Sets the watts.
     *
     * @param watts
     *            the new watts
     */
    public void setWatts(final int watts) {
        this.watts = watts;
    }
}
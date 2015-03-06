package org.tensin.ccf.events;

import org.tensin.ccf.bean.BeanField;

/**
 * The Class Event.
 */
public class EventWatts extends AbstractEvent implements IEvent {

    /** The watts. */
    @BeanField
    private int watts;

    /**
     * Instantiates a new event watts.
     *
     * @param watts
     *            the watts
     */
    public EventWatts(final String sensor, final String id, final int watts) {
        super(sensor, id);
        this.watts = watts;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#enhanceTopicWithInternalValues(java.lang.String)
     */
    @Override
    public String enhanceTopicWithInternalValues(final String topicPattern) {
        String result = topicPattern;
        result = replace(result, "${id}", getId());
        result = replace(result, "${sensor}", getSensor());
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
     * Gets the watts.
     *
     * @return the watts
     */
    public int getWatts() {
        return watts;
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

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#subTopic()
     */
    @Override
    public String subTopic() {
        return "watts";
    }
}
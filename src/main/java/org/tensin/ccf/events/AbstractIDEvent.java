package org.tensin.ccf.events;

import org.apache.commons.lang3.StringUtils;
import org.tensin.ccf.StringHelper;
import org.tensin.ccf.bean.BeanField;

/**
 * The Class AbstractIDEvent.
 */
public abstract class AbstractIDEvent extends AbstractSensorEvent {

    /** The id. */
    @BeanField
    private final String id;

    /**
     * Instantiates a new abstract event.
     *
     * @param sensor
     *            the sensor
     * @param id
     *            the id
     */
    public AbstractIDEvent(final String sensor, final String id) {
        super(StringUtils.trim(sensor));
        this.id = StringUtils.trim(id);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#enhanceTopicWithInternalValues(java.lang.String)
     */
    public String enhanceTopicWithInternalValues(final String brokerTopic) {
        String result = brokerTopic;
        result = StringHelper.replace(result, "${id}", getId());
        result = StringHelper.replace(result, "${sensor}", getSensor());
        return result;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

}

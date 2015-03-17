package org.tensin.ccf.events;

import org.apache.commons.lang3.StringUtils;
import org.tensin.ccf.StringHelper;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.history.CurrentCostHistoryItemType;

/**
 * The Class EventHistory.
 */
public class EventHistory extends AbstractSensorEvent implements IEvent {

    /** The watts. */
    @BeanField
    private final String value;

    /** The type. */
    @BeanField
    private final CurrentCostHistoryItemType type;

    /** The type. */
    @BeanField
    private final String seed;

    /**
     * Instantiates a new event history.
     *
     * @param sensor
     *            the sensor
     * @param type
     *            the type
     * @param seed
     *            the seed
     * @param value
     *            the value
     */
    public EventHistory(final String sensor, final CurrentCostHistoryItemType type, final String seed, final String value) {
        super(StringUtils.trim(sensor));
        this.type = type;
        this.value = StringUtils.trim(value);
        this.seed = StringUtils.trim(seed);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.AbstractSensorEvent#enhanceTopicWithInternalValues(java.lang.String)
     */
    @Override
    public String enhanceTopicWithInternalValues(final String brokerTopic) {
        String result = brokerTopic;
        result = StringHelper.replace(result, "${sensor}", getSensor());
        result = StringHelper.replace(result, "${seed}", getSeed());
        if (result.contains("${type}")) {
            result = StringHelper.replace(result, "${type}", type.topic());
        } else {
            result = result + "/" + type.topic();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#format()
     */
    @Override
    public String format() {
        return String.valueOf(value);
    }

    /**
     * Gets the seed.
     *
     * @return the seed
     */
    public String getSeed() {
        return seed;
    }
}
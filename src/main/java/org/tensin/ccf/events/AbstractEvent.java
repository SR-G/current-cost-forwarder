package org.tensin.ccf.events;

import org.apache.commons.lang3.StringUtils;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.bean.BeanHelper;

/**
 * The Class AbstractEvent.
 */
public abstract class AbstractEvent {

    /** The timestamp. */
    @BeanField
    private final long timestamp;

    /** The sensor. */
    @BeanField
    private final String sensor;

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
    public AbstractEvent(final String sensor, final String id) {
        super();
        timestamp = System.currentTimeMillis();
        this.sensor = sensor;
        this.id = id;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return BeanHelper.equals(this, obj);
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the sensor.
     *
     * @return the sensor
     */
    public String getSensor() {
        return sensor;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return BeanHelper.hashCode(this);
    }

    /**
     * Replace.
     *
     * @param result
     *            the result
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the string
     */
    protected String replace(final String result, final String key, final String value) {
        if (StringUtils.isNotEmpty(value) && !StringUtils.equals("null", value)) {
            return StringUtils.replace(result, key, value);
        } else {
            return StringUtils.replace(result, key, "");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return BeanHelper.toString(this);
    }
}
package org.tensin.ccf.events;

import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.bean.BeanHelper;

/**
 * The Class AbstractEvent.
 */
public abstract class AbstractSensorEvent {

    /** The timestamp. */
    @BeanField
    private final long timestamp;

    /** The sensor. */
    @BeanField
    private final String sensor;

    /**
     * Instantiates a new abstract event.
     *
     * @param sensor
     *            the sensor
     * @param id
     *            the id
     */
    public AbstractSensorEvent(final String sensor) {
        super();
        timestamp = System.currentTimeMillis();
        this.sensor = sensor;
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
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return BeanHelper.toString(this);
    }
}
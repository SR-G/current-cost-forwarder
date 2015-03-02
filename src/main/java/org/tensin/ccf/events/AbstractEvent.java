package org.tensin.ccf.events;

import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.bean.BeanHelper;

/**
 * The Class AbstractEvent.
 */
public abstract class AbstractEvent {

    /** The timestamp. */
    @BeanField
    private final long timestamp;

    /**
     * Instantiates a new abstract event.
     */
    public AbstractEvent() {
        super();
        timestamp = System.currentTimeMillis();
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
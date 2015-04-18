package org.tensin.ccf.model;

import org.simpleframework.xml.Element;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.bean.BeanHelper;

/**
 * The Class AbstractCurrentCostMessage.
 */
public abstract class AbstractCurrentCostMessage extends AbstractCurrentCost {

    /** The source and software version, ex. CC128-v0.11 */
    @Element(required = false, name = "src")
    @BeanField
    private String source;

    /** The dsb, days since birth, ie days run */
    @Element(required = false, name = "dsb")
    @BeanField
    private String dsb;

    /** The time, 24 hour clock time as displayed. */
    @Element(required = false, name = "time")
    @BeanField
    private String time;

    /**
     * Gets the dsb.
     *
     * @return the dsb
     */
    public String getDsb() {
        return dsb;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the time.
     *
     * @return the time
     */
    public String getTime() {
        return time;
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
     * Sets the dsb.
     *
     * @param dsb
     *            the new dsb
     */
    public void setDsb(final String dsb) {
        this.dsb = dsb;
    }

    /**
     * Sets the source.
     *
     * @param source
     *            the new source
     */
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * Sets the time.
     *
     * @param time
     *            the new time
     */
    public void setTime(final String time) {
        this.time = time;
    }

}
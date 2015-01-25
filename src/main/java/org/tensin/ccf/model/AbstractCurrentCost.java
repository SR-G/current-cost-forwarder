package org.tensin.ccf.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.tensin.ccf.bean.BeanHelper;

/**
 * The Class AbstractCurrentCost.
 */
public abstract class AbstractCurrentCost {

    /**
     * Convert time.
     *
     * @param s
     *            the s
     * @return the long
     */
    public static long convertTime(final String s) {
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        try {
            final Date d = sdf.parse(s);
            return d.getTime();
        } catch (ParseException e) {
            return 0;
        }
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
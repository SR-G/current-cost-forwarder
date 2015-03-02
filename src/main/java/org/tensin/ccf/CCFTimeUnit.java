package org.tensin.ccf;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.bean.BeanHelper;

/**
 * The Class SAIGTimeUnit.
 * Allows to write time unit, for example in XML or as user input, with the format "1000ms" or "7s" or "3m" or "1h", and so on
 * internally the value is stored in milliseconds (by default, may be overridden if using the right constructor)
 * various .toSeconds(), .toMinutes(), .. are available for conversions
 * format() method dumps the best looking result (for example "1h 17m 58s")
 * toString() dumps the raw value in the internal TimeUnit
 *
 * @author Serge SIMON
 * @version $Revision:1.00 $
 * @since 2 jan. 2012 13:18:03
 */
public class CCFTimeUnit implements Serializable {

    /**
     * Parses the time.
     *
     * @param sValue
     *            the s value
     * @return the SAIG time unit
     */
    public static CCFTimeUnit parseTime(final String sValue) {
        try {
            long millis;
            if (sValue.endsWith("S")) {
                millis = Long.parseLong(sValue.substring(0, sValue.length() - 1));
            } else if (sValue.endsWith("ms")) {
                millis = (long) (Double.parseDouble(sValue.substring(0, sValue.length() - 2)));
            } else if (sValue.endsWith("s")) {
                millis = (long) (Double.parseDouble(sValue.substring(0, sValue.length() - 1)) * DateUtils.MILLIS_PER_SECOND);
            } else if (sValue.endsWith("m")) {
                millis = (long) (Double.parseDouble(sValue.substring(0, sValue.length() - 1)) * DateUtils.MILLIS_PER_MINUTE);
            } else if (sValue.endsWith("H") || sValue.endsWith("h")) {
                millis = (long) (Double.parseDouble(sValue.substring(0, sValue.length() - 1)) * DateUtils.MILLIS_PER_HOUR);
            } else if (sValue.endsWith("d")) {
                millis = (long) (Double.parseDouble(sValue.substring(0, sValue.length() - 1)) * DateUtils.MILLIS_PER_DAY);
            } else if (sValue.endsWith("w")) {
                millis = (long) (Double.parseDouble(sValue.substring(0, sValue.length() - 1)) * MILLIS_PER_WEEK);
            } else {
                millis = Long.parseLong(sValue);
            }
            return new CCFTimeUnit(millis, TimeUnit.MILLISECONDS);
        } catch (NumberFormatException e) {
            throw new CCFRuntimeException("Failed to parse [" + sValue + "]", e);
        }
    }

    /**
     * Parses the time.
     *
     * @param sValue
     *            the s value
     * @param defaultValue
     *            the default value
     * @return the SAIG time unit
     */
    public static CCFTimeUnit parseTime(final String sValue, final CCFTimeUnit defaultValue) {
        if (StringUtils.isEmpty(sValue)) {
            return defaultValue;
        } else {
            return parseTime(sValue);
        }
    }

    /**
     * Parses the time.
     *
     * @param sValue
     *            the s value
     * @param defaultValue
     *            the default value
     * @return the SAIG time unit
     */
    public static CCFTimeUnit parseTime(final String sValue, final String defaultValue) {
        if (StringUtils.isEmpty(sValue)) {
            return parseTime(defaultValue);
        } else {
            return parseTime(sValue);
        }
    }

    /** The Constant WEEK. */
    private static final long MILLIS_PER_WEEK = 7 * DateUtils.MILLIS_PER_DAY;

    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The duration. */
    @BeanField
    private long duration;

    /** The time unit. */
    @BeanField
    private TimeUnit timeUnit;

    /**
     * The Constructor.
     */
    public CCFTimeUnit() {
        super();
    }

    /**
     * The Constructor.
     *
     * @param duration
     *            the duration
     * @param timeUnit
     *            the time unit
     */
    public CCFTimeUnit(final long duration, final TimeUnit timeUnit) {
        this.duration = duration;
        this.timeUnit = timeUnit;
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
     * Format.
     *
     * @return the string
     */
    public String format() {
        long millis = toMilliseconds();
        String separator = "";
        final StringBuilder sb = new StringBuilder();
        final int days = (int) (millis / DateUtils.MILLIS_PER_DAY);
        if (days > 0) {
            millis = millis - (days * DateUtils.MILLIS_PER_DAY);
            sb.append(separator).append(days).append("d");
            separator = " ";
        }
        final int hours = (int) (millis / DateUtils.MILLIS_PER_HOUR);
        if (hours > 0) {
            millis = millis - (hours * DateUtils.MILLIS_PER_HOUR);
            sb.append(separator).append(hours).append("h");
            separator = " ";
        }
        final int minutes = (int) (millis / DateUtils.MILLIS_PER_MINUTE);
        if (minutes > 0) {
            millis = millis - (minutes * DateUtils.MILLIS_PER_MINUTE);
            sb.append(separator).append(minutes).append("m");
            separator = " ";
        }
        final int seconds = (int) (millis / DateUtils.MILLIS_PER_SECOND);
        if (seconds > 0) {
            millis = millis - (seconds * DateUtils.MILLIS_PER_SECOND);
            sb.append(separator).append(seconds).append("s");
            separator = " ";
        }
        if (millis > 0) {
            sb.append(separator).append(millis).append("ms");
        }
        return sb.toString();
    }

    /**
     * Gets the duration.
     *
     * @return the duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Gets the time unit.
     *
     * @return the time unit
     */
    public TimeUnit getTimeUnit() {
        return timeUnit;
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
     * Sets the duration.
     *
     * @param duration
     *            the duration
     */
    public void setDuration(final long duration) {
        this.duration = duration;
    }

    /**
     * Sets the time unit.
     *
     * @param timeUnit
     *            the time unit
     */
    public void setTimeUnit(final TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * To microseconds.
     *
     * @return the long
     */
    public long toMicroseconds() {
        return timeUnit.toMicros(duration);
    }

    /**
     * To milliseconds.
     *
     * @return the long
     */
    public long toMilliseconds() {
        return timeUnit.toMillis(duration);
    }

    /**
     * To minutes.
     *
     * @return the long
     */
    public long toMinutes() {
        return timeUnit.toMinutes(duration);
    }

    /**
     * To nanoseconds.
     *
     * @return the long
     */
    public long toNanoseconds() {
        return timeUnit.toNanos(duration);
    }

    /**
     * To seconds.
     *
     * @return the long
     */
    public long toSeconds() {
        return timeUnit.toSeconds(duration);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        switch (timeUnit) {
        case NANOSECONDS:
            return duration + "ns";
        case MICROSECONDS:
            return duration + "us";
        case MILLISECONDS:
            return duration + "ms";
        case SECONDS:
            return duration + "s";
        case MINUTES:
            return duration + "m";
        case HOURS:
            return duration + "h";
        case DAYS:
            return duration + "d";
        default:
            return String.valueOf(duration);
        }
    }
}

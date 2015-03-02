package org.tensin.ccf;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 * The Class TimeHelper.
 */
public final class TimeHelper {

    /**
     * Wait.
     *
     * @param timeout
     *            the timeout
     */
    public static void wait(final CCFTimeUnit timeout) {
        Uninterruptibles.sleepUninterruptibly(timeout.getDuration(), timeout.getTimeUnit());
    }

    /**
     * Instantiates a new time helper.
     */
    private TimeHelper() {

    }
}
package org.tensin.ccf;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class StringHelper.
 */
public final class StringHelper {

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
    public static String replace(final String result, final String key, final String value) {
        if (StringUtils.isNotEmpty(value) && !StringUtils.equals("null", value)) {
            return StringUtils.replace(result, key, value);
        } else {
            return StringUtils.replace(result, key, "");
        }
    }

    /**
     * Instantiates a new string helper.
     */
    private StringHelper() {

    }
}
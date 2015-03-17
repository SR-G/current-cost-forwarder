package org.tensin.ccf.model.history;

/**
 * The Enum CurrentCostHistoryItemType.
 */
public enum CurrentCostHistoryItemType {

    /** The day. */
    DAY("daily"), /** The month. */
    MONTH("monthly"), /** The hour. */
    HOUR("hourly");

    /** The topic. */
    private final String topic;

    /**
     * Instantiates a new current cost history item type.
     *
     * @param s
     *            the s
     */
    CurrentCostHistoryItemType(final String s) {
        topic = s;
    }

    /**
     * Topic.
     *
     * @return the string
     */
    public String topic() {
        return topic;
    }
}
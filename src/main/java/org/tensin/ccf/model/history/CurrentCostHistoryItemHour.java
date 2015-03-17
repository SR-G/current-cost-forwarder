package org.tensin.ccf.model.history;


/**
 * The Class CurrentCostHistoryItemHour.
 */
public final class CurrentCostHistoryItemHour extends AbstractCurrentCostHistoryItem implements ICurrentCostHistoryItem {

    /**
     * Instantiates a new current cost history item hour.
     *
     * @param seed
     *            the seed
     * @param value
     *            the value
     */
    public CurrentCostHistoryItemHour(final String seed, final String value) {
        super(CurrentCostHistoryItemType.HOUR, seed, value);
    }
}
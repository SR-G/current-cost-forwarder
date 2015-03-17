package org.tensin.ccf.model.history;


/**
 * The Class CurrentCostHistoryItemMonth.
 */
public final class CurrentCostHistoryItemMonth extends AbstractCurrentCostHistoryItem implements ICurrentCostHistoryItem {

    /**
     * Instantiates a new current cost history item month.
     *
     * @param seed
     *            the seed
     * @param value
     *            the value
     */
    public CurrentCostHistoryItemMonth(final String seed, final String value) {
        super(CurrentCostHistoryItemType.MONTH, seed, value);
    }
}
package org.tensin.ccf.model.history;


/**
 * The Class CurrentCostHistoryItemDay.
 */
public final class CurrentCostHistoryItemDay extends AbstractCurrentCostHistoryItem implements ICurrentCostHistoryItem {

    /**
     * Instantiates a new current cost history item day.
     *
     * @param seed
     *            the seed
     * @param value
     *            the value
     */
    public CurrentCostHistoryItemDay(final String seed, final String value) {
        super(CurrentCostHistoryItemType.DAY, seed, value);
    }
}
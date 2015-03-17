package org.tensin.ccf.model.history;


/**
 * The Interface ICurrentCostHistoryItem.
 */
public interface ICurrentCostHistoryItem {

    /**
     * Gets the seed.
     *
     * @return the seed
     */
    String getSeed();

    /**
     * Gets the type.
     *
     * @return the type
     */
    CurrentCostHistoryItemType getType();

    /**
     * Gets the value.
     *
     * @return the value
     */
    String getValue();

    /**
     * Checks if is not empty.
     *
     * @return true, if is not empty
     */
    boolean isNotEmpty();
}
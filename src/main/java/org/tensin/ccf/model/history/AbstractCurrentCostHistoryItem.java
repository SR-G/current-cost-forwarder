package org.tensin.ccf.model.history;

import org.apache.commons.lang3.StringUtils;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCost;

/**
 * The Class AbstractCurrentCostHistoryItem.
 */
public abstract class AbstractCurrentCostHistoryItem extends AbstractCurrentCost {

    /** The type. */
    @BeanField
    private final CurrentCostHistoryItemType type;

    /** The seed. */
    @BeanField
    private final String seed;

    /** The value. */
    @BeanField
    private final String value;

    /**
     * Instantiates a new abstract current cost history item.
     *
     * @param type
     *            the type
     * @param seed
     *            the seed
     * @param value
     *            the value
     */
    public AbstractCurrentCostHistoryItem(final CurrentCostHistoryItemType type, final String seed, final String value) {
        super();
        this.type = type;
        this.seed = seed;
        this.value = value;
    }

    /**
     * Gets the seed.
     *
     * @return the seed
     */
    public String getSeed() {
        return seed;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public CurrentCostHistoryItemType getType() {
        return type;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public boolean isNotEmpty() {
        return !StringUtils.equals("0.000", value);
    }
}
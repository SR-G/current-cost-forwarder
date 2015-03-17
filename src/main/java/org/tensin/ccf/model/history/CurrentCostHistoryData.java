package org.tensin.ccf.model.history;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCost;

/**
 * The Class CurrentCostHistory.
 */
@Root(strict = true)
public class CurrentCostHistoryData extends AbstractCurrentCost {

    /** The type. */
    @BeanField
    @Element
    private String sensor;

    /** The items. */
    @BeanField
    private Collection<ICurrentCostHistoryItem> items = new ArrayList<ICurrentCostHistoryItem>();

    /**
     * Adds the item.
     *
     * @param item
     *            the item
     */
    public void addItem(final ICurrentCostHistoryItem item) {
        items.add(item);
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public Collection<ICurrentCostHistoryItem> getItems() {
        return items;
    }

    /**
     * Gets the sensor.
     *
     * @return the sensor
     */
    public String getSensor() {
        return sensor;
    }

    /**
     * Sets the items.
     *
     * @param items
     *            the new items
     */
    public void setItems(final Collection<ICurrentCostHistoryItem> items) {
        this.items = items;
    }

    /**
     * Sets the sensor.
     *
     * @param sensor
     *            the new sensor
     */
    public void setSensor(final String sensor) {
        this.sensor = sensor;
    }
}
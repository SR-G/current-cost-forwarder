package org.tensin.ccf.model.message;

import org.simpleframework.xml.Element;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCost;

/**
 * The Class CurrentCostChannel.
 */
public abstract class AbstractCurrentCostChannel extends AbstractCurrentCost {

    /** The watts. */
    @Element(required = false, name = "watts")
    @BeanField
    private int watts;

    /**
     * Gets the watts.
     *
     * @return the watts
     */
    public int getWatts() {
        return watts;
    }

    /**
     * Sets the watts.
     *
     * @param watts
     *            the new watts
     */
    public void setWatts(final int watts) {
        this.watts = watts;
    }
}
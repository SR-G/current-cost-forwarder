package org.tensin.ccf.model.history;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCost;

/**
 * The Class CurrentCostHistory.
 */
@Root()
public class CurrentCostHistoryData extends AbstractCurrentCost {

    /** The type. */
    @Element(required = false, name = "sensor")
    @BeanField
    private String sensor;

    /**
     * Gets the sensor.
     *
     * @return the sensor
     */
    public String getSensor() {
        return sensor;
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
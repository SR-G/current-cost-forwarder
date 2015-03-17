package org.tensin.ccf.model.history;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCost;

/**
 * The Class CurrentCostHistory.
 */
@Root(name = "hist", strict = true)
public class CurrentCostHistoryEntry extends AbstractCurrentCost {

    /** The type. */
    @Element(required = false, name = "dsw")
    @BeanField
    private String dsw;

    /** The type. */
    @Element(required = false, name = "type")
    @BeanField
    private int type;

    /** The type. */
    @Element(required = false, name = "units")
    @BeanField
    private String units;

    /** The data. */
    @ElementList(required = false, inline = true, entry = "data")
    @BeanField
    private Collection<CurrentCostHistoryData> data = new ArrayList<CurrentCostHistoryData>();

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Collection<CurrentCostHistoryData> getData() {
        return data;
    }

    /**
     * Gets the dsw.
     *
     * @return the dsw
     */
    public String getDsw() {
        return dsw;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the units.
     *
     * @return the units
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the data.
     *
     * @param data
     *            the new data
     */
    public void setData(final Collection<CurrentCostHistoryData> data) {
        this.data = data;
    }

    /**
     * Sets the dsw.
     *
     * @param dsw
     *            the new dsw
     */
    public void setDsw(final String dsw) {
        this.dsw = dsw;
    }

    /**
     * Sets the type.
     *
     * @param type
     *            the new type
     */
    public void setType(final int type) {
        this.type = type;
    }

    /**
     * Sets the units.
     *
     * @param units
     *            the new units
     */
    public void setUnits(final String units) {
        this.units = units;
    }
}
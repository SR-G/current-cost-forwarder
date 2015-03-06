package org.tensin.ccf.model.message;

import java.util.ArrayList;
import java.util.Collection;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Root;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.model.AbstractCurrentCostMessage;

/**
 * The Class CurrentCostMessage.
 */
@Root(name = "msg")
public class CurrentCostMessage extends AbstractCurrentCostMessage {

    /** The temperature. */
    @Element(required = false, name = "tmpr")
    @BeanField
    private double temperature;

    /** The sensor. */
    @Element(required = false, name = "sensor")
    @BeanField
    private String sensor;

    /** The id. */
    @Element(required = false, name = "id")
    @BeanField
    private String id;

    /** The type. */
    @Element(required = false, name = "type")
    @BeanField
    private int type;

    /** The channels. */
    @ElementListUnion({ @ElementList(required = false, inline = true, entry = "ch1", type = CurrentCostChannelCH1.class),
        @ElementList(required = false, inline = true, entry = "ch2", type = CurrentCostChannelCH2.class),
        @ElementList(required = false, inline = true, entry = "ch3", type = CurrentCostChannelCH3.class),
        @ElementList(required = false, inline = true, entry = "ch4", type = CurrentCostChannelCH4.class),
        @ElementList(required = false, inline = true, entry = "ch5", type = CurrentCostChannelCH5.class),
        @ElementList(required = false, inline = true, entry = "ch6", type = CurrentCostChannelCH6.class),
        @ElementList(required = false, inline = true, entry = "ch7", type = CurrentCostChannelCH7.class),
        @ElementList(required = false, inline = true, entry = "ch8", type = CurrentCostChannelCH8.class),
        @ElementList(required = false, inline = true, entry = "ch9", type = CurrentCostChannelCH9.class) })
    @BeanField
    private Collection<AbstractCurrentCostChannel> channels = new ArrayList<AbstractCurrentCostChannel>();

    /**
     * Gets the channels.
     *
     * @return the channels
     */
    public Collection<AbstractCurrentCostChannel> getChannels() {
        return channels;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
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
     * Gets the temperature.
     *
     * @return the temperature
     */
    public double getTemperature() {
        return temperature;
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
     * Sets the channels.
     *
     * @param channels
     *            the new channels
     */
    public void setChannels(final Collection<AbstractCurrentCostChannel> channels) {
        this.channels = channels;
    }

    /**
     * Sets the id.
     *
     * @param id
     *            the new id
     */
    public void setId(final String id) {
        this.id = id;
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

    /**
     * Sets the temperature.
     *
     * @param temperature
     *            the new temperature
     */
    public void setTemperature(final double temperature) {
        this.temperature = temperature;
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
}

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

    /** The channel. */
    @BeanField
    private String channel;

    /**
     * Instantiates a new abstract current cost channel.
     *
     * @param channel
     *            the channel
     */
    public AbstractCurrentCostChannel(final String channel) {
        super();
        this.channel = channel;
    }

    /**
     * Gets the channel.
     *
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Gets the watts.
     *
     * @return the watts
     */
    public int getWatts() {
        return watts;
    }

    /**
     * Sets the channel.
     *
     * @param channel
     *            the new channel
     */
    public void setChannel(final String channel) {
        this.channel = channel;
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
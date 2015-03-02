/*
 *
 */
package org.tensin.ccf.events;

import org.tensin.ccf.bean.BeanField;

/**
 * The Class Event.
 */
public class EventTemperature extends AbstractEvent implements IEvent {

    /** The watts. */
    @BeanField
    private double temperature;

    /**
     * Instantiates a new event temperature.
     *
     * @param temperature
     *            the temperature
     */
    public EventTemperature(final double temperature) {
        super();
        this.temperature = temperature;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#format()
     */
    @Override
    public String format() {
        return String.valueOf(temperature);
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
     * Sets the temperature.
     *
     * @param temperature
     *            the new temperature
     */
    public void setTemperature(final double temperature) {
        this.temperature = temperature;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.events.IEvent#subTopic()
     */
    @Override
    public String subTopic() {
        return "temperature";
    }
}
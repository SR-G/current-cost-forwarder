package org.tensin.ccf.events;

/**
 * The Interface IEvent.
 */
public interface IEvent {

    /**
     * Enhance topic with internal values.
     *
     * @param topicPattern
     *            the topic pattern
     * @param brokerTopicTemperature
     * @return the string
     */
    String enhanceTopicWithInternalValues(final String brokerTopic);

    /**
     * Format.
     *
     * @return the string
     */
    String format();
}
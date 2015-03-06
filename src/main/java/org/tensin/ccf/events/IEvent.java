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
     * @return the string
     */
    String enhanceTopicWithInternalValues(final String topicPattern);

    /**
     * Format.
     *
     * @return the string
     */
    String format();

    /**
     * Sub topic.
     *
     * @return the string
     */
    String subTopic();
}
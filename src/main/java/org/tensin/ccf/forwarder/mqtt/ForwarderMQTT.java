package org.tensin.ccf.forwarder.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.CCFTimeUnit;
import org.tensin.ccf.Reducer;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.events.EventHistory;
import org.tensin.ccf.events.EventTemperature;
import org.tensin.ccf.events.EventWatts;
import org.tensin.ccf.events.IEvent;
import org.tensin.ccf.forwarder.IForwarder;

/**
 * The Class ForwarderMQTT.
 */
public class ForwarderMQTT implements IForwarder {

    /**
     * Builds the.
     *
     * @param mqttBrokerDefinition
     *            the mqtt broker definition
     * @param brokerTopic
     *            the broker topic
     * @param brokerDataDir
     *            the broker data dir
     * @param brokerReconnectTimeout
     * @return the forwarder mqtt
     */
    public static ForwarderMQTT build(final MQTTBrokerDefinition mqttBrokerDefinition, final String brokerTopicWatts, final String brokerTopicTemperature,
            final String brokerTopicHistory, final String brokerDataDir, final CCFTimeUnit brokerReconnectTimeout) {
        final ForwarderMQTT forwarderMQTT = new ForwarderMQTT();
        forwarderMQTT.mqttBrokerDefinition = mqttBrokerDefinition;
        forwarderMQTT.brokerTopicWatts = brokerTopicWatts;
        forwarderMQTT.brokerTopicTemperature = brokerTopicTemperature;
        forwarderMQTT.brokerTopicHistory = brokerTopicHistory;
        forwarderMQTT.brokerDataDir = brokerDataDir;
        forwarderMQTT.brokerReconnectTimeout = brokerReconnectTimeout;
        return forwarderMQTT;
    }

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The mqtt broker definition. */
    @BeanField
    private MQTTBrokerDefinition mqttBrokerDefinition;

    /** The client. */
    private MQTTReconnectClient client;

    /** The broker topic. */
    @BeanField
    private String brokerTopicWatts, brokerTopicTemperature, brokerTopicHistory;

    /** The broker data dir. */
    @BeanField
    private String brokerDataDir;

    /** The broker reconnect timeout. */
    @BeanField
    private CCFTimeUnit brokerReconnectTimeout;

    /** The count. */
    private long count;

    /**
     * Gets the broker topic.
     *
     * @param event
     *            the event
     * @return the broker topic
     * @throws CCFException
     */
    public String buildBrokerTopic(final IEvent event) throws CCFException {
        if (event instanceof EventTemperature) {
            return event.enhanceTopicWithInternalValues(brokerTopicTemperature);
        } else if (event instanceof EventWatts) {
            return event.enhanceTopicWithInternalValues(brokerTopicWatts);
        } else if (event instanceof EventHistory) {
            return event.enhanceTopicWithInternalValues(brokerTopicHistory);
        } else {
            throw new CCFException("Unknown event type [" + event.getClass().getName() + "]");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#forward(org.tensin.ccf.events.IEvent)
     */
    @Override
    public void forward(final IEvent event) throws CCFException {
        final String topicName = buildBrokerTopic(event);
        if (Reducer.render(count)) {
            LOGGER.info("Forwarding event #" + count + " " + event.toString() + " on topic [" + topicName + "]");
        }
        final MqttMessage message = new MqttMessage();
        message.setPayload(event.format().getBytes());
        if (client.isConnected()) {
            client.publish(topicName, message);
            count++;
        } else {
            LOGGER.debug("Client not connected, can't forward event");
        }
    }

    /**
     * Gets the broker data dir.
     *
     * @return the broker data dir
     */
    public String getBrokerDataDir() {
        return brokerDataDir;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * Gets the mqtt broker definition.
     *
     * @return the mqtt broker definition
     */
    public MQTTBrokerDefinition getMqttBrokerDefinition() {
        return mqttBrokerDefinition;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#nbThreads()
     */
    @Override
    public int nbThreads() {
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#start()
     */
    @Override
    public void start() throws CCFException {
        LOGGER.info("Starting MQTT forwarder with topic [" + brokerTopicWatts + "] for watts and [" + brokerTopicTemperature
                + "] for temperatures, mqtt broker " + mqttBrokerDefinition.toString());
        client = MQTTReconnectClient.build(mqttBrokerDefinition, MqttClient.generateClientId(), brokerReconnectTimeout, brokerDataDir);
        client.start();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#stop()
     */
    @Override
    public void stop() throws CCFException {
        LOGGER.info("Stopping MQTT forwarder");
        client.stop();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#type()
     */
    @Override
    public String type() {
        return "MQTT";
    }
}
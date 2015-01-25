package org.tensin.ccf.forwarder.mqtt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.bean.BeanField;
import org.tensin.ccf.events.IEvent;
import org.tensin.ccf.forwarder.IForwarder;

/**
 * The Class ForwarderMQTT.
 */
public class ForwarderMQTT implements IForwarder {

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The mqtt broker definition. */
    @BeanField
    private MQTTBrokerDefinition mqttBrokerDefinition;

    /** The client. */
    private MqttClient client;

    /** The broker topic. */
    @BeanField
    private String brokerTopic;

    /** The broker data dir. */
    @BeanField
    private String brokerDataDir;

    /**
     * Gets the broker topic.
     *
     * @param event
     *            the event
     * @return the broker topic
     */
    public String buildBrokerTopic(final IEvent event) {
        if (brokerTopic.endsWith("/")) {
            return brokerTopic + event.subTopic();
        } else {
            return brokerTopic + "/" + event.subTopic();
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
        LOGGER.info("Forwarding event " + event.toString() + " on topic [" + topicName + "]");
        final MqttMessage message = new MqttMessage();
        message.setPayload(event.format().getBytes());
        try {
            client.publish(topicName, message);
        } catch (MqttPersistenceException e) {
            throw new CCFException("Can't publish message [" + message.toString() + "] on topic [" + topicName + "]", e);
        } catch (MqttException e) {
            throw new CCFException("Can't publish message [" + message.toString() + "] on topic [" + topicName + "]", e);
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
     * Gets the mqtt broker definition.
     *
     * @return the mqtt broker definition
     */
    public MQTTBrokerDefinition getMqttBrokerDefinition() {
        return mqttBrokerDefinition;
    }

    /**
     * Inits the client mqtt.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void initClientMQTT() throws CCFException {
        final String previousUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", brokerDataDir);

            final String clientId = MqttClient.generateClientId();
            final MqttConnectOptions options = new MqttConnectOptions();
            final StringBuilder sb = new StringBuilder();
            if (getMqttBrokerDefinition().isBrokerAuth()) {
                final String hiddenPassword = StringUtils.repeat("*", getMqttBrokerDefinition().getBrokerPassword() == null ? 0 : getMqttBrokerDefinition()
                        .getBrokerPassword().length());
                sb.append(", connection will be authentificated with username [").append(getMqttBrokerDefinition().getBrokerUsername()).append("], password [")
                        .append(hiddenPassword).append("]");
                options.setUserName(getMqttBrokerDefinition().getBrokerUsername());
                options.setPassword(getMqttBrokerDefinition().getBrokerPassword().toCharArray());
                client.setCallback(new MqttCallback() {

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
                     */
                    @Override
                    public void connectionLost(final Throwable arg0) {
                        LOGGER.info("MQTT connection lost", arg0);
                    }

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
                     */
                    @Override
                    public void deliveryComplete(final IMqttDeliveryToken arg0) {
                        try {
                            LOGGER.debug("Message delivered [" + arg0.getMessage().toString() + "]");
                        } catch (MqttException e) {
                            LOGGER.error("Can't read back message", e);
                        }
                    }

                    /**
                     * {@inheritDoc}
                     *
                     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
                     */
                    @Override
                    public void messageArrived(final String arg0, final MqttMessage arg1) throws Exception {
                    }
                });
            } else {
                sb.append(", without authentification");
            }
            LOGGER.info("Now starting MQTT client on broker url [" + getMqttBrokerDefinition().getBrokerUrl() + "], client ID is [" + clientId + "]"
                    + sb.toString());

            client = new MqttClient(getMqttBrokerDefinition().getBrokerUrl(), clientId);
            client.connect(options);
        } catch (MqttException e) {
            throw new CCFException("Can't start MQTT client on broker url [" + getMqttBrokerDefinition().getBrokerUrl() + "]", e);
        } finally {
            System.setProperty("user.dir", previousUserDir);
        }
    }

    /**
     * Sets the broker data dir.
     *
     * @param brokerDataDir
     *            the new broker data dir
     */
    public void setBrokerDataDir(final String brokerDataDir) {
        this.brokerDataDir = brokerDataDir;
    }

    /**
     * Sets the broker topic.
     *
     * @param brokerTopic
     *            the new broker topic
     */
    public void setBrokerTopic(final String brokerTopic) {
        this.brokerTopic = brokerTopic;
    }

    /**
     * Sets the mqtt broker definition.
     *
     * @param mqttBrokerDefinition
     *            the new mqtt broker definition
     */
    public void setMqttBrokerDefinition(final MQTTBrokerDefinition mqttBrokerDefinition) {
        this.mqttBrokerDefinition = mqttBrokerDefinition;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#start()
     */
    @Override
    public void start() throws CCFException {
        LOGGER.info("Starting MQTT forwarder with topic base name [" + brokerTopic + "], mqtt broker " + mqttBrokerDefinition.toString());
        initClientMQTT();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.tensin.ccf.forwarder.IForwarder#stop()
     */
    @Override
    public void stop() throws CCFException {
        LOGGER.info("Stopping MQTT forwarder");
        stopClientMQTT();
    }

    /**
     * Stop client mqtt.
     *
     * @throws CCFException
     *             the CCF exception
     */
    private void stopClientMQTT() throws CCFException {
        if (client != null) {
            LOGGER.info("Now stoping MQTT client");
            try {
                client.disconnect();
            } catch (MqttException e) {
                throw new CCFException("Can't stop MQTT client", e);
            }
        }
    }
}
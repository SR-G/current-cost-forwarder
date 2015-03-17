package org.tensin.ccf.forwarder.mqtt;

import java.util.concurrent.atomic.AtomicBoolean;

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
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.tensin.ccf.CCFException;
import org.tensin.ccf.CCFTimeUnit;
import org.tensin.ccf.Constants;
import org.tensin.ccf.TimeHelper;

/**
 * The Class MQTTReconnectClient.
 */
public class MQTTReconnectClient {

    /**
     * Builds the.
     *
     * @param mqttBrokerDefinition
     *            the mqtt broker definition
     * @param clientId
     *            the client id
     * @param reconnectTimeout
     *            the reconnect timeout
     * @param brokerDataDir
     *            the broker data dir
     * @return the MQTT reconnect client
     */
    public static MQTTReconnectClient build(final MQTTBrokerDefinition mqttBrokerDefinition, final String clientId, final CCFTimeUnit reconnectTimeout,
            final String brokerDataDir) {
        final MQTTReconnectClient client = new MQTTReconnectClient();
        client.reconnectTimeout = reconnectTimeout;
        client.clientId = MqttClient.generateClientId();
        client.mqttBrokerDefinition = mqttBrokerDefinition;
        client.brokerDataDir = brokerDataDir;
        return client;
    }

    /** The broker data dir. */
    private String brokerDataDir;

    /** The mqtt broker definition. */
    private MQTTBrokerDefinition mqttBrokerDefinition;

    /** Logger. */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The client. */
    private MqttClient client;

    /** The options. */
    private MqttConnectOptions options;

    /** The client id. */
    private String clientId;

    /** The reconnect timeout. */
    private CCFTimeUnit reconnectTimeout = CCFTimeUnit.parseTime("5s");

    /** The reconnect thread. */
    private Thread reconnectThread;

    /** The alive. */
    private boolean alive;

    /** The connected. */
    private final AtomicBoolean connected = new AtomicBoolean(false);

    /** The connection in progress. */
    private final AtomicBoolean connectionInProgress = new AtomicBoolean(false);

    /**
     * Connect.
     */
    private void connect() {
        connectionInProgress.set(true);
        // final String previousUserDir = System.getProperty("user.dir");
        try {
            // System.setProperty("user.dir", brokerDataDir);
            final MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(brokerDataDir);
            client = new MqttClient(getMqttBrokerDefinition().getBrokerUrl(), clientId, persistence);
            client.setCallback(new MqttCallback() {

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
                 */
                @Override
                public void connectionLost(final Throwable throwable) {
                    LOGGER.info("MQTT connection lost", throwable);
                    connected.set(false);
                }

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
                 */
                @Override
                public void deliveryComplete(final IMqttDeliveryToken token) {
                }

                /**
                 * {@inheritDoc}
                 *
                 * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
                 */
                @Override
                public void messageArrived(final String topicName, final MqttMessage message) throws Exception {
                }
            });
            client.connect(options);
            connected.set(true);
            LOGGER.info("Connection done on MQTT Broker");
        } catch (MqttException e) {
            LOGGER.error("Can't start MQTT client on broker url [" + getMqttBrokerDefinition().getBrokerUrl() + "]", e);
            connected.set(false);
        } finally {
            // System.setProperty("user.dir", previousUserDir);
            connectionInProgress.set(false);
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
     * Gets the reconnect timeout.
     *
     * @return the reconnect timeout
     */
    public CCFTimeUnit getReconnectTimeout() {
        return reconnectTimeout;
    }

    /**
     * Checks if is alive.
     *
     * @return true, if is alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        // return (client != null) && client.isConnected();
        return connected.get();
    }

    /**
     * Checks if is connection in progress.
     *
     * @return true, if is connection in progress
     */
    protected boolean isConnectionInProgress() {
        return connectionInProgress.get();
    }

    /**
     * Publish.
     *
     * @param topicName
     *            the topic name
     * @param message
     *            the message
     * @throws CCFException
     *             the CCF exception
     */
    public void publish(final String topicName, final MqttMessage message) throws CCFException {
        try {
            if (isConnected() && (client != null)) {
                client.publish(topicName, message);
            }
        } catch (MqttPersistenceException e) {
            throw new CCFException("Can't publish message [" + message.toString() + "] on topic [" + topicName + "]", e);
        } catch (MqttException e) {
            throw new CCFException("Can't publish message [" + message.toString() + "] on topic [" + topicName + "]", e);
        }
    }

    /**
     * Start.
     */
    public void start() {
        alive = true;

        options = new MqttConnectOptions();
        // options.setConnectionTimeout(reconnectTimeout);
        final StringBuilder sb = new StringBuilder();
        if (mqttBrokerDefinition.isBrokerAuth()) {
            final String hiddenPassword = StringUtils.repeat("*", mqttBrokerDefinition.getBrokerPassword() == null ? 0 : mqttBrokerDefinition
                    .getBrokerPassword().length());
            sb.append(", connection will be authentificated with username [").append(mqttBrokerDefinition.getBrokerUsername()).append("], password [")
            .append(hiddenPassword).append("]");
            options.setUserName(mqttBrokerDefinition.getBrokerUsername());
            options.setPassword(mqttBrokerDefinition.getBrokerPassword().toCharArray());
        } else {
            sb.append(", without authentification");
        }
        LOGGER.info("Now starting MQTT client on broker url [" + mqttBrokerDefinition.getBrokerUrl() + "], client ID is [" + clientId
                + "], reconnecting each [" + reconnectTimeout.format() + "]" + sb.toString());
        reconnectThread = new Thread() {

            /**
             * {@inheritDoc}
             *
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
                setName(Constants.THREAD_NAME + "-MQTT-RECONNECT");
                while (isAlive()) {
                    if (!isConnected() && !isConnectionInProgress()) {
                        LOGGER.info("Connection not done on MQTT broker, will now try to connect");
                        connect();
                    }
                    TimeHelper.wait(reconnectTimeout);
                }
            }
        };
        reconnectThread.start();
    }

    /**
     * Stop.
     *
     * @throws CCFException
     *             the CCF exception
     */
    public void stop() throws CCFException {
        if (client != null) {
            LOGGER.info("Now stoping MQTT client");
            try {
                client.disconnect();
            } catch (MqttException e) {
                throw new CCFException("Can't stop MQTT client", e);
            }
        }
        alive = false;
    }
}
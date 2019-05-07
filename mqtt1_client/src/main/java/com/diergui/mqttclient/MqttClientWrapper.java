/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diergui.mqttclient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/* QOS documentation:
    0 – “at most once” semantics, also known as “fire-and-forget”. Use this option when message loss is acceptable, as it does not require any kind of acknowledgment or persistence
    1 – “at least once” semantics. Use this option when message loss is not acceptable and your subscribers can handle duplicates
    2 – “exactly once” semantics. Use this option when message loss is not acceptable and your subscribers cannot handle duplicates
 */
public class MqttClientWrapper {

    private MqttClient sampleClient;

    private final String topic = "news";
    private final int qos = 0;
    private final String broker = "tcp://0.0.0.0:1883";
    private final ReceivedMessageListener receivedMessageListener;

    private boolean keepDisconnected;

    public MqttClientWrapper(ReceivedMessageListener receivedMessageListener) {
        this.receivedMessageListener = receivedMessageListener;
    }

    public void initialize(String publisherId) throws MqttException {

        if (sampleClient == null) {
            sampleClient = new MqttClient(broker, publisherId, new MemoryPersistence());
        }

        keepDisconnected = false;
        connect();
    }

    private void connect() {
        if (!sampleClient.isConnected()) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!keepDisconnected && !sampleClient.isConnected()) {
                        try {

                            final MqttConnectOptions options = new MqttConnectOptions();
                            options.setAutomaticReconnect(true);
                            options.setCleanSession(false);
                            options.setConnectionTimeout(10);

                            System.out.println("paho-client connecting to broker: " + broker);
                            sampleClient.connect(options);
                            System.out.println("paho-client connected to broker " + sampleClient.getClientId());

                            sampleClient.subscribe(topic, new IMqttMessageListener() {
                                @Override
                                public void messageArrived(final String string, final MqttMessage mm) throws Exception {
                                    receivedMessageListener.onReceived(mm);
                                }
                            });

                            final MqttCallback callback = new MqttCallback() {

                                @Override
                                public void connectionLost(Throwable t) {
                                    connect();
                                }

                                @Override
                                public void messageArrived(String topic, MqttMessage mm) throws Exception {
                                }

                                @Override
                                public void deliveryComplete(IMqttDeliveryToken token) {
                                }
                            };

                            sampleClient.setCallback(callback);

                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                System.err.println("Reconnecting in 5000 ms...");
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                            }
                        }
                    }

                }
            }).start();

        }
    }

    public void disconnect() {
        if (sampleClient.isConnected()) {
            try {
                keepDisconnected = true;
                sampleClient.disconnect();
                System.out.println("paho-client disconnected");
            } catch (MqttException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void enviar(final String text) throws NotConnectedException, Exception {

        // Creating a MQTT Client using Eclipse Paho
        try {

            if (sampleClient.isConnected()) {

                System.out.println("paho-client publishing message: " + text);

                final MqttMessage message = new MqttMessage(text.getBytes());
                message.setQos(qos);
                message.setRetained(true);

                sampleClient.publish(topic, message);
                System.out.println("paho-client message published");

            } else {
                throw new NotConnectedException();
            }

        } catch (Exception me) {
            me.printStackTrace();
            throw me;
        }

    }

    public static class NotConnectedException extends Exception {

    }

    public static interface ReceivedMessageListener {

        void onReceived(MqttMessage message);

    }
}

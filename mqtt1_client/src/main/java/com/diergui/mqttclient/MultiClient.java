/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diergui.mqttclient;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author Diego
 */
public class MultiClient {

    private int hilos;

    public MultiClient(int hilos) {
        this.hilos = hilos;

        for (int i = 0; i < hilos; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        MqttClientWrapper client = new MqttClientWrapper(new MqttClientWrapper.ReceivedMessageListener() {

                            @Override
                            public void onReceived(MqttMessage message) {
                                System.out.println("message: " + new String(message.getPayload()));
                            }

                        });

                        final String publisherId = UUID.randomUUID().toString();

                        client.initialize(publisherId);

                        while (true) {

                            try {
                                client.enviar(publisherId + " " + new Date().toString());
                            } catch (Exception ex) {
                            }

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }).start();

            try {
                Thread.sleep(usingMathClass());
            } catch (InterruptedException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    static int usingMathClass() {
        double randomDouble = Math.random();
        randomDouble = randomDouble * 1000 + 200;
        int randomInt = (int) randomDouble;
        return (randomInt);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diergui.mqtt1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.ClasspathResourceLoader;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.IResourceLoader;
import io.moquette.server.config.ResourceLoaderConfig;
import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;

public class Main {

    public static final String ENCODING = "UTF-8";
    public static Charset enconding;

    static class PublisherListener extends AbstractInterceptHandler {

        @Override
        public void onPublish(InterceptPublishMessage message) {
            final ByteBuf payload = message.getPayload();

            final String dec = payload.hasArray() ? new String(payload.array()) : payload.toString(enconding);
            System.out.println("REC: \n"
                    + "Client: " + message.getClientID() + "\n"
                    + "Topic: " + message.getTopicName() + "\n"
                    + "Content: " + dec);
            System.out.println(dec);
        }

        @Override
        public String getID() {
            return "listener";
        }

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        enconding = Charset.forName(ENCODING);
        IResourceLoader classpathLoader = new ClasspathResourceLoader();
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader, IConfig.DEFAULT_CONFIG);

        final Server mqttBroker = new Server();

        final List<? extends InterceptHandler> userHandlers = Arrays.asList(new PublisherListener());
        mqttBroker.startServer(classPathConfig, userHandlers);

        System.out.println("moquette mqtt broker started, press ctrl-c to shutdown..");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("stopping moquette mqtt broker..");
                mqttBroker.stopServer();
                System.out.println("moquette mqtt broker stopped");
            }
        });

    }
}

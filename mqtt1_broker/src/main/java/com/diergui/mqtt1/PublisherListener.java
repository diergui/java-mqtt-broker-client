/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.diergui.mqtt1;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;

/**
 *
 * @author Diego
 */
public class PublisherListener extends AbstractInterceptHandler {

    @Override
    public void onPublish(InterceptPublishMessage message) {
        System.out.println("moquette mqtt broker message intercepted, topic: " + message.getTopicName()
                + ", content: " + new String(message.getPayload().array()));
    }

    @Override
    public String getID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

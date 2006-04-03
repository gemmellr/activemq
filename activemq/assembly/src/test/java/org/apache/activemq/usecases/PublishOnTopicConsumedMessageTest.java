/**
 *
 * Copyright 2005-2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;

import org.apache.activemq.test.JmsTopicSendReceiveWithTwoConnectionsTest;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * @version $Revision: 1.1.1.1 $
 */
public class PublishOnTopicConsumedMessageTest extends JmsTopicSendReceiveWithTwoConnectionsTest {
    private MessageProducer replyProducer;


    public synchronized void onMessage(Message message) {

        // lets resend the message somewhere else
        try {
            Message msgCopy = (Message)((org.apache.activemq.command.Message)message).copy();
            replyProducer.send(msgCopy);

            //System.out.println("Sending reply: " + message);
            super.onMessage(message);
        }
        catch (JMSException e) {
            System.out.println("Failed to send message: " + e);
            e.printStackTrace();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();

        Destination replyDestination = null;

        if (topic) {
            replyDestination = receiveSession.createTopic("REPLY." + getSubject());
        }
        else {
            replyDestination = receiveSession.createQueue("REPLY." + getSubject());
        }

        replyProducer = receiveSession.createProducer(replyDestination);
        System.out.println("Created replyProducer: " + replyProducer);

    }
}

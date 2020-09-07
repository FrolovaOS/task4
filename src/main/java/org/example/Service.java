package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Service extends MessageProducerSupport implements MessageHandler{
    User user;

    @Value("${output-topic}")
    private String outputTopic;

    public void handleMessage(Message<?> message) throws MessagingException {

        JsonParser users = new JsonParser();
        this.user = users.getUser(message.getPayload().toString());
        if (user != null) {
            String messages = users.getJson(user);
            Message message2 = MessageBuilder.withPayload(messages).setHeader(KafkaHeaders.TOPIC, outputTopic).build();
            sendMessage(message2);
        }
    }
}

package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class AppService {

    User user;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(groupId = "testGroup02",topics = {"topic1"},containerFactory = "singleFactory")
    public void dataProcessing(String message) throws JsonProcessingException {
        JsonParser users = new JsonParser();
        this.user=users.getUser(message);

        String messages = users.getJson(user);
        kafkaTemplate.send("topic2",messages);
    }
}

package org.example;

import java.sql.Timestamp;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {


    public User getUser(String response)  {
        ObjectMapper objectMapper = new ObjectMapper();
        User  user = null;

        try {
            user = objectMapper.readValue(response, User.class);
        } catch (JsonProcessingException e) {
            Logger log = Logger.getLogger(JsonParser.class.getName());
            log.info("Invalid data");
            log.info(e.getMessage());
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        user.setTimestamp(timestamp.getTime());
        return  user;
    }

    public String getJson(User user){
        ObjectMapper objectMapper = new ObjectMapper();

        String  message = null;
        try {
            message = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            Logger log = Logger.getLogger(JsonParser.class.getName());
            log.info("Invalid data");
            log.info(e.getMessage());
        }

        return message;
    }
}

package org.example;

import org.example.db.UserDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration

public class Service extends MessageProducerSupport implements MessageHandler{

    private User user;

    private static ConcurrentMap<Integer,String> users =new ConcurrentHashMap<>(1);

    private static int id;

    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private ConfigProperties properties;

    @Autowired
    private JsonParser jsonParser;


    @Bean
    public void loadUsers()
    {
        List<User> userFromBd = userDao.loadAllUser();
        for(User user : userFromBd)
        {
            String record="{\"firstName\": \""   +  user.getFirstName()  +   "\", \"lastName\": \""  +   user.getLastName()  +   "\", \"age\": "   +   user.getAge()  +    ", \"role\": \""  +  user.getRole()   +    "\"}";

            users.put(user.getId(),record);
        }
    }

    public void handleMessage(Message<?> message) throws MessagingException {
        this.user = jsonParser.getUser(message.getPayload().toString());
        String record = message.getPayload().toString();
        Message message2 = null;
        if(users.containsValue(record)) {
            for(Map.Entry<Integer,String> entry : users.entrySet())
            {
                String k=entry.getValue();
                if(record.equals(k) )
                {
                    id=entry.getKey();
                }
            }
            user.setId(id);
            String messages = jsonParser.getJson(user);
            message2 = MessageBuilder.withPayload(messages).setHeader(KafkaHeaders.TOPIC, properties.getUsertopic()).setHeader("sending","0").build();
        }
        else if (!users.containsValue(record)){
            id=users.size()+1;
            user.setId(id);
            users.put(id,record);
            String messages = jsonParser.getJson(user);
            message2 = MessageBuilder.withPayload(messages).setHeader(KafkaHeaders.TOPIC, properties.getUsertopic()).setHeader("sending","1").build();
        }

             sendMessage(message2);
    }
}

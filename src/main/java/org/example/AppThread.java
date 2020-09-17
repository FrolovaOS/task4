package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.*;

@Configuration
@EnableIntegration
@EnableAutoConfiguration
public class AppThread extends MessageProducerSupport implements MessageHandler {

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    @Value("${staticstopic}")
    private String staticstopic;

    private static ConcurrentMap<String,Integer> statics =new ConcurrentHashMap<>(1);

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Bean
    public ThreadPoolTaskExecutor initExecutor(){
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(25);
        executor.initialize();
        return executor;
    }

    @Autowired
    DirectChannel sendStatics;

    @Bean
    public DirectChannel sendStatics(){return new DirectChannel();}




    @Scheduled(fixedDelay = 100000)
    public void showStatics(){
        executor.execute(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            for(Map.Entry<String,Integer> entry : statics.entrySet())
            {
                JsonParser user = new JsonParser();
                User user2 =user.getUser(entry.getKey());

                user2.setTimestamp(timestamp.getTime());
                user2.setCount(entry.getValue());
                String info ="";
                try {
                     info = objectMapper.writeValueAsString(user2);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                Message message = MessageBuilder.withPayload(info).build();
                sendStatics.send(message);
            }
            statics.clear();
            timestamp = new Timestamp(System.currentTimeMillis());
        });
    }
    @Bean
    public IntegrationFlow staticsToKafka() {
        return IntegrationFlows
                .from(sendStatics)
                .handle(Kafka.outboundChannelAdapter(new DefaultKafkaProducerFactory<>(properties.buildProducerProperties())).topic(staticstopic))
                .get();
    }

    @Autowired
    private KafkaProperties properties;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        JsonParser user = new JsonParser();
        User user2 =user.getUser(message.getPayload().toString());

        String record = user.getJson(user2);
        Integer oldValue=statics.get(record);
        if(oldValue!=null)
        {
            statics.replace(record,oldValue+1);
        }
        else statics.put(record,1);

        sendMessage(message);
    }
}

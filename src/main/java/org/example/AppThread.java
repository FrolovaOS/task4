package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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

    private DirectChannel sendStatics;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    private static ConcurrentMap<String,Integer> statics =new ConcurrentHashMap<>(1);

    @Autowired
    private ConfigProperties properties;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private JsonParser jsonParser;

    @Bean
    public ThreadPoolTaskExecutor initExecutor(){
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(25);
        executor.initialize();
        return executor;
    }



    @Bean
    public void sendStatics(){ sendStatics = new DirectChannel();}


    @Scheduled(fixedDelay = 100000)
    public void showStatics(){
        executor.execute(() -> {

            for(Map.Entry<String,Integer> entry : statics.entrySet())
            {
                User user =jsonParser.getUser(entry.getKey());

                user.setTimestamp(timestamp.getTime());
                user.setCount(entry.getValue());

                String info =jsonParser.getJson(user);

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
                .handle(Kafka.outboundChannelAdapter(new DefaultKafkaProducerFactory<>(properties.getProperties().buildProducerProperties())).topic(properties.getStaticstopic()))
                .get();
    }



    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        User user =jsonParser.getUser(message.getPayload().toString());
        String record = jsonParser.getJson(user);
        Integer oldValue=statics.get(record);

        if(oldValue!=null)
        {
            statics.replace(record,oldValue+1);
        }
        else statics.put(record,1);

        sendMessage(message);
    }
}

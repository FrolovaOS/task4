package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.*;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.messaging.Message;

@Configuration
@EnableKafka
@EnableIntegration
@EnableAutoConfiguration
public class KafkaConsumerConfig {


    @Value("${input-topic}")
    private String inputTopic;

    @Value("${usertopic}")
    private String usertopic;

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private Service service;

    @Autowired
    private AppThread appThread;


    @Bean
    public IntegrationFlow readFromKafka() {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter( new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties()),inputTopic))
                .handle(service)
                .handle(appThread)
                //.filter(("headers'[sending]' == '1'"))
                .filter(Message.class, m ->m.getHeaders().get("sending")=="1")
                .handle(Kafka.outboundChannelAdapter(new DefaultKafkaProducerFactory<>(properties.buildProducerProperties())).topic(usertopic))
                .get();
    }

}

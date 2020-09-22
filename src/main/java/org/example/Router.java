package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
public class Router {

    @Autowired
    ConfigProperties properties;


    @Autowired
    private Service service;

    @Autowired
    private AppThread appThread;


    @Bean
    public IntegrationFlow readFromKafka() {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter( new DefaultKafkaConsumerFactory<>(properties.getProperties().buildConsumerProperties()),properties.getInputtopic()))
                .handle(service)
                .handle(appThread)
                .filter(Message.class, m ->m.getHeaders().get("sending")=="1")
                .handle(Kafka.outboundChannelAdapter(new DefaultKafkaProducerFactory<>(properties.getProperties().buildProducerProperties())).topic(properties.getUsertopic()))
                .get();
    }

}

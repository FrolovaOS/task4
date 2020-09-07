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

@Configuration
@EnableKafka
@EnableIntegration
@EnableAutoConfiguration
public class KafkaConsumerConfig {

    //@Qualifier("input-topic")
    //@Value("bean.input-topic")

    @Value("${input-topic}")
    private String inputTopic;

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    @Value("${output-topic}")
    private String outputTopic;

    @Autowired
    private KafkaProperties properties;
    @Autowired
    private Service service;


    @Bean
    public IntegrationFlow readFromKafka() {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter( new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties()),inputTopic))
                .channel("fromKafka")
                .handle(service)
                .handle(Kafka.outboundChannelAdapter(new DefaultKafkaProducerFactory<>(properties.buildProducerProperties())).topic(outputTopic))
                .get();
    }

}

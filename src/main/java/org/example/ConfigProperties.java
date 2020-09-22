package org.example;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "topics")
@EnableAutoConfiguration
@Data
public class ConfigProperties {
    private String staticstopic;
    private String inputtopic;
    private String usertopic;

    @Autowired
    private KafkaProperties properties;

}

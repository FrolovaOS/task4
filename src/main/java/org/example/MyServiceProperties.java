package org.example;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix ="kafka")
public class MyServiceProperties {

    private String server;

    private String groupId;

    private String reset;

    private String clientId;

    private String valueDeserializer;

    private String valueSerializer;

    private String keyDeserializer;

    private String keySerializer;

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }




    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    public String getServer() {
        return server;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getReset() {
        return reset;
    }

    public String getClientId() {
        return clientId;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }
}

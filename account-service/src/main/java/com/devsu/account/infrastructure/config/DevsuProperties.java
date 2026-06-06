package com.devsu.account.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "devsu")
public class DevsuProperties {

    @NestedConfigurationProperty
    private Kafka kafka = new Kafka();

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public static class Kafka {

        private String topicClientEvents = "devsu.client.events";
        private String consumerGroup = "account-service-client-events";

        public String getTopicClientEvents() {
            return topicClientEvents;
        }

        public void setTopicClientEvents(String topicClientEvents) {
            this.topicClientEvents = topicClientEvents;
        }

        public String getConsumerGroup() {
            return consumerGroup;
        }

        public void setConsumerGroup(String consumerGroup) {
            this.consumerGroup = consumerGroup;
        }
    }
}

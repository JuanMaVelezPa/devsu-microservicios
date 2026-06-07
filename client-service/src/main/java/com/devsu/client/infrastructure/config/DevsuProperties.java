package com.devsu.client.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "devsu")
public class DevsuProperties {

    @NestedConfigurationProperty
    private Kafka kafka = new Kafka();

    @NestedConfigurationProperty
    private Outbox outbox = new Outbox();

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public Outbox getOutbox() {
        return outbox;
    }

    public void setOutbox(Outbox outbox) {
        this.outbox = outbox;
    }

    public static class Kafka {

        private String topicClientEvents = "devsu.client.events";

        public String getTopicClientEvents() {
            return topicClientEvents;
        }

        public void setTopicClientEvents(String topicClientEvents) {
            this.topicClientEvents = topicClientEvents;
        }
    }

    public static class Outbox {

        private long publishIntervalMs = 3000;
        private int batchSize = 50;
        private boolean publisherEnabled = true;

        public long getPublishIntervalMs() {
            return publishIntervalMs;
        }

        public void setPublishIntervalMs(long publishIntervalMs) {
            this.publishIntervalMs = publishIntervalMs;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public boolean isPublisherEnabled() {
            return publisherEnabled;
        }

        public void setPublisherEnabled(boolean publisherEnabled) {
            this.publisherEnabled = publisherEnabled;
        }
    }
}

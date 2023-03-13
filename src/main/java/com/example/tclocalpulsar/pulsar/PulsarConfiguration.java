package com.example.tclocalpulsar.pulsar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.reactive.core.ReactiveMessageConsumerBuilderCustomizer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PulsarConfiguration {

    @Value("${spring.pulsar.producer.topic-name}")
    String topic;

    @Value("${spring.pulsar.consumer.subscription-name}")
    String subscriptionName;

    @Value("${spring.pulsar.consumer.consumer-name}")
    String consumerName;

    @Bean
    ReactiveMessageConsumerBuilderCustomizer<Message<byte[]>> noteEventsConsumerCustomizer() {
        return builder ->
            builder
                .topic(topic)
                .subscriptionName(subscriptionName)
                .consumerName(consumerName)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .subscriptionType(SubscriptionType.Key_Shared);
    }
}

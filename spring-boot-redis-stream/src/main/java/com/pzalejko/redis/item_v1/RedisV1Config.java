package com.pzalejko.redis.item_v1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;

@Configuration
class RedisV1Config {

    @Value("${my-stream.stream-key-v1}")
    private String streamKey;

    @Bean
    Subscription subscriptionV1(RedisConnectionFactory connectionFactory, StreamListener<String, ObjectRecord<String, ItemV1>> streamListener) {
        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofMillis(100))
                .targetType(ItemV1.class)
                .batchSize(1)
                .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, ItemV1>> container = StreamMessageListenerContainer
                .create(connectionFactory, options);

        Subscription subscription = container.receive(
                StreamOffset.fromStart(streamKey),
                streamListener
        );

        container.start();
        return subscription;
    }
}
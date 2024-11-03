package com.gmail.pzalejko.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class StreamProducer implements Closeable {

    private final String redisAddress;
    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public StreamProducer(String redisAddress) {
        this.redisAddress = Objects.requireNonNull(redisAddress);
    }

    public void init() {
        client = RedisClient.create(redisAddress);
        connection = client.connect();
    }

    public void close() {
        if (client != null) {
            connection.close();
            client.close();
        }
    }

    public void send(String streamName, Message message) {
        Objects.requireNonNull(streamName);
        Objects.requireNonNull(message);
        if (streamName.isBlank()) {
            throw new IllegalArgumentException("streamName is blank");
        }
        if (Objects.isNull(connection)) {
            throw new IllegalStateException("Connection not established. Call the 'init' method first.");
        }

        RedisAsyncCommands<String, String> async = connection.async();
        Map<String, String> body = Collections.singletonMap(message.key, message.body);
        async.xadd(streamName, body);
    }

    public record Message(String key, String body) {

        public Message {
            Objects.requireNonNull(body);
            Objects.requireNonNull(key);

            if (body.isBlank()) {
                throw new IllegalArgumentException("body is blank");
            }
            if (key.isBlank()) {
                throw new IllegalArgumentException("key is blank");
            }
        }
    }
}

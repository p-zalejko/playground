package com.gmail.pzalejko.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisStreamCommands;

import java.io.Closeable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class StreamConsumer implements Closeable {


    @FunctionalInterface
    public interface Consumer {
        void accept(Map<String, String> message);
    }

    private final String redisAddress;
    private final String streamName;
    private final String initOffset;
    private final Consumer consumer;
    private final ExecutorService executorService;

    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public StreamConsumer(String redisAddress, String streamName, Consumer consumer, String initOffset) {
        this(redisAddress, streamName, consumer, initOffset, Executors.newSingleThreadExecutor());
    }

    public StreamConsumer(String redisAddress, String streamName, Consumer consumer, String initOffset, ExecutorService executorService) {
        this.redisAddress = Objects.requireNonNull(redisAddress);
        this.streamName = Objects.requireNonNull(streamName);
        this.initOffset = Objects.requireNonNull(initOffset);
        this.consumer = Objects.requireNonNull(consumer);
        this.executorService = Objects.requireNonNull(executorService);
        if (streamName.isBlank()) {
            throw new IllegalArgumentException("streamName is blank");
        }
    }

    public void init() {
        client = RedisClient.create(redisAddress);
        connection = client.connect();
        executorService.submit(this::consume);
    }

    public void close() {
        if (client != null) {
            connection.close();
            client.close();
            executorService.shutdownNow();
        }
    }

    private void consume() {
        if (Objects.isNull(connection)) {
            throw new IllegalStateException("Connection not established. Call the 'init' method first.");
        }

        RedisStreamCommands<String, String> commands = connection.sync();
        String lastSeenMessage = initOffset;

        while (true) {
            List<StreamMessage<String, String>> messages = commands.xread(
                    XReadArgs.Builder.block(Duration.ofSeconds(1)),
                    XReadArgs.StreamOffset.from(streamName, lastSeenMessage)
            );

            for (StreamMessage<String, String> message : messages) {
                lastSeenMessage = message.getId();

                consumer.accept(message.getBody());
            }
        }
    }
}




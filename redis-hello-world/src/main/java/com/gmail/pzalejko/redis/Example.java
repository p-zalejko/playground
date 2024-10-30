package com.gmail.pzalejko.redis;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStreamCommands;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs.Builder;
import io.lettuce.core.XReadArgs.StreamOffset;

public class Example {

    public static void main(String[] args) throws InterruptedException {
        String redis = "redis://192.168.2.235:30007";
        String streamName = "helloStream";

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> produce(redis, streamName));
        executorService.submit(() -> consume(redis, streamName));

        Thread.sleep(20_000);
        executorService.shutdownNow();
    }


    private static void produce(String redis, String streamName) {
        try (RedisClient client = RedisClient.create(redis)) {
            var connection = client.connect();
            RedisAsyncCommands<String, String> async = connection.async();

            while (true) {
                Map<String, String> body = Collections.singletonMap("time", LocalDateTime.now().toString());
                async.xadd(streamName, body);

                _wait();
            }
        }
    }

    private static void consume(String redis, String streamName) {
        try (RedisClient client = RedisClient.create(redis)) {
            StatefulRedisConnection<String, String> connection = client.connect();
            RedisStreamCommands<String, String> commands = connection.sync();

            String lastSeenMessage = "0-0";
            while (true) {
                List<StreamMessage<String, String>> messages = commands.xread(
                        Builder.block(Duration.ofSeconds(1)),
                        StreamOffset.from(streamName, lastSeenMessage)
                );

                for (StreamMessage<String, String> message : messages) {
                    lastSeenMessage = message.getId();
                    System.out.println("Received: " + message);
                }
            }
        }
    }

    private static void _wait() {
        try {
            Random random = new Random();
            Thread.sleep(random.nextInt(2_000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

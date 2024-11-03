package com.gmail.pzalejko.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Testcontainers
public class AppTest {

    @Container
    public static final GenericContainer REDIS = new GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Test
    void top_level_container_should_be_running() {
        assertThat(REDIS.isRunning()).isTrue();
    }

    @Test
    public void shouldSendAndReceiveMessages() {
        //given
        String host = REDIS.getHost();
        Integer port = REDIS.getFirstMappedPort();
        String redisAddress = "redis://" + host + ":" + port;
        String streamName = "helloMyStream";


        try (var producer = new StreamProducer(redisAddress)) {
            var testConsumer = new TestConsumer();
            try (var consumer = new StreamConsumer(redisAddress, streamName, testConsumer, "0-0")) {
                producer.init();
                consumer.init();

                // when
                List<StreamProducer.Message> messages = IntStream.range(0, 10)
                        .mapToObj(Integer::toString)
                        .map(i -> new StreamProducer.Message("myKey", i))
                        .toList();

                messages.forEach(i -> producer.send(streamName, i));

                // then
                await()
                        .atMost(Durations.TEN_SECONDS)
                        .with()
                        .pollInterval(Durations.ONE_HUNDRED_MILLISECONDS)
                        .until(() -> testConsumer.containsAll(messages));

            }
        }
    }

    private static class TestConsumer implements StreamConsumer.Consumer {

        List<Map<String, String>> messages = new ArrayList<>();

        @Override
        public void accept(Map<String, String> message) {
            messages.add(message);
        }

        public boolean containsAll(List<StreamProducer.Message> expectedMessages) {
            for (StreamProducer.Message message : expectedMessages) {
                String key = message.key();
                String body = message.body();

                List<Map.Entry<String, String>> list = messages.stream()
                        .flatMap(i -> i.entrySet().stream())
                        .toList();

                var withSameKey = list.stream()
                        .filter(e -> e.getKey().equals(key))
                        .toList();

                if (withSameKey.isEmpty()) {
                    return false;
                }

                var exists = withSameKey.stream()
                        .anyMatch(i -> i.getValue().equals(body));
                if (!exists) {
                    return false;
                }
            }
            return true;
        }
    }
}

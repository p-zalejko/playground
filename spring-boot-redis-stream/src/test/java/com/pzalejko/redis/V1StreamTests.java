package com.pzalejko.redis;

import com.pzalejko.redis.item_v1.ItemV1;
import com.pzalejko.redis.item_v1.ItemV1Repository;
import com.pzalejko.redis.item_v2.ItemV2;
import com.pzalejko.redis.item_v2.ItemV2Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Testcontainers
@SuppressWarnings("all")
class V1StreamTests {

    @Container
    @ServiceConnection
    public static final GenericContainer REDIS = new GenericContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Value("${my-stream.stream-key-v1}")
    private String streamKeyV1;

    @Value("${my-stream.stream-key-v2}")
    private String streamKeyV2;

    @Autowired
    StreamProducer streamProducer;

    @Autowired
    ItemV1Repository repositoryV1;
    @Autowired
    ItemV2Repository repositoryV2;

    @AfterEach
    void tearDown() {
        streamProducer.clear(streamKeyV1);
        streamProducer.clear(streamKeyV2);
    }

    @Test
    void redisShouldBeRunning() {
        assertThat(REDIS.isRunning()).isTrue();
    }

    @Test
    public void shouldStreamMessagesV1() {
        //given
        var items = IntStream.range(0, 10)
                .mapToObj(i -> new ItemV1(UUID.randomUUID().toString()))
                .toList();

        // when
        items.forEach(i -> streamProducer.send(i, streamKeyV1));

        // them
        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    assertThat(repositoryV1.getItems()).containsAll(items);
                    assertThat(streamProducer.getQueuedItemsSize(streamKeyV1)).isEqualTo(0);
                });
    }

    @Test
    public void shouldStreamMessagesV2() {
        //given
        var items = IntStream.range(0, 10)
                .mapToObj(i -> new ItemV2(UUID.randomUUID().toString()))
                .toList();

        // when
        items.forEach(i -> streamProducer.send(i, streamKeyV2));

        // them
        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    assertThat(repositoryV2.getItems()).containsAll(items);
                    assertThat(streamProducer.getQueuedItemsSize(streamKeyV2)).isEqualTo(0);
                });
    }
}

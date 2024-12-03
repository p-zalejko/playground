package com.pzalejko.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamProducer {

    private final RedisTemplate<String, String> redisTemplate;

    public void send(Object item, String streamName) {
        ObjectRecord<String, Object> jobRecord = StreamRecords.newRecord()
                .ofObject(item)
                .withStreamKey(streamName);

        RecordId recordId = redisTemplate.opsForStream()
                .add(jobRecord);

        Objects.requireNonNull(recordId);
    }

    public long getQueuedItemsSize(String streamName) {
        return redisTemplate.opsForStream()
                .read(Object.class, StreamOffset.fromStart(streamName))
                .size();
    }

    public void clear(String streamName) {
        redisTemplate.opsForStream()
                .trim(streamName, 0);
    }
}
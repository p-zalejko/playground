package com.pzalejko.redis.item_v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class ItemV1Consumer implements StreamListener<String, ObjectRecord<String, ItemV1>> {

    private final RedisTemplate<String, String> redisTemplate;
    private final ItemV1Repository repository;

    @Override
    public void onMessage(ObjectRecord<String, ItemV1> record) {
        log.info("received a new message: {}", record);
        ItemV1 item = record.getValue();
        repository.add(item);

        redisTemplate.opsForStream()
                .delete(record);
    }
}
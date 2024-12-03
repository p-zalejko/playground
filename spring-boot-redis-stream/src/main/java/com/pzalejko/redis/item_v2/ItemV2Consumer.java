package com.pzalejko.redis.item_v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class ItemV2Consumer implements StreamListener<String, ObjectRecord<String, ItemV2>> {

    private final RedisTemplate<String, String> redisTemplate;
    private final ItemV2Repository repository;

    @Override
    public void onMessage(ObjectRecord<String, ItemV2> record) {
        log.info("received a new message: {}", record);
        ItemV2 item = record.getValue();
        repository.add(item);

        redisTemplate.opsForStream()
                .delete(record);
    }
}
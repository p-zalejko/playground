package com.pzalejko.redis.item_v1;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
class InMemoryItemV1Repository implements ItemV1Repository {

    private final List<ItemV1> items = new ArrayList<>();

    @Override
    public void add(ItemV1 item) {
        items.add(Objects.requireNonNull(item));
    }

    @Override
    public List<ItemV1> getItems() {
        return new ArrayList<>(items);
    }
}

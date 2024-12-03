package com.pzalejko.redis.item_v2;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
class InMemoryItemV2Repository implements ItemV2Repository {

    private final List<ItemV2> items = new ArrayList<>();

    @Override
    public void add(ItemV2 item) {
        items.add(Objects.requireNonNull(item));
    }

    @Override
    public List<ItemV2> getItems() {
        return new ArrayList<>(items);
    }
}

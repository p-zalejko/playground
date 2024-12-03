package com.pzalejko.redis.item_v1;

import java.util.List;

public interface ItemV1Repository {

    void add(ItemV1 itemV1);

    List<ItemV1> getItems();
}

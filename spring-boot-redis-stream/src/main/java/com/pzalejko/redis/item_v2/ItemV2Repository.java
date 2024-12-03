package com.pzalejko.redis.item_v2;

import java.util.List;

public interface ItemV2Repository {

    void add(ItemV2 itemV1);

    List<ItemV2> getItems();
}

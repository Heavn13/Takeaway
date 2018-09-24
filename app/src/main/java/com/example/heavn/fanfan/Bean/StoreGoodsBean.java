package com.example.heavn.fanfan.Bean;

/**
 * 商家的商品类，用于HashMap本地化存储，即本地存储顾客给每个商家添加的购物车
 * Created by Administrator on 2018/9/18 0018.
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ${GongWenbo} on 2018/6/4 0004.
 */
public class StoreGoodsBean implements Serializable {

    private int allCount;                         // 商品总数

    private HashMap<String, Integer> mHashMap;    // 每件商品的个数

    public StoreGoodsBean(HashMap<String, Integer> hashMap) {
        this.mHashMap = hashMap;
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            Integer value = entry.getValue();
            if (value != 0) {
                allCount += value;
            }
        }
    }

    public int getAllCount() {
        return allCount;
    }

    public HashMap<String, Integer> getHashMap() {
        return mHashMap;
    }

}
package com.example.heavn.fanfan.Util;

import android.os.Environment;

import com.example.heavn.fanfan.Bean.StoreGoodsBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/9/18 0018.
 */

public class ShoppingCarHistory {
    private static final String PATH        = Environment.getExternalStorageDirectory().getPath() + File.separator + "goods";
    private static final String FILE_FORMAT = ".txt";
    private static ShoppingCarHistory shoppingCarHistory;

    private ShoppingCarHistory() {

    }

    public static ShoppingCarHistory getInstance() {
        if (shoppingCarHistory == null) {
            synchronized (ShoppingCarHistory.class) {
                if (shoppingCarHistory == null) {
                    shoppingCarHistory = new ShoppingCarHistory();
                }
            }
        }
        return shoppingCarHistory;
    }

    /**
     * 添加商品缓存
     *
     * @param storeId 商品的id
     */
    public ShoppingCarHistory add(String storeId, StoreGoodsBean storeGoodsBean) {
        File file = new File(PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file.getAbsolutePath() + File.separator + storeId + FILE_FORMAT);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(storeGoodsBean);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 得到商品缓存
     *
     * @param storeId 商铺的id
     */
    public HashMap<String, Integer> get(String storeId) {
        File file = new File(PATH);
        if (!file.exists()) {
            return null;
        }
        FileInputStream FileInputStream = null;
        ObjectInputStream objectInputStream = null;
        StoreGoodsBean storeGoodsBean = null;
        try {
            FileInputStream = new FileInputStream(file.getAbsolutePath() + File.separator + storeId + FILE_FORMAT);
            objectInputStream = new ObjectInputStream(FileInputStream);
            storeGoodsBean = (StoreGoodsBean) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return storeGoodsBean.getHashMap();
    }

    /**
     * 获取商铺选择的总个数
     *
     * @param storeId 商铺id
     * @return
     */
    public int getAllGoodsCount(String storeId) {
        HashMap<String, Integer> hashMap = get(storeId);
        int allCount = 0;
        if (hashMap != null) {
            for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                Integer value = entry.getValue();
                if (value != 0) {
                    allCount += value;
                }
            }
        }
        return allCount;
    }

    /**
     * 删除商铺缓存,如果数量为0
     *
     * @param storeId 商铺的id
     */
    public ShoppingCarHistory delete(String storeId) {
        File file = new File(PATH, storeId + FILE_FORMAT);
        if (file.exists()) {
            file.delete();
        }
        return this;
    }
}

package com.example.heavn.fanfan.Util;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.heavn.fanfan.Bean.Address;
import com.example.heavn.fanfan.Bean.CustomerGoods;
import com.example.heavn.fanfan.Bean.CustomerOrder;
import com.example.heavn.fanfan.Bean.RiderOrder;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.Bean.SalesOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 全局变量类
 */

public class MyApp extends Application{
    public static RequestQueue queue;//volley框架使用的请求队列
    private String user_type;//用户类型，四个类型
    private String customer_phone,rider_phone,sales_phone,backstage_phone;//四个客户端的手机账号
    private String goods_type = "热销",goods_name;//商品的类型和名称
    //121.250.222.235
    //http://47.93.25.241:8080/Order
    private String url = "http://47.93.25.241:8080/Order";//后台方法的请求地址
    private SalesOrder salesOrder;//商家订单
    private RiderOrder riderOrder;//骑手订单
    private CustomerOrder customerOrder;//用户订单
    private Address address;//顾客地址
    private SalesDetail salesDetail;//商家详情
    private HashMap<String, Integer> hashMap;//用于存储购物车中缓存的商品
    private List<CustomerGoods> allGoodsList;//用于存顾客添加到购物车中的商品列表


    @Override
    public final void onCreate() {
         super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public List<CustomerGoods> getAllGoodsList() {
        return allGoodsList;
    }

    public void setAllGoodsList(List<CustomerGoods> allGoodsList) {
        this.allGoodsList = allGoodsList;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public HashMap<String, Integer> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, Integer> hashMap) {
        this.hashMap = hashMap;
    }

    public SalesDetail getSalesDetail() {
        return salesDetail;
    }

    public void setSalesDetail(SalesDetail salesDetail) {
        this.salesDetail = salesDetail;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public RiderOrder getRiderOrder() {
        return riderOrder;
    }

    public void setRiderOrder(RiderOrder riderOrder) {
        this.riderOrder = riderOrder;
    }

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static RequestQueue getHttpQueue() {
        return queue;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getRider_phone() {
        return rider_phone;
    }

    public void setRider_phone(String rider_phone) {
        this.rider_phone = rider_phone;
    }

    public String getSales_phone() {
        return sales_phone;
    }

    public void setSales_phone(String sales_phone) {
        this.sales_phone = sales_phone;
    }

    public String getBackstage_phone() {
        return backstage_phone;
    }

    public void setBackstage_phone(String backstage_phone) {
        this.backstage_phone = backstage_phone;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }
}

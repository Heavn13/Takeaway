package com.example.heavn.fanfan.Bean;

/**
 * 顾客订单类
 * Created by Administrator on 2018/9/19 0019.
 */

public class CustomerOrder {
    private String id,customer_user,rider_user,sales_user,customer_text,rider_text,sales_text,customer_username,rider_username,sales_username,sales_address,name,receive_phone,address,sales_head;
    private int customer_rank,rider_rank,sales_rank;
    private boolean receive,finish,punctuality;
    private long order_time,arrival_time;

    public CustomerOrder(String id, String customer_user, String rider_user, String sales_user, String customer_text, String rider_text, String sales_text,String rider_username, String sales_username, String sales_address, String name, String receive_phone, String address, int customer_rank, int rider_rank, int sales_rank, boolean receive, boolean finish, boolean punctuality, long order_time, long arrival_time) {
        this.id = id;
        this.customer_user = customer_user;
        this.rider_user = rider_user;
        this.sales_user = sales_user;
        this.customer_text = customer_text;
        this.rider_text = rider_text;
        this.sales_text = sales_text;
        this.rider_username = rider_username;
        this.sales_username = sales_username;
        this.sales_address = sales_address;
        this.name = name;
        this.receive_phone = receive_phone;
        this.address = address;
        this.customer_rank = customer_rank;
        this.rider_rank = rider_rank;
        this.sales_rank = sales_rank;
        this.receive = receive;
        this.finish = finish;
        this.punctuality = punctuality;
        this.order_time = order_time;
        this.arrival_time = arrival_time;
    }

    public String getSales_head() {
        return sales_head;
    }

    public void setSales_head(String sales_head) {
        this.sales_head = sales_head;
    }

    public String getSales_username() {
        return sales_username;
    }

    public void setSales_username(String sales_username) {
        this.sales_username = sales_username;
    }

    public String getSales_address() {
        return sales_address;
    }

    public void setSales_address(String sales_address) {
        this.sales_address = sales_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceive_phone() {
        return receive_phone;
    }

    public void setReceive_phone(String receive_phone) {
        this.receive_phone = receive_phone;
    }

    public String getCustomer_username() {
        return customer_username;
    }

    public void setCustomer_username(String customer_username) {
        this.customer_username = customer_username;
    }

    public String getRider_username() {
        return rider_username;
    }

    public void setRider_username(String rider_username) {
        this.rider_username = rider_username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_user() {
        return customer_user;
    }

    public void setCustomer_user(String customer_user) {
        this.customer_user = customer_user;
    }

    public String getRider_user() {
        return rider_user;
    }

    public void setRider_user(String rider_user) {
        this.rider_user = rider_user;
    }

    public String getSales_user() {
        return sales_user;
    }

    public void setSales_user(String sales_user) {
        this.sales_user = sales_user;
    }

    public String getCustomer_text() {
        return customer_text;
    }

    public void setCustomer_text(String customer_text) {
        this.customer_text = customer_text;
    }

    public String getRider_text() {
        return rider_text;
    }

    public void setRider_text(String rider_text) {
        this.rider_text = rider_text;
    }

    public String getSales_text() {
        return sales_text;
    }

    public void setSales_text(String sales_text) {
        this.sales_text = sales_text;
    }

    public int getCustomer_rank() {
        return customer_rank;
    }

    public void setCustomer_rank(int customer_rank) {
        this.customer_rank = customer_rank;
    }

    public int getRider_rank() {
        return rider_rank;
    }

    public void setRider_rank(int rider_rank) {
        this.rider_rank = rider_rank;
    }

    public int getSales_rank() {
        return sales_rank;
    }

    public void setSales_rank(int sales_rank) {
        this.sales_rank = sales_rank;
    }

    public boolean isReceive() {
        return receive;
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public boolean isPunctuality() {
        return punctuality;
    }

    public void setPunctuality(boolean punctuality) {
        this.punctuality = punctuality;
    }

    public long getOrder_time() {
        return order_time;
    }

    public void setOrder_time(long order_time) {
        this.order_time = order_time;
    }

    public long getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(long arrival_time) {
        this.arrival_time = arrival_time;
    }
}

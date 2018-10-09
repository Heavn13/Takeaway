package com.example.heavn.fanfan.Bean;

/**
 * 骑手类
 * Created by Administrator on 2018/9/10 0010.
 */

public class RiderBean {
    private String username,password,phone,head,health_certificate;
    private int order_count;
    private Boolean verify;

    public RiderBean() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getHealth_certificate() {
        return health_certificate;
    }

    public void setHealth_certificate(String health_certificate) {
        this.health_certificate = health_certificate;
    }

    public int getOrder_count() {
        return order_count;
    }

    public void setOrder_count(int order_count) {
        this.order_count = order_count;
    }

    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }
}

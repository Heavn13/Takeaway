package com.example.heavn.fanfan.Bean;

/**
 * 商家详细信息类
 * Created by Administrator on 2018/9/17 0017.
 */

public class SalesDetail {
    private String username,master, password, phone, address, head, summary;
    private Boolean verify;
    private float rank;
    private int sales_number;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public float getRank() {
        return rank;
    }

    public void setRank(float rank) {
        this.rank = rank;
    }

    public int getSales_number() {
        return sales_number;
    }

    public void setSales_number(int sales_number) {
        this.sales_number = sales_number;
    }
}

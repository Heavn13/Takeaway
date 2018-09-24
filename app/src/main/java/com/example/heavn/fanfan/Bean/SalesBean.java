package com.example.heavn.fanfan.Bean;

/**
 * 商家类
 * Created by Administrator on 2018/9/10 0010.
 */

public class SalesBean {
    private String username,master, password, phone, address, head, summary;
    private Boolean verify;

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
}

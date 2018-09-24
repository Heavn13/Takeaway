package com.example.heavn.fanfan.Bean;

/**
 * 顾客评论类
 * Created by Administrator on 2018/9/18 0018.
 */

public class CustomerComment {
    private String head,username,rank_text;
    private  int rank;
    private long time;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRank_text() {
        return rank_text;
    }

    public void setRank_text(String rank_text) {
        this.rank_text = rank_text;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

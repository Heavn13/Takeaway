package com.example.heavn.fanfan.Util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
/**
 * 活动继承的新类
 * Created by Administrator on 2018/9/10 0010.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.remove(this);
    }
}

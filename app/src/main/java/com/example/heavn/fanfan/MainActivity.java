package com.example.heavn.fanfan;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;

import cn.bmob.v3.Bmob;

/**
 * APP主界面，四个客户端
 * Created by Administrator on 2018/9/10 0010.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{
    private Button customer, rider, sales, backstage;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (MyApp)getApplication();

        Bmob.initialize(this,"6e75baf342075856490f2ebe984c75d7");

        /**
         * 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                this,
                new String[] {
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                },
                MY_PERMISSION_REQUEST_CODE
        );

        customer = findViewById(R.id.customer);
        customer.setOnClickListener(this);
        rider = findViewById(R.id.rider);
        rider.setOnClickListener(this);
        sales = findViewById(R.id.sales);
        sales.setOnClickListener(this);
        backstage = findViewById(R.id.backstage);
        backstage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.customer:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                app.setUser_type("customer_user");
                startActivity(intent);
                break;
            case R.id.rider:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                app.setUser_type("rider_user");
                startActivity(intent);
                break;
            case R.id.sales:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                app.setUser_type("sales_user");
                startActivity(intent);
                break;
            case R.id.backstage:
                intent = new Intent(MainActivity.this,LoginActivity.class);
                app.setUser_type("administrator");
                startActivity(intent);
                break;
            default:
                break;

        }
    }
}

package com.example.heavn.fanfan.Backstage;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.R;

/**
 * 后台审核主界面
 * Created by Administrator on 2018/9/10 0010.
 */

public class BackstageMainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{
    private RadioGroup radioGroup;
    private RadioButton rider,sales;
    private RiderFragment riderFragment;
    private SalesFragment salesFragment;
    //悬浮按钮，退出后台客户端
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_main);
        init();
    }

    //初始化碎片管理
    private void init(){
        riderFragment = new RiderFragment();
        salesFragment = new SalesFragment();
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        rider = findViewById(R.id.radioButton_rider);
        //默认商家审核界面
        sales = findViewById(R.id.radioButton_sales);
        sales.setChecked(true);

        floatingActionButton = findViewById(R.id.floatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId){
            case R.id.radioButton_rider:
                if (riderFragment == null) {
                    riderFragment = new RiderFragment();
                }
                transaction.replace(R.id.frameLayout, riderFragment);
                break;
            case R.id.radioButton_sales:
                if (salesFragment == null) {
                    salesFragment = new SalesFragment();
                }
                transaction.replace(R.id.frameLayout, salesFragment);
                break;
            default:
                break;
        }
        transaction.commit();// 事务提交
    }
}

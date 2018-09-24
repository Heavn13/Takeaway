package com.example.heavn.fanfan.Customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.heavn.fanfan.ChangePasswordActivity;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Rider.RiderVerifyActivity;
import com.example.heavn.fanfan.Util.ActivityCollector;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;

/**
 * 顾客系统设置页面
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerSettingActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private Button pay_password,login_password,detail,logout,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_setting);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        pay_password = findViewById(R.id.change_pay_password);
        pay_password.setOnClickListener(this);
        login_password = findViewById(R.id.change_login_password);
        login_password.setOnClickListener(this);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        address = findViewById(R.id.change_address);
        address.setOnClickListener(this);
        detail = findViewById(R.id.change_user_detail);
        detail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change_user_detail:
                intent = new Intent(this,CustomerDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.change_pay_password:
                intent = new Intent(this,CustomerChangePayPassword.class);
                startActivity(intent);
                break;
            case R.id.change_login_password:
                intent = new Intent(this,ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.change_address:
                intent = new Intent(this,CustomerAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                ActivityCollector.finishAll();
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}

package com.example.heavn.fanfan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Backstage.BackstageMainActivity;
import com.example.heavn.fanfan.Bean.CustomerBean;
import com.example.heavn.fanfan.Customer.CustomerChangePayPassword;
import com.example.heavn.fanfan.Customer.CustomerMainActivity;
import com.example.heavn.fanfan.Rider.RiderMainActivity;
import com.example.heavn.fanfan.Sales.SalesMainActivity;
import com.example.heavn.fanfan.Util.ActivityCollector;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 修改登录密码
 * Created by Administrator on 2018/9/17 0017.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private Button change;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private EditText old_password,new_password;
    private String s_old = "",s_new = "",type,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_password);

        app = (MyApp)getApplication();

        type = app.getUser_type();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        change = findViewById(R.id.change_password);
        change.setOnClickListener(this);

        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change_password:
                s_old = old_password.getText().toString();
                s_new = new_password.getText().toString();
                if(!s_new.equals("") && !s_old.equals("")){
                    change();
                    ActivityCollector.finishAll();
                    Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    private void change(){
        if(type.equals("customer_user")){
            phone = app.getCustomer_phone();
        }else if(type.equals("sales_user")){
            phone = app.getSales_phone();
        }else if(type.equals("rider_user")){
            phone = app.getRider_phone();
        }
        RequestBody requestBody = new FormBody.Builder().add("type",type).add("phone",phone).add("old_password",s_old).add("new_password",s_new).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/ChangePassword").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangePasswordActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                if(res.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this, "登录密码修改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePasswordActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

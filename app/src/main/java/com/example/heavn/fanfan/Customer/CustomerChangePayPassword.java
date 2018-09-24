package com.example.heavn.fanfan.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.CustomerBean;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.ActivityCollector;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
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
 * 顾客修改支付密码界面
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerChangePayPassword extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private Button change;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private EditText old_password,new_password;
    private TextView hint;
    private String s_old = "",s_new = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pay_password);

        app = (MyApp)getApplication();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        change = findViewById(R.id.change_password);
        change.setOnClickListener(this);

        hint = findViewById(R.id.hint);
        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);

        initView();

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
                if(!s_new.equals("")){
                    change();
                }else{
                    Toast.makeText(this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //初始化信息，判断是否已设置支付密码
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetCustomer").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CustomerChangePayPassword.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        if(res.equals("false")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CustomerChangePayPassword.this, "该账户查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomerBean customer = new Gson().fromJson(res,CustomerBean.class);
                                    if(customer.getPay_password().equals("")){
                                        old_password.setVisibility(View.GONE);
                                        hint.setVisibility(View.VISIBLE);
                                    }else {
                                        old_password.setVisibility(View.VISIBLE);
                                        hint.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    //修改支付密码
    private void change(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).add("old_password",s_old).add("new_password",s_new).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/ChangePayPassword").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerChangePayPassword.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CustomerChangePayPassword.this, "支付密码修改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerChangePayPassword.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

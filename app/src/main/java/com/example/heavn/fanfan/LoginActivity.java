package com.example.heavn.fanfan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Backstage.BackstageMainActivity;
import com.example.heavn.fanfan.Bean.CustomerBean;
import com.example.heavn.fanfan.Customer.CustomerMainActivity;
import com.example.heavn.fanfan.Rider.RiderMainActivity;
import com.example.heavn.fanfan.Sales.SalesMainActivity;
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
 * 登录页面
 * Created by Administrator on 2018/9/10 0010.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText username;
    private EditText password;
    public static CheckBox remember;
    private TextView forget;
    private String s_username,s_password,type;
    private MyApp app;
    private SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    private Button login,register;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (MyApp)getApplication();

        type = app.getUser_type();

        loadingDialog = new ProgressDialog(LoginActivity.this);
        loadingDialog.setMessage("正在登录, 请稍候...");
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(true);

        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        forget = findViewById(R.id.forget);
        forget.setOnClickListener(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        remember = findViewById(R.id.remember);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = preferences.getBoolean("remember_password",false);
        //记住密码
        if(isRemember){
            String account1 = preferences.getString("username","");
            String password1 = preferences.getString("password","");
            username.setText(account1);
            password.setText(password1);
            remember.setChecked(true);
        }
        else{
            username.setText("");
            password.setText("");
            remember.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.login:
                s_username = username.getText().toString();
                s_password = password.getText().toString();
                //记住密码
                editor = preferences.edit();
                if(remember.isChecked()){
                    editor.putBoolean("remember_password",true);
                    editor.putString("username", s_username);
                    editor.putString("password", s_password);
                }else{
                    editor.clear();
                }
                editor.commit();
                if(!s_username.equals("") && !s_password.equals("")){
                    loadingDialog.show();
                    //http请求
                    RequestBody requestBody = new FormBody.Builder().add("type",type).add("phone",s_username).add("password",s_password).build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url(app.getUrl()+"/Login").post(requestBody).build();
                    Call call = okHttpClient.newCall(request);
                    //执行Call
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                        loadingDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else if(res.equals("true")){
                                loadingDialog.dismiss();
                                //跳转到相应的客户端
                                Intent intent1;
                                if(type.equals("customer_user")){
                                    app.setCustomer_phone(s_username);
                                    intent1 = new Intent(LoginActivity.this,CustomerMainActivity.class);
                                    startActivity(intent1);
                                }else if(type.equals("sales_user")){
                                    app.setSales_phone(s_username);
                                    intent1 = new Intent(LoginActivity.this,SalesMainActivity.class);
                                    startActivity(intent1);
                                }else if(type.equals("rider_user")){
                                    app.setRider_phone(s_username);
                                    intent1 = new Intent(LoginActivity.this,RiderMainActivity.class);
                                    startActivity(intent1);
                                }else if(type.equals("administrator")){
                                    intent1 = new Intent(LoginActivity.this,BackstageMainActivity.class);
                                    startActivity(intent1);
                                }
                                finish();
                            }

                        }
                    });
                }
                break;
            case R.id.register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;

        }
    }
}

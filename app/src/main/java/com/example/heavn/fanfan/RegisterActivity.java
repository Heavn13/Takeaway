package com.example.heavn.fanfan;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;
import com.jkb.vcedittext.VerificationCodeEditText;

import java.io.IOException;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 注册页面，使用了Bmob的短信接口
 * Created by Administrator on 2018/9/10 0010.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private EditText phone;
    private EditText password;
    private VerificationCodeEditText code;
    private Button send;
    private Button register;
    private String s_phone = "";
    private String s_password = "";
    private String s_code = "";
    private ImageView back;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        app = (MyApp)getApplication();

        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        code = findViewById(R.id.code);

        send = findViewById(R.id.send);
        send.setOnClickListener(this);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.send:
                s_phone = phone.getText().toString();
                if(!s_phone.equals("") && s_phone.length() == 11){
                    //60s内只能发送一次
                    send.setEnabled(false);
                    new CountDownTimer(60000,1000){
                        @Override
                        public void onTick(long millisUntilFinished) {
                            send.setText(millisUntilFinished / 1000 + "s后获取");
                        }
                        @Override
                        public void onFinish() {
                            send.setEnabled(true);
                            send.setText("重新发送");
                        }
                    }.start();
                    BmobSMS.requestSMSCode(s_phone, "饭饭", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e == null){
                                Toast.makeText(getApplicationContext(),"短信验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(),"短信验证码发送失败，请稍后重试",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(this,"手机号码格式不正确",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                s_phone = phone.getText().toString();
                s_password = password.getText().toString();
                s_code = code.getText().toString();
                if(!s_password.equals("") && !s_code.equals("")){
                    BmobSMS.verifySmsCode(s_phone, s_code, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e == null){
                                //http请求
                                RequestBody requestBody = new FormBody.Builder().add("type",app.getUser_type()).add("phone",s_phone).add("username",s_phone).add("password",s_password).build();
                                Request.Builder builder = new Request.Builder();
                                Request request = builder.url(app.getUrl()+"/Register").post(requestBody).build();
                                Call call = okHttpClient.newCall(request);
                                //执行Call
                                call.enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 2000);//1.5秒后执行Runnable中的run方法
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(RegisterActivity.this, "该账号已存在", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }else{
                            Toast.makeText(getApplicationContext(),"短信验证码错误",Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }else{
                    Toast.makeText(this,"请把内容填写完整",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}

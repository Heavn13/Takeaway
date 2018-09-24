package com.example.heavn.fanfan.Backstage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.CircleImageView;
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
 * 后台审核骑手界面
 * Created by Administrator on 2018/9/11 0011.
 */

public class BackstageRiderVerifyActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private MyApp app;
    private CircleImageView head;
    private Button accept,reject;
    private TextView username,phone,health;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_rider_verify);

        app = (MyApp)getApplication();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        accept = findViewById(R.id.accept);
        accept.setOnClickListener(this);
        reject = findViewById(R.id.reject);
        reject.setOnClickListener(this);

        head = findViewById(R.id.head);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        health = findViewById(R.id.health_certificate);

        initView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.accept:
                verify(true);
                break;
            case R.id.reject:
                verify(false);
                break;
            default:
                break;
        }
    }

    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getBackstage_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetRider").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BackstageRiderVerifyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(BackstageRiderVerifyActivity.this, "该账户查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
//                    Log.e("json",res);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RiderBean rider = new Gson().fromJson(res,RiderBean.class);
                                    ImageDownloadTask imgTask = new ImageDownloadTask();
                                    if(rider.getHead() != null)
                                        imgTask.execute(rider.getHead(),head);
                                    username.setText(rider.getUsername());
                                    phone.setText(rider.getPhone());
                                    if(rider.getHealth_certificate() != null)
                                        health.setText(rider.getHealth_certificate());
                                    if(rider.getVerify()){
                                        accept.setText("审核已通过");
                                        accept.setEnabled(false);
                                        reject.setVisibility(View.GONE);
                                    }

                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    //审核操作，true审核通过，false审核不通过
    private void verify(Boolean verify){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getBackstage_phone()).add("verify",verify.toString()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/VerifyRider").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BackstageRiderVerifyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BackstageRiderVerifyActivity.this, "审核功能失效", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BackstageRiderVerifyActivity.this, "已完成对该骑手的审核", Toast.LENGTH_SHORT).show();
                    }
                });
                }
            }
        });
    }
}

package com.example.heavn.fanfan.Backstage;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesVerifyActivity;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.CircleImageView;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
import com.example.heavn.fanfan.Util.MyApp;
import com.google.gson.Gson;
import com.jkb.vcedittext.VerificationCodeEditText;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 后台审核商家界面
 * Created by Administrator on 2018/9/11 0011.
 */

public class BackStageSalesVerifyActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back;
    private MyApp app;
    private CircleImageView head;
    private Button accept,reject;
    private TextView username,phone,master,address,summary;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backstage_sales_verify);

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
        master = findViewById(R.id.master);
        address = findViewById(R.id.address);
        summary = findViewById(R.id.summary);

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
                sendMessage("恭喜你，你的账号已经被审核通过。你的店铺现在可以被顾客看见了，接下来，你可以开始管理你的店铺，为你的商店多添加一些商品吧。赶紧开始赚取你的第一桶金吧！",true);
                break;
            case R.id.reject:
                View view = getLayoutInflater().inflate(R.layout.dialog_verify_reason, null);
                final EditText editText = view.findViewById(R.id.content);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("请输入审核不通过的理由")//设置对话框的标题
                        .setView(view)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hideSoftKeyboard(BackStageSalesVerifyActivity.this);
                                String content = editText.getText().toString();
                                //审核不通过
                                //需要对传到后台的中文字符进行编码转化，否则传到后台会变成乱码
                                try{
                                    content = URLEncoder.encode(content,"utf-8");
                                }catch (UnsupportedEncodingException e){
                                    e.printStackTrace();
                                }
                                //发送系统消息
                                sendMessage("你的账号审核未通过，未通过的理由是："+content,false);
                                dialog.dismiss();
                                hideSoftKeyboard(BackStageSalesVerifyActivity.this);
                            }
                        }).create();
                dialog.show();
                break;
            default:
                break;
        }
    }

    //初始化商家审核信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getBackstage_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetSales").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BackStageSalesVerifyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(BackStageSalesVerifyActivity.this, "该账户查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
//                    Log.e("json",res);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SalesBean sales = new Gson().fromJson(res,SalesBean.class);
                                    ImageDownloadTask imgTask = new ImageDownloadTask();
                                    if(sales.getHead() != null)
                                        imgTask.execute(sales.getHead(),head);
                                    username.setText(sales.getUsername());
                                    phone.setText(sales.getPhone());
                                    phone.setEnabled(false);
                                    if(sales.getAddress() != null)
                                        address.setText(sales.getAddress());
                                    if(sales.getMaster() != null)
                                        master.setText(sales.getMaster());
                                    if(sales.getSummary() != null)
                                        summary.setText(sales.getSummary());
                                    if(sales.getVerify()){
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
        Request request = builder.url(app.getUrl()+"/VerifySales").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BackStageSalesVerifyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BackStageSalesVerifyActivity.this, "审核功能失效", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BackStageSalesVerifyActivity.this, "已完成对该商户的审核", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //发送审核结果的系统消息，true审核通过，false审核不通过
    private void sendMessage(String content, final Boolean verify){

        long date = System.currentTimeMillis();
        //判断审核是否通过，然后发送相应的系统消息给对应的用户
        RequestBody requestBody = new FormBody.Builder().add("date",""+date).add("type","sales")
                .add("user",app.getBackstage_phone()).add("content",content).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/AddMessage").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BackStageSalesVerifyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(BackStageSalesVerifyActivity.this, "审核功能失效", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(verify){
                                Log.e("verify","审核已通过");
                            }else{
                                Toast.makeText(BackStageSalesVerifyActivity.this, "已将审核不通过的理由发送至该商家", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    /**
     * 隐藏Dialog中的软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
    }


}

package com.example.heavn.fanfan.Customer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.CustomerBean;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Rider.RiderVerifyActivity;
import com.example.heavn.fanfan.Util.ActivityCollector;
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
 * 顾客个人详细信息的碎片
 * Created by Administrator on 2018/9/16 0016.
 */

public class CustomerDetailFragment extends Fragment implements View.OnClickListener {
    private CircleImageView head;
    private TextView username;
    private Button setting,logout;
    private LinearLayout detail;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_detail,container,false);

        app = (MyApp)getActivity().getApplication();

        head = view.findViewById(R.id.head);
        username = view.findViewById(R.id.username);

        detail = view.findViewById(R.id.background);
        detail.setOnClickListener(this);
        setting = view.findViewById(R.id.setting);
        setting.setOnClickListener(this);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        initView();

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.background:
                intent = new Intent(getActivity(),CustomerDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
                intent = new Intent(getActivity(),CustomerSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                ActivityCollector.finishAll();
                intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //初始化个人信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //http请求
                Log.e("phone",app.getCustomer_phone());
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetCustomer").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        if(res.equals("false")){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "该账户查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomerBean customer = new Gson().fromJson(res,CustomerBean.class);
                                    ImageDownloadTask imgTask = new ImageDownloadTask();
                                    if(!customer.getHead().equals(""))
                                        imgTask.execute(customer.getHead(),head);
                                    username.setText(customer.getUsername());
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }
}

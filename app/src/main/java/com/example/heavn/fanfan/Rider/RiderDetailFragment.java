package com.example.heavn.fanfan.Rider;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesVerifyActivity;
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
 * 骑手个人信息碎片
 * Created by Administrator on 2018/9/11 0011.
 */

public class RiderDetailFragment extends Fragment implements View.OnClickListener{
    private CircleImageView head;
    private TextView username,verify;
    private Button logout;
    private LinearLayout detail;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_detail,container,false);

        app = (MyApp)getActivity().getApplication();

        head = view.findViewById(R.id.head);
        username = view.findViewById(R.id.username);
        verify = view.findViewById(R.id.verify);

        detail = view.findViewById(R.id.background);
        detail.setOnClickListener(this);

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
                intent = new Intent(getActivity(),RiderVerifyActivity.class);
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
                Log.e("phone",app.getRider_phone());
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getRider_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetRider").post(requestBody).build();
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
                                    RiderBean rider = new Gson().fromJson(res,RiderBean.class);
                                    ImageDownloadTask imgTask = new ImageDownloadTask();
                                    if(rider.getHead() != null)
                                        imgTask.execute(rider.getHead(),head);
                                    username.setText(rider.getUsername());
                                    if(rider.getVerify()){
                                        verify.setText("已认证");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).start();

    }
}

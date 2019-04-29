package com.example.heavn.fanfan.Rider;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.Message;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.MessageActivity;
import com.example.heavn.fanfan.MessageAdapter;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesVerifyActivity;
import com.example.heavn.fanfan.Util.ActivityCollector;
import com.example.heavn.fanfan.Util.CircleImageView;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
import com.example.heavn.fanfan.Util.MyApp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private TextView username,verify,order_count,message_count;
    private Button logout;
    private ImageView message;
    private LinearLayout detail;
    private List<Message> messageList = new ArrayList<>();
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_detail,container,false);

        app = (MyApp)getActivity().getApplication();

        head = view.findViewById(R.id.head);
        username = view.findViewById(R.id.username);
        verify = view.findViewById(R.id.verify);
        order_count = view.findViewById(R.id.order_count);

        message = view.findViewById(R.id.message);
        message.setOnClickListener(this);
        message_count = view.findViewById(R.id.message_count);

        detail = view.findViewById(R.id.background);
        detail.setOnClickListener(this);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        initView();

        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                refreshLayout.setRefreshing(false);
            }
        });

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
            case R.id.message:
                intent = new Intent(getActivity(), MessageActivity.class);
                startActivity(intent);
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
                                    app.setRider_user(rider);
                                    ImageDownloadTask imgTask = new ImageDownloadTask();
                                    if(rider.getHead() != null)
                                        imgTask.execute(rider.getHead(),head);
                                    username.setText(rider.getUsername());
                                    if(rider.getVerify()){
                                        verify.setText("已认证");
                                    }
                                    order_count.setText(""+rider.getOrder_count());

                                    getMessageCount();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    //获取该用户所有未读系统消息的数量
    private void getMessageCount(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("user",app.getRider_phone()).add("type","rider").build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetMessageCount").post(requestBody).build();
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
                                    Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageList.clear();
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement i:jsonArray) {
                                        Message message = gson.fromJson(i,Message.class);
                                        messageList.add(message);
                                    }

                                    if(messageList.size() == 0){
                                        message_count.setVisibility(View.INVISIBLE);
                                    }else{
                                        message_count.setText(""+messageList.size());
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

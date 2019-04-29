package com.example.heavn.fanfan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.Message;
import com.example.heavn.fanfan.Util.BaseActivity;
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
 * 系统消息列表
 */

public class MessageActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private ImageView back;
    private ListView listView;
    private MessageAdapter adapter;
    private MyApp app;
    private List<Message> messageList = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SwipeRefreshLayout refreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        app = (MyApp)getApplication();


        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        listView = findViewById(R.id.message_list);
        listView.setOnItemClickListener(this);

        refreshLayout = findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                refreshLayout.setRefreshing(false);
            }
        });

        initView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    //listView的单个点击事件，即单个消息点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Message message = messageList.get(position);
        Log.e("date",""+message.getDate());
        updateRead(message.getDate());
        app.setMessage(message);
        // 要获取相应参数传值
        Intent intent = new Intent(MessageActivity.this, MessageContentActivity.class);
        startActivity(intent);
    }

    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = null;
                //区分是骑手还是商家收到的系统消息
                if(app.getUser_type().equals("sales_user")){
                    requestBody = new FormBody.Builder().add("user",app.getSales_phone()).add("type","sales").build();
                }else if(app.getUser_type().equals("rider_user")){
                    requestBody = new FormBody.Builder().add("user",app.getRider_phone()).add("type","rider").build();
                }
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetMessageItems").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MessageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MessageActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            runOnUiThread(new Runnable() {
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
                                    adapter = new MessageAdapter(MessageActivity.this, messageList);
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    refreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    //更新消息阅读状态
    private void updateRead(final Long time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = null;
                //区分是骑手还是商家收到的系统消息
                if(app.getUser_type().equals("sales_user")){
                    requestBody = new FormBody.Builder().add("date",""+time).add("user",app.getSales_phone()).add("type","sales").build();
                }else if(app.getUser_type().equals("rider_user")){
                    requestBody = new FormBody.Builder().add("date",""+time).add("user",app.getRider_phone()).add("type","rider").build();
                }
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/UpdateMessageRead").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MessageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(MessageActivity.this, "更新阅读状态失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                        }
                    }
                });
            }
        }).start();
    }

}

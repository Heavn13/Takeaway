package com.example.heavn.fanfan.Sales;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.heavn.fanfan.Backstage.RiderAdapter;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.Bean.SalesOrder;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
import com.example.heavn.fanfan.Util.MyApp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
 * 商家收到的订单列表页面
 * Created by Administrator on 2018/9/15 0015.
 */

public class SalesOrderActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private MyApp app;
    private ListView listView;
    private SalesOrderAdapter adapter;
    private List<SalesOrder> orderList = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SwipeRefreshLayout refreshLayout;//刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);

        app = (MyApp)getApplication();

        listView = findViewById(R.id.orderList);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        //刷新操作
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

    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getSales_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetSalesOrder").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SalesOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SalesOrderActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    orderList.clear();
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement sales:jsonArray) {
                                        SalesOrder order = gson.fromJson(sales,SalesOrder.class);
                                        orderList.add(order);
                                    }
                                    adapter = new SalesOrderAdapter(SalesOrderActivity.this,orderList);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            SalesOrder salesOrder = orderList.get(position);
                                            app.setSalesOrder(salesOrder);
                                            Intent intent = new Intent(SalesOrderActivity.this,SalesOrderDetailActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }
}

package com.example.heavn.fanfan.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.heavn.fanfan.Backstage.BackstageRiderVerifyActivity;
import com.example.heavn.fanfan.Backstage.RiderAdapter;
import com.example.heavn.fanfan.Bean.Address;
import com.example.heavn.fanfan.Bean.CustomerOrder;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.ChangePasswordActivity;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.ActivityCollector;
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
 * 顾客显示地址列表的界面
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerAddressActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ImageView back,add;
    private ListView listView;
    private CustomerAddressAdapter adapter;
    private MyApp app;
    private List<Address> addressList = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SwipeRefreshLayout refreshLayout;//刷新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        listView = findViewById(R.id.address_list);
        listView.setOnItemClickListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        add = findViewById(R.id.add);
        add.setOnClickListener(this);

        app = (MyApp)getApplication();

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
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.add:
                intent = new Intent(this,CustomerAddressAddActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //listView的单个点击事件，点击设置地址
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Address address = addressList.get(position);
        app.setAddress(address);
        Intent intent = new Intent(CustomerAddressActivity.this,CustomerAddressManageActivity.class);
        startActivity(intent);
    }

    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetAddressItems").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CustomerAddressActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(CustomerAddressActivity.this, "地址查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addressList.clear();
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement element:jsonArray) {
                                        Address address = gson.fromJson(element,Address.class);
                                        addressList.add(address);
                                    }
                                    adapter = new CustomerAddressAdapter(CustomerAddressActivity.this, addressList);
                                    listView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }


}

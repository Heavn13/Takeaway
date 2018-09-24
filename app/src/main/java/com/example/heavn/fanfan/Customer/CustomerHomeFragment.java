package com.example.heavn.fanfan.Customer;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.heavn.fanfan.Backstage.BackstageRiderVerifyActivity;
import com.example.heavn.fanfan.Backstage.RiderAdapter;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.MyApp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 顾客的商店列表的碎片
 * Created by Administrator on 2018/9/16 0016.
 */

public class CustomerHomeFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView;
    private CustomerSalesAdapter adapter;
    private MyApp app;
    private List<SalesDetail> salesList = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SwipeRefreshLayout refreshLayout;//刷新


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home,container,false);
        listView = view.findViewById(R.id.sales_list);
        listView.setOnItemClickListener(this);

        app = (MyApp)getActivity().getApplication();

        //刷新操作
        refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.blue));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                refreshLayout.setRefreshing(false);
            }
        });

        initView();
        return view;
    }

    //listView的单个点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SalesDetail sales = salesList.get(position);
        app.setSalesDetail(sales);
        //把里面的之清空
        HashMap<String,Integer> hashMap = app.getHashMap();
        if(hashMap != null){
            hashMap.clear();
        }
        // 要获取相应参数传值
        Intent intent = new Intent(getActivity(), CustomerSalesDetailActivity.class);
        startActivity(intent);

    }

    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetSalesDetailItems").post(requestBody).build();
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
                                    Toast.makeText(getActivity(), "商店列表查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    salesList.clear();
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement element:jsonArray) {
                                        SalesDetail sales = gson.fromJson(element,SalesDetail.class);
                                        salesList.add(sales);
                                    }
                                    adapter = new CustomerSalesAdapter(getActivity(), salesList);
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

}

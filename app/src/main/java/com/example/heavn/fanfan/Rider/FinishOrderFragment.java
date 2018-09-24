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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.RiderOrder;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesOrderDetailActivity;
import com.example.heavn.fanfan.Util.MyApp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/9/16 0016.
 */

public class FinishOrderFragment extends Fragment{
    private ListView listView;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SwipeRefreshLayout refreshLayout;
    private RiderOrderAdapter adapter;
    private List<RiderOrder> orderList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_order,container,false);
        listView = view.findViewById(R.id.orderList);

        app = (MyApp)getActivity().getApplication();

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

    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("rider_user",app.getRider_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetRiderFinishOrder").post(requestBody).build();
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
                                    orderList.clear();
                                    Log.e("content",res);
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement sales:jsonArray) {
                                        RiderOrder order = gson.fromJson(sales,RiderOrder.class);
                                        orderList.add(order);
                                    }
                                    adapter = new RiderOrderAdapter(getActivity(),orderList);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            RiderOrder order = orderList.get(position);
                                            app.setRiderOrder(order);
                                            Intent intent = new Intent(getActivity(),RiderOrderDetailActivity.class);
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

package com.example.heavn.fanfan.Sales;

import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Backstage.BackstageMainActivity;
import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.Bean.SalesOrder;
import com.example.heavn.fanfan.Customer.CustomerMainActivity;
import com.example.heavn.fanfan.LoginActivity;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Rider.RiderMainActivity;
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

import cn.bmob.v3.BmobUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 商家详细信息的碎片
 * Created by Administrator on 2018/9/11 0011.
 */

public class SalesDetailFragment extends Fragment implements View.OnClickListener {
    private CircleImageView head;
    private TextView username, verify;
    private Button logout;
    private ImageView order, bill;
    private TextView count,rank,sales;
    private LinearLayout detail;
    private List<SalesOrder> orderList = new ArrayList<>();
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_detail, container, false);

        app = (MyApp) getActivity().getApplication();

        head = view.findViewById(R.id.head);
        username = view.findViewById(R.id.username);
        verify = view.findViewById(R.id.verify);

        detail = view.findViewById(R.id.background);
        detail.setOnClickListener(this);
        count = view.findViewById(R.id.count);
        sales = view.findViewById(R.id.sales);
        rank = view.findViewById(R.id.rank);

        order = view.findViewById(R.id.order);
        order.setOnClickListener(this);

        bill = view.findViewById(R.id.bill);
        bill.setOnClickListener(this);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.background:
                intent = new Intent(getActivity(), SalesVerifyActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                ActivityCollector.finishAll();
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.order:
                intent = new Intent(getActivity(), SalesOrderActivity.class);
                startActivity(intent);

                break;
            case R.id.bill:

                break;
            default:
                break;
        }
    }

    //初始化个人信息
    private void init() {
        //http请求
//        Log.e("phone",app.getSales_phone());
        RequestBody requestBody = new FormBody.Builder().add("phone", app.getSales_phone()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSales").post(requestBody).build();
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
                if (res.equals("false")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "该账户查询失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("json", res);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SalesDetail salesDetail = new Gson().fromJson(res, SalesDetail.class);
                            ImageDownloadTask imgTask = new ImageDownloadTask();
                            if (salesDetail.getHead() != null)
                                imgTask.execute(salesDetail.getHead(), head);
                            username.setText(salesDetail.getUsername());
                            if (salesDetail.getVerify()) {
                                verify.setText("已认证");
                            }
                            rank.setText(""+salesDetail.getRank());
                            sales.setText(""+salesDetail.getSales_number());
                        }
                    });
                }
            }
        });


        initCount();
    }

    //初始化未接单数
    private void initCount(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getSales_phone()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSalesOrder").post(requestBody).build();
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
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                            Log.e("jsonArray",jsonArray.toString());
                            Gson gson = new Gson();
                            for (JsonElement sales:jsonArray) {
                                SalesOrder order = gson.fromJson(sales,SalesOrder.class);
                                if(!order.isReceive()){
                                    orderList.add(order);
                                }
                            }
                            if (orderList.size() != 0){
                                count.setText(""+orderList.size());
                            }else{
                                count.setVisibility(View.GONE);
                            }

                        }
                    });
                }
            }
        });
    }
}

package com.example.heavn.fanfan.Customer;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.CustomerGoods;
import com.example.heavn.fanfan.Bean.Goods;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.Bean.Type;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesGoodsTypeAdapter;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
import com.example.heavn.fanfan.Util.MyApp;
import com.example.heavn.fanfan.Util.ShoppingCarHistory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
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
 * 顾客端商家的商品列表碎片
 * Created by Administrator on 2018/9/18 0018.
 */

public class CustomerSalesListFragment extends Fragment implements View.OnClickListener{
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MyApp app;
    private ListView listView,typeListView;
    private List<CustomerGoods> goodsList = new ArrayList<>();
    private List<CustomerGoods> allGoodsList = new ArrayList<>();
    private List<Type> typeList = new ArrayList<>();
    private CustomerSalesGoodsAdapter adapter;
    private SalesGoodsTypeAdapter typeAdapter;
    private SalesDetail salesDetail;
    private float total = 0;
    private int allCount = 0;
    private Button pay;
    private TextView count,total_money;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_sales_list,container,false);

        app = (MyApp)getActivity().getApplication();

        salesDetail = app.getSalesDetail();

        listView = view.findViewById(R.id.foodList);
        typeListView = view.findViewById(R.id.typeList);
        count = view.findViewById(R.id.count);
        total_money = view.findViewById(R.id.total_money);
        pay = view.findViewById(R.id.pay);
        pay.setOnClickListener(this);

        initView();

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.pay:
                intent = new Intent(getActivity(),CustomerTakeOrderActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //初始化热销类型的商品信息
    private void initView(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                getTotalPrice();
                //初始化商品类型栏
                RequestBody requestBody = new FormBody.Builder().add("phone",salesDetail.getPhone()).build();
                final Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetSalesGoodsType").post(requestBody).build();
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
                                    typeList.add(new Type("热销"));
                                    typeAdapter = new SalesGoodsTypeAdapter(getActivity(),typeList);
                                    typeListView.setItemChecked(0,true);

                                    typeListView.setAdapter(typeAdapter);
                                }
                            });
                        }else{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    typeList.clear();
                                    typeList.add(new Type("热销"));
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement goodsType:jsonArray) {
                                        Type type = gson.fromJson(goodsType,Type.class);
                                        typeList.add(type);
                                    }

                                    typeAdapter = new SalesGoodsTypeAdapter(getActivity(),typeList);
                                    typeListView.setAdapter(typeAdapter);
                                    //设置默认选中的item的颜色
                                    typeAdapter.defItem = 0;
                                    typeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Type type = typeList.get(position);
                                            app.setGoods_type(type.getType());
                                            initGoods(type.getType());
                                            //点击其他item时保证默认item为无色
                                            if(position != 0){
                                                View view1 = typeListView.getChildAt(0);
                                                TextView textView = view1.findViewById(R.id.type);
                                                textView.setBackgroundColor(getResources().getColor(R.color.transparent));
                                            }

                                            getTotalPrice();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });



                //初始化每个类型的商品信息
                initGoods("热销");
            }
        }).start();
    }

    //根据商品类型获取商品信息
    private void initGoods(String type){
        RequestBody requestBody = new FormBody.Builder().add("phone",salesDetail.getPhone()).add("type",type).build();
        final Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSalesGoods").post(requestBody).build();
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
                if(!res.equals("false")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goodsList.clear();
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                            Log.e("jsonArray",jsonArray.toString());
                            Gson gson = new Gson();
                            for (JsonElement element:jsonArray) {
                                CustomerGoods goods = gson.fromJson(element,CustomerGoods.class);
                                goodsList.add(goods);
                            }
                            //先读取缓存
                            HashMap<String,Integer> hashMap = ShoppingCarHistory.getInstance().get(salesDetail.getPhone());
                            if(hashMap != null){
                                app.setHashMap(hashMap);
                            }
                            adapter = new CustomerSalesGoodsAdapter(getActivity(),goodsList,count,total_money);
                            listView.setAdapter(adapter);
                            //读取购物车缓存数据
                            if (hashMap != null) {
                                for (CustomerGoods bean : goodsList) {
                                    if (hashMap.containsKey(bean.getName())) {
                                        Integer count = hashMap.get(bean.getName());
                                        bean.setCount(count);
                                        adapter.notifyDataSetChanged();
                                        Log.e("商品",bean.getName()+count);
                                    }
                                }
                            }


                        }
                    });
                }
            }
        });
    }

    //获取加入购物车中的商品总价
    private void getTotalPrice(){
        RequestBody requestBody = new FormBody.Builder().add("phone",salesDetail.getPhone()).build();
        final Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSalesAllGoods").post(requestBody).build();
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
                if(!res.equals("false")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allGoodsList.clear();
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                            Log.e("jsonArray",jsonArray.toString());
                            Gson gson = new Gson();
                            for (JsonElement element:jsonArray) {
                                CustomerGoods goods = gson.fromJson(element,CustomerGoods.class);
                                allGoodsList.add(goods);
                            }
                            app.setAllGoodsList(allGoodsList);
                            //读取购物车缓存数据
                            HashMap<String,Integer> hashMap = ShoppingCarHistory.getInstance().get(salesDetail.getPhone());
                            if (hashMap != null) {
                                app.setHashMap(hashMap);
                                allCount = ShoppingCarHistory.getInstance().getAllGoodsCount(salesDetail.getPhone());
                                count.setText(""+allCount);
                                Log.e("count",salesDetail.getPhone());
                                total = 0;
                                for (CustomerGoods bean : allGoodsList) {
                                    if (hashMap.containsKey(bean.getName())) {
                                        Integer count = hashMap.get(bean.getName());
                                        total += (bean.getPrice()*count*bean.getDiscount())/(float)10;
                                        Log.e("商品",bean.getName()+count);
                                    }
                                }

                                total_money.setText(""+total);
                            }
                        }
                    });
                }
            }
        });
    }
}



package com.example.heavn.fanfan.Sales;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.Goods;
import com.example.heavn.fanfan.Bean.Type;
import com.example.heavn.fanfan.R;
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
 * 商家我的商店页面
 * Created by Administrator on 2018/9/11 0011.
 */

public class SalesStoreFragment extends Fragment implements View.OnClickListener{
    private ImageView add;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private MyApp app;
    private ListView listView,typeListView;
    private List<Goods> goodsList = new ArrayList<>();
    private List<Type> typeList = new ArrayList<>();
    private SalesGoodsAdapter adapter;
    private SalesGoodsTypeAdapter typeAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_store,container,false);

        add = view.findViewById(R.id.add);
        add.setOnClickListener(this);

        app = (MyApp)getActivity().getApplication();

        listView = view.findViewById(R.id.foodList);
        typeListView = view.findViewById(R.id.typeList);

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

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.add:
                intent = new Intent(getActivity(),SalesAddGoodsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化商品类型栏
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getSales_phone()).build();
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
                                    typeList.clear();
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
                                                textView.setBackgroundColor(getResources().getColor(R.color.transparant));
                                            }
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

    private void initGoods(String type){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getSales_phone()).add("type",type).build();
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
                                Goods goods = gson.fromJson(element,Goods.class);
                                goodsList.add(goods);
                            }
                            adapter = new SalesGoodsAdapter(getActivity(),goodsList);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Goods goods = goodsList.get(position);
                                    app.setGoods_name(goods.getName());
                                    Intent intent = new Intent(getActivity(),ManageGoodsActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}

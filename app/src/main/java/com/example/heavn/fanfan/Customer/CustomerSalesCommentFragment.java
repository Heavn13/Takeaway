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
import com.example.heavn.fanfan.Bean.CustomerComment;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesDetail;
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
 * 顾客端商店的顾客评论的碎片
 * Created by Administrator on 2018/9/18 0018.
 */

public class CustomerSalesCommentFragment extends Fragment {
    private ListView listView;
    private CustomerCommentAdapter adapter;
    private MyApp app;
    private List<CustomerComment> comments = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SalesDetail salesDetail;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_sales_comment,container,false);
        listView = view.findViewById(R.id.comment_list);

        app = (MyApp)getActivity().getApplication();

        salesDetail = app.getSalesDetail();

        initView();
        return view;
    }


    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder().add("phone",salesDetail.getPhone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetSalesComment").post(requestBody).build();
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
                                    Toast.makeText(getActivity(), "评论查询失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Log.e("json",res);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    comments.clear();
                                    JsonParser parser = new JsonParser();
                                    JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                                    Log.e("jsonArray",jsonArray.toString());
                                    Gson gson = new Gson();
                                    for (JsonElement element:jsonArray) {
                                        CustomerComment comment = gson.fromJson(element,CustomerComment.class);
                                        comments.add(comment);
                                    }
                                    adapter = new CustomerCommentAdapter(getActivity(), comments);
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

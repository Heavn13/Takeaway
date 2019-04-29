package com.example.heavn.fanfan.Rider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.OrderGoods;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.RiderOrder;
import com.example.heavn.fanfan.MapActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.OrderGoodsAdapter;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.DateUtil;
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
 * 骑手订单详细页面
 * Created by Administrator on 2018/9/16 0016.
 */

public class RiderOrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back;
    private MyApp app;
    private ListView listView;
    private List<OrderGoods> goodsList = new ArrayList<>();
    private OrderGoodsAdapter adapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private RiderOrder riderOrder;
    private RiderBean rider_user;
    private TextView sales_name,sales_address,address,customer,rider,id,order_time,arrival_time,total_money,customer_text, sales_text;
    private EditText rider_text;
    private ImageView sales_phone,customer_phone,sales_location,customer_location;
    private LinearLayout button,comment,user_rank,rider_rank,sales_rank;
    private Button submit,accept,reject,finish;
    private String s_sales_phone = null,s_customer_phone = null;
    private int rider_rank_number = 0;
    //初始化线程
    private Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_order_detail);

        app = (MyApp)getApplication();
        rider_user = app.getRider_user();

        sales_name = findViewById(R.id.sales_name);
        sales_address = findViewById(R.id.sales_address);
        address = findViewById(R.id.address);
        customer = findViewById(R.id.customer);
        rider = findViewById(R.id.rider);
        id = findViewById(R.id.order_id);
        order_time = findViewById(R.id.order_time);
        arrival_time = findViewById(R.id.arrival_time);
        total_money = findViewById(R.id.total_money);
        customer_text = findViewById(R.id.customer_text);
        rider_text = findViewById(R.id.rider_text);
        sales_text = findViewById(R.id.sales_text);
        listView = findViewById(R.id.foodsList);

        button = findViewById(R.id.button);
        comment = findViewById(R.id.comment);
        finish = findViewById(R.id.finish);
        finish.setOnClickListener(this);
        user_rank = findViewById(R.id.user_rank);
        rider_rank = findViewById(R.id.rider_rank);
        sales_rank = findViewById(R.id.store_rank);
        addClickListener(rider_rank);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        accept = findViewById(R.id.accept);
        accept.setOnClickListener(this);
        reject = findViewById(R.id.reject);
        reject.setOnClickListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        sales_phone = findViewById(R.id.sales_phone);
        sales_phone.setOnClickListener(this);
        customer_phone = findViewById(R.id.customer_phone);
        customer_phone.setOnClickListener(this);
        sales_location = findViewById(R.id.sales_location);
        sales_location.setOnClickListener(this);
        customer_location = findViewById(R.id.customer_location);
        customer_location.setOnClickListener(this);

        //初始化信息
        new Thread(initRunnable).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.sales_phone:
                if(s_sales_phone != null){
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:"+ s_sales_phone));
                    startActivity(intent1);
                }
                break;
            case R.id.customer_phone:
                if(s_customer_phone != null){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+s_customer_phone));
                    startActivity(intent);
                }
                break;
            case R.id.sales_location:
                app.setEndNote(sales_address.getText().toString());
                Intent intent = new Intent(RiderOrderDetailActivity.this, MapActivity.class);
                startActivity(intent);
                break;
            case R.id.customer_location:
                app.setEndNote(address.getText().toString());
                Intent intent1 = new Intent(RiderOrderDetailActivity.this, MapActivity.class);
                startActivity(intent1);
                break;
            case R.id.accept:
                if(rider_user.getVerify()){
                    receive(app.getRider_phone());
                }else{
                    Toast.makeText(this, "您的账号未通过审核，无权限进行此操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reject:
                if(rider_user.getVerify()){
                    receive("");
                }else{
                    Toast.makeText(this, "您的账号未通过审核，无权限进行此操作", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.finish:
                finishOrder(true);
                break;
            case R.id.submit:
                String text = rider_text.getText().toString();
                if(!text.equals("") && rider_rank_number != 0){
                    updateRiderRank(text, rider_rank_number);
                }else{
                    Toast.makeText(this, "请把评价内容填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void initView(){
        //资料赋值
        riderOrder = app.getRiderOrder();

        s_sales_phone = riderOrder.getSales_user();
        s_customer_phone = riderOrder.getCustomer_user();

        sales_name.setText(riderOrder.getSales_username());
        sales_address.setText(riderOrder.getSales_address());
        address.setText(riderOrder.getAddress());
        customer.setText(riderOrder.getName()+"  "+ riderOrder.getCustomer_user());
        if (!riderOrder.getRider_user().equals("")){
            rider.setText("自己");
        }
        id.setText(riderOrder.getId());
        order_time.setText(DateUtil.getTime(riderOrder.getOrder_time()));
        if(riderOrder.isFinish()){
            arrival_time.setText(DateUtil.getTime(riderOrder.getArrival_time()));
        }
        if(!riderOrder.getRider_user().equals("") && riderOrder.isFinish()){
            button.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
            finish.setVisibility(View.GONE);
        }else if(riderOrder.getRider_user().equals("")){
            button.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            finish.setVisibility(View.GONE);
        }else if(!riderOrder.getRider_user().equals("") && !riderOrder.isFinish()){
            button.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            finish.setVisibility(View.VISIBLE);
        }
        //画星星和评价
        if(riderOrder.getCustomer_rank() != 0){
            drawStar(user_rank, riderOrder.getCustomer_rank()-1);
        }
        if(!riderOrder.getCustomer_text().equals("")){
            customer_text.setText(riderOrder.getCustomer_text());
        }
        if(riderOrder.getRider_rank() != 0){
            drawStar(rider_rank, riderOrder.getRider_rank()-1);
        }
        if(!riderOrder.getRider_text().equals("")){
            rider_text.setText(riderOrder.getRider_text());
        }
        if(riderOrder.getSales_rank() != 0){
            drawStar(sales_rank, riderOrder.getSales_rank()-1);
        }
        if(!riderOrder.getSales_text().equals("")){
            sales_text.setText(riderOrder.getSales_text());
        }

        //获取商品
        RequestBody requestBody = new FormBody.Builder().add("sales_user",riderOrder.getSales_user()).add("order_id", riderOrder.getId()).build();
        final Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetOrderGoods").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RiderOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                if(!res.equals("false")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goodsList.clear();
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                            Log.e("jsonArray",jsonArray.toString());
                            Gson gson = new Gson();
                            float total = 0;
                            for (JsonElement element:jsonArray) {
                                OrderGoods goods = gson.fromJson(element,OrderGoods.class);
                                goodsList.add(goods);
                                total += goods.getPrice()*goods.getCount()*(((float)goods.getDiscount()/(float)10));
                            }

                            total_money.setText("￥"+total);
                            adapter = new OrderGoodsAdapter(RiderOrderDetailActivity.this,goodsList);
                            listView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }

    //画星星
    private void drawStar(LinearLayout linearLayout,int index){
        for(int i=0;i<=index;i++){
            ImageView imageView = (ImageView) linearLayout.getChildAt(i);
            imageView.setImageResource(R.drawable.selected);
        }

    }

    //给星星添加点击事件
    private void addClickListener(final LinearLayout linearLayout){
        for(int i=0;i<linearLayout.getChildCount();i++){
            final int index = i;
            ImageView imageView = (ImageView) linearLayout.getChildAt(i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeStar(linearLayout);
                    drawStar(linearLayout,index);
                    rider_rank_number = index+1;

                }
            });
        }
    }

    //移除所有的星星
    private void removeStar(LinearLayout linearLayout){
        for(int i=0;i<linearLayout.getChildCount();i++){
            ImageView imageView = (ImageView) linearLayout.getChildAt(i);
            imageView.setImageResource(R.drawable.unselect);
        }
    }

    //接单操作
    private void receive(final String rider_user){
        RequestBody requestBody = new FormBody.Builder().add("id", riderOrder.getId()).add("rider_user",rider_user).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateOrderRider").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RiderOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RiderOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!rider_user.equals(""))
                                Toast.makeText(RiderOrderDetailActivity.this, "已接单", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(RiderOrderDetailActivity.this, "已拒单", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    //骑手评价操作
    private void updateRiderRank(String rider_text, int rider_rank_number){
        RequestBody requestBody = new FormBody.Builder().add("id", riderOrder.getId()).add("rider_text",rider_text).add("rider_rank",""+rider_rank_number).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateRiderRank").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RiderOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RiderOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RiderOrderDetailActivity.this, "已完成对该订单的评价", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    //完成配送操作
    private void finishOrder(Boolean finish){
        long arrival_time = System.currentTimeMillis();
        RequestBody requestBody = new FormBody.Builder().add("id",riderOrder.getId()).add("finish",finish.toString()).add("arrival_time",""+arrival_time).add("rider_user",app.getRider_phone()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateOrderFinish").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RiderOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RiderOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RiderOrderDetailActivity.this, "恭喜你，完成配送", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

    }
}

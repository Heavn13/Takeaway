package com.example.heavn.fanfan.Sales;

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
import com.example.heavn.fanfan.Bean.SalesOrder;
import com.example.heavn.fanfan.OrderGoodsAdapter;
import com.example.heavn.fanfan.R;
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
 * 商家订单详情页面
 * Created by Administrator on 2018/9/15 0015.
 */

public class SalesOrderDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private MyApp app;
    private ListView listView;
    private List<OrderGoods> goodsList = new ArrayList<>();
    private OrderGoodsAdapter adapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private SalesOrder salesOrder;
    private TextView address,customer,rider,id,order_time,arrival_time,total_money,customer_text,rider_text;
    private EditText sales_text;
    private ImageView rider_phone,customer_phone;
    private LinearLayout button,comment,user_rank,rider_rank,sales_rank;
    private Button submit,accept,reject;
    private String s_rider_phone = null,s_customer_phone = null;
    private int sales_rank_number = 0;
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
        setContentView(R.layout.activity_sales_order_detail);

        app = (MyApp)getApplication();

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
        user_rank = findViewById(R.id.user_rank);
        rider_rank = findViewById(R.id.rider_rank);
        sales_rank = findViewById(R.id.store_rank);
        addClickListener(sales_rank);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        accept = findViewById(R.id.accept);
        accept.setOnClickListener(this);
        reject = findViewById(R.id.reject);
        reject.setOnClickListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        rider_phone = findViewById(R.id.rider_phone);
        rider_phone.setOnClickListener(this);
        customer_phone = findViewById(R.id.customer_phone);
        customer_phone.setOnClickListener(this);

        //初始化信息
        new Thread(initRunnable).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.rider_phone:
                if(s_rider_phone != null){
                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                    intent1.setData(Uri.parse("tel:"+s_rider_phone));
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
            case R.id.accept:
                receive(true);
                break;
            case R.id.reject:
                receive(false);
                break;
            case R.id.submit:
                String text = sales_text.getText().toString();
                if(!text.equals("") && sales_rank_number != 0){
                    updateSalesRank(text,sales_rank_number);
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
        salesOrder = app.getSalesOrder();
        s_rider_phone = salesOrder.getRider_user();
        s_customer_phone = salesOrder.getCustomer_user();

        address.setText(salesOrder.getAddress());
        customer.setText(salesOrder.getName()+"  "+salesOrder.getReceive_phone());
        if (!salesOrder.getRider_user().equals("")){
            rider.setText(salesOrder.getRider_username()+"  "+salesOrder.getRider_user());
        }
        id.setText(salesOrder.getId());
        order_time.setText(DateUtil.getTime(salesOrder.getOrder_time()));
        if(salesOrder.isFinish()){
            arrival_time.setText(DateUtil.getTime(salesOrder.getArrival_time()));
        }
        if(salesOrder.isReceive()){
            button.setVisibility(View.GONE);
            submit.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
        }else{
            button.setVisibility(View.VISIBLE);
            submit.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
        }
        //画星星和评价
        if(salesOrder.getCustomer_rank() != 0){
            drawStar(user_rank,salesOrder.getCustomer_rank()-1);
        }
        if(!salesOrder.getCustomer_text().equals("")){
            customer_text.setText(salesOrder.getCustomer_text());
        }
        if(salesOrder.getRider_rank() != 0){
            drawStar(rider_rank,salesOrder.getRider_rank()-1);
        }
        if(!salesOrder.getRider_text().equals("")){
            rider_text.setText(salesOrder.getRider_text());
        }
        if(salesOrder.getSales_rank() != 0){
            drawStar(sales_rank,salesOrder.getSales_rank()-1);
        }
        if(!salesOrder.getSales_text().equals("")){
            sales_text.setText(salesOrder.getSales_text());
        }

        //获取商品
        RequestBody requestBody = new FormBody.Builder().add("sales_user",app.getSales_phone()).add("order_id",salesOrder.getId()).build();
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
                        Toast.makeText(SalesOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            adapter = new OrderGoodsAdapter(SalesOrderDetailActivity.this,goodsList);
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
                    sales_rank_number = index + 1;
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
    private void receive(final Boolean receive){
        RequestBody requestBody = new FormBody.Builder().add("id",salesOrder.getId()).add("receive",receive.toString()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateOrderReceive").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SalesOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SalesOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(receive)
                                Toast.makeText(SalesOrderDetailActivity.this, "已接单", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(SalesOrderDetailActivity.this, "已拒单", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    //商家评价操作
    private void updateSalesRank(String sales_text,int sales_rank_numebr){
        RequestBody requestBody = new FormBody.Builder().add("id",salesOrder.getId()).add("sales_text",sales_text).add("sales_rank",""+sales_rank_numebr).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateSalesRank").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SalesOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SalesOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                Toast.makeText(SalesOrderDetailActivity.this, "已完成对该订单的评价", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

}

package com.example.heavn.fanfan.Customer;

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

import com.example.heavn.fanfan.Bean.CustomerOrder;
import com.example.heavn.fanfan.Bean.OrderGoods;
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
 * 顾客订单的详细信息页面
 * Created by Administrator on 2018/9/19 0019.
 */

public class CustomerOrderDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private MyApp app;
    private ListView listView;
    private List<OrderGoods> goodsList = new ArrayList<>();
    private OrderGoodsAdapter adapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private CustomerOrder customerOrder;
    private TextView sales_name,sales_address,address,customer,rider,id,order_time,arrival_time,total_money,sales_text,rider_text;
    private EditText customer_text;
    private ImageView rider_phone, sales_phone;
    private LinearLayout comment,user_rank,rider_rank,sales_rank;
    private Button submit;
    private String s_rider_phone = null, s_sales_phone = null;
    private int customer_rank_number = 0;
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
        setContentView(R.layout.activity_customer_order_detail);

        app = (MyApp)getApplication();

        address = findViewById(R.id.address);
        sales_name = findViewById(R.id.sales_name);
        sales_address = findViewById(R.id.sales_address);
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
        comment = findViewById(R.id.comment);
        user_rank = findViewById(R.id.user_rank);
        rider_rank = findViewById(R.id.rider_rank);
        sales_rank = findViewById(R.id.store_rank);
        addClickListener(user_rank);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        rider_phone = findViewById(R.id.rider_phone);
        rider_phone.setOnClickListener(this);
        sales_phone = findViewById(R.id.sales_phone);
        sales_phone.setOnClickListener(this);

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
            case R.id.sales_phone:
                if(s_sales_phone != null){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+ s_sales_phone));
                    startActivity(intent);
                }
                break;
            case R.id.submit:
                String text = customer_text.getText().toString();
                if(!text.equals("") && customer_rank_number != 0){
                    updateCustomerRank(text, customer_rank_number);
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
        customerOrder = app.getCustomerOrder();
        s_rider_phone = customerOrder.getRider_user();
        s_sales_phone = customerOrder.getSales_user();
        sales_name.setText(customerOrder.getSales_username());
        sales_address.setText(customerOrder.getSales_address());
        address.setText(customerOrder.getAddress());
        customer.setText(customerOrder.getName()+"  "+ customerOrder.getReceive_phone());
        if (!customerOrder.getRider_user().equals("")){
            rider.setText(customerOrder.getRider_username()+"  "+ customerOrder.getRider_user());
        }
        id.setText(customerOrder.getId());
        order_time.setText(DateUtil.getTime(customerOrder.getOrder_time()));
        if(customerOrder.isFinish()){
            arrival_time.setText(DateUtil.getTime(customerOrder.getArrival_time()));
        }
        if(customerOrder.isReceive() && customerOrder.isFinish()){
            submit.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
        }else{
            submit.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
        }
        //画星星和评价
        if(customerOrder.getCustomer_rank() != 0){
            drawStar(user_rank, customerOrder.getCustomer_rank()-1);
        }
        if(!customerOrder.getCustomer_text().equals("")){
            customer_text.setText(customerOrder.getCustomer_text());
        }
        if(customerOrder.getRider_rank() != 0){
            drawStar(rider_rank, customerOrder.getRider_rank()-1);
        }
        if(!customerOrder.getRider_text().equals("")){
            rider_text.setText(customerOrder.getRider_text());
        }
        if(customerOrder.getSales_rank() != 0){
            drawStar(sales_rank, customerOrder.getSales_rank()-1);
        }
        if(!customerOrder.getSales_text().equals("")){
            sales_text.setText(customerOrder.getSales_text());
        }

        //获取商品
        RequestBody requestBody = new FormBody.Builder().add("sales_user",customerOrder.getSales_user()).add("order_id", customerOrder.getId()).build();
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
                        Toast.makeText(CustomerOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            adapter = new OrderGoodsAdapter(CustomerOrderDetailActivity.this,goodsList);
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
                    customer_rank_number = index+1;
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

    //用户评价操作
    private void updateCustomerRank(String customer_text,int customer_rank_number){
        RequestBody requestBody = new FormBody.Builder().add("id", customerOrder.getId()).add("customer_text",customer_text).add("customer_rank",""+customer_rank_number).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateCustomerRank").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerOrderDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CustomerOrderDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerOrderDetailActivity.this, "已完成对该订单的评价", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}

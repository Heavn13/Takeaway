package com.example.heavn.fanfan.Customer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.Address;
import com.example.heavn.fanfan.Bean.CustomerOrder;
import com.example.heavn.fanfan.Bean.OrderGoods;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.OrderGoodsAdapter;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;
import com.example.heavn.fanfan.Util.MyListView;
import com.example.heavn.fanfan.Util.ShoppingCarHistory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkb.vcedittext.VerificationCodeEditText;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
 * 顾客下订单页面
 * Created by Administrator on 2018/9/19 0019.
 */

public class CustomerTakeOrderActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private MyApp app;
    private MyListView listView;
    private List<OrderGoods> goodsList = new ArrayList<>();
    private List<OrderGoods> allGoodsList = new ArrayList<>();
    private OrderGoodsAdapter adapter;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private TextView sales_name,address,name,phone,total_money,total;
    private Button pay;
    private SalesDetail salesDetail;
    private String order_id,order_time;
    private Address a;
    private float money;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_take_order);

        app = (MyApp)getApplication();

        salesDetail = app.getSalesDetail();

        sales_name = findViewById(R.id.username);
        address = findViewById(R.id.address);
        address.setOnClickListener(this);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        total_money = findViewById(R.id.total_money);
        total = findViewById(R.id.total);
        listView = findViewById(R.id.foodsList);
        pay = findViewById(R.id.pay);
        pay.setOnClickListener(this);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        initView();

    }

    //修改地址后更新订单的地址信息
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置商店名称
                sales_name.setText(salesDetail.getUsername());
                //获取该商家的所有商品信息
                getAllGoods();
                //选取收货地址后更新收货地址
                final Address add = app.getAddress();
                if(add != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            address.setText(add.getAddress());
                            name.setText(add.getName());
                            phone.setText(add.getReceive_phone());
                        }
                    });
                }else{
                    //获取默认地址
                    RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url(app.getUrl()+"/GetDefaultAddress").post(requestBody).build();
                    Call call = okHttpClient.newCall(request);
                    //执行Call
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CustomerTakeOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                        address.setText("暂未设置地址，请点击设置");
                                        name.setText("");
                                        phone.setText("");
                                    }
                                });
                            }else{
//                    Log.e("json",res);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        a = new Gson().fromJson(res,Address.class);
                                        address.setText(a.getAddress());
                                        name.setText(a.getName());
                                        phone.setText(a.getPhone());
                                    }
                                });
                            }
                        }
                    });
                }

            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.address:
                Intent intent = new Intent(CustomerTakeOrderActivity.this,CustomerAddressChooseActivity.class);
                startActivity(intent);
                break;
            case R.id.pay:
                if(address.getText().equals("暂未设置地址，请点击设置")){
                    Toast.makeText(this, "请先设置收货地址", Toast.LENGTH_SHORT).show();
                }else{
                    View view = getLayoutInflater().inflate(R.layout.dialog_code, null);
                    final VerificationCodeEditText editText = view.findViewById(R.id.code);
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("请输入您的支付密码")//设置对话框的标题
                            .setView(view)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hideSoftKeyboard(CustomerTakeOrderActivity.this);
                                    String content = editText.getText().toString();
                                    verifyPayPassword(content);
                                    dialog.dismiss();
                                    hideSoftKeyboard(CustomerTakeOrderActivity.this);
                                }
                            }).create();
                    dialog.show();
                }

                break;
            default:
                break;
        }
    }

    //初始化信息
    private void initView(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置商店名称
                sales_name.setText(salesDetail.getUsername());
                //获取该商家的所有商品信息
                getAllGoods();

                //获取默认地址
                RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(app.getUrl()+"/GetDefaultAddress").post(requestBody).build();
                Call call = okHttpClient.newCall(request);
                //执行Call
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CustomerTakeOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                                    address.setText("暂未设置地址，请点击设置");
                                    name.setText("");
                                    phone.setText("");
                                }
                            });
                        }else{
//                    Log.e("json",res);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    a = new Gson().fromJson(res,Address.class);
                                    address.setText(a.getAddress());
                                    name.setText(a.getName());
                                    phone.setText(a.getPhone());
                                }
                            });
                        }
                    }
                });
            }
        }).start();

    }

    //获取该商家的所有商品信息，加入购物车的商品信息
    private void getAllGoods(){
        RequestBody requestBody = new FormBody.Builder().add("phone",salesDetail.getPhone()).build();
        final Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSalesAllGoods").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerTakeOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            allGoodsList.clear();
                            goodsList.clear();
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                            Log.e("jsonArray",jsonArray.toString());
                            Gson gson = new Gson();
                            for (JsonElement element:jsonArray) {
                                OrderGoods goods = gson.fromJson(element,OrderGoods.class);
                                allGoodsList.add(goods);
                            }
                            //读取购物车缓存数据
                            HashMap<String,Integer> hashMap = ShoppingCarHistory.getInstance().get(salesDetail.getPhone());
                            if (hashMap != null) {
                                app.setHashMap(hashMap);
                                money = 0;
                                for (OrderGoods bean : allGoodsList) {
                                    if (hashMap.containsKey(bean.getName())) {
                                        Integer count = hashMap.get(bean.getName());
                                        money += (bean.getPrice()*count*bean.getDiscount())/(float)10;
                                        Log.e("商品",bean.getName()+count+bean.getPrice());
                                        bean.setCount(count);
                                        //如果数量不等于0上一个删除的也会显示
                                        if(count != 0){
                                            goodsList.add(bean);
                                        }
                                    }
                                }

                                total_money.setText("￥"+money);
                                total.setText("￥"+(money));
                                adapter = new OrderGoodsAdapter(CustomerTakeOrderActivity.this,goodsList);
                                listView.setAdapter(adapter);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 隐藏Dialog中的软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
    }

    private void verifyPayPassword(String content){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone()).add("pay_password",content).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/VerifyPayPassword").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerTakeOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if(res.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            takeOrder();
                        }
                    });
                }else{
//                    Log.e("json",res);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerTakeOrderActivity.this, "支付密码错误或未设置支付密码", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //下订单
    private void takeOrder(){
        order_id = app.getCustomer_phone().substring(7);
        order_time = ""+System.currentTimeMillis();
        order_id += order_time;
        //将购物车中的商品信息以数组的形式传到后台
        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<goodsList.size();i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",goodsList.get(i).getName());
                jsonObject.put("picture",goodsList.get(i).getPicture());
                jsonObject.put("count",goodsList.get(i).getCount());
                jsonObject.put("discount",goodsList.get(i).getDiscount());
                jsonObject.put("price",goodsList.get(i).getPrice());
            }catch(JSONException e){
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
//        try{
//            s_name = URLEncoder.encode(s_name,"utf-8");
//        }catch (UnsupportedEncodingException e){
//            e.printStackTrace();
//        }
        Log.e("json",jsonArray.toString());
        RequestBody requestBody = new FormBody.Builder().add("order_id",order_id)
                .add("order_time",order_time).add("customer_user",app.getCustomer_phone())
                .add("name",name.getText().toString()).add("receive_phone",phone.getText().toString())
                .add("address",address.getText().toString()).add("total_price",""+money)
                .add("sales_user",salesDetail.getPhone()).add("jsonArray",jsonArray.toString()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/AddOrder").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerTakeOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                if(res.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerTakeOrderActivity.this, "订单支付成功，等待商家接单", Toast.LENGTH_SHORT).show();
                            //下订单成功，删除购物车缓存
                            ShoppingCarHistory.getInstance().delete(salesDetail.getPhone());
                            //获取当前顾客下的订单
                            CustomerOrder customerOrder = new CustomerOrder(order_id,app.getCustomer_phone(),"",salesDetail.getPhone(),"",
                                    "","","","",salesDetail.getUsername(),salesDetail.getAddress(),name.getText().toString(),phone.getText().toString(),
                                    address.getText().toString(),salesDetail.getHead(),0,0,0,false,false,Long.parseLong(order_time),0,money);
                            app.setCustomerOrder(customerOrder);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(CustomerTakeOrderActivity.this,CustomerOrderDetailActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);//1.5秒后执行Runnable中的run方法


                        }
                    });
                }else{
//                    Log.e("json",res);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerTakeOrderActivity.this, "订单失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }
}

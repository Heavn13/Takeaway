package com.example.heavn.fanfan.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.heavn.fanfan.ChangePasswordActivity;
import com.example.heavn.fanfan.MainActivity;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.ManageGoodsActivity;
import com.example.heavn.fanfan.Util.ActivityCollector;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 顾客添加地址的界面
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerAddressAddActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    private ImageView back;
    private Button add;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String s_name,s_phone,s_address;
    private EditText name,phone,address;
    private boolean selected = false;//存默认地址的状态
    private RadioGroup radioGroup;//是否设为默认地址
    private RadioButton no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);

        app = (MyApp)getApplication();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        add = findViewById(R.id.add_address);
        add.setOnClickListener(this);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.receive_phone);
        address = findViewById(R.id.address);
        radioGroup = findViewById(R.id.radioGroup);
        no = findViewById(R.id.no);
        no.setChecked(true);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.add_address:
                s_name = name.getText().toString();
                s_phone = phone.getText().toString();
                s_address = address.getText().toString();
                //需要对传到后台的中文字符进行编码转化，否则传到后台会变成乱码
                try{
                    s_name = URLEncoder.encode(s_name,"utf-8");
                    s_address = URLEncoder.encode(s_address,"utf-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                if(!s_name.equals("") && !s_phone.equals("") && !s_address.equals("")){
                    add();
                }else{
                    Toast.makeText(this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.yes:
                selected = true;
                break;
            case R.id.no:
                selected = false;
                break;
            default:
                break;
        }
    }

    //给当前用户添加地址
    private void add(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone())
                .add("name",s_name).add("receive_phone",s_phone).add("address",s_address).add("selected",""+selected).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/AddAddress").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerAddressAddActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                if(res.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerAddressAddActivity.this, "地址添加成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerAddressAddActivity.this, "地址添加失败", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }
}

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

import com.example.heavn.fanfan.Bean.Address;
import com.example.heavn.fanfan.R;
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
 * 顾客管理地址的页面，修改地址，删除地址
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerAddressManageActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener {
    private ImageView back;
    private Button change,delete;
    private EditText name,phone,address;
    private boolean selected = false;
    private RadioGroup radioGroup;
    private RadioButton no,yes;
    private MyApp app;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String s_name,s_phone,s_address;
    private Address location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_modify);

        app = (MyApp)getApplication();
        location = app.getAddress();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        change = findViewById(R.id.change_address);
        change.setOnClickListener(this);
        delete = findViewById(R.id.delete_address);
        delete.setOnClickListener(this);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.receive_phone);
        address = findViewById(R.id.address);
        radioGroup = findViewById(R.id.radioGroup);
        no = findViewById(R.id.no);
        yes = findViewById(R.id.yes);

        initView();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.change_address:
                s_name = name.getText().toString();
                s_phone = phone.getText().toString();
                s_address = address.getText().toString();
                if(yes.isChecked()){
                    selected = true;
                }else{
                    selected = false;
                }
                try{
                    s_name = URLEncoder.encode(s_name,"utf-8");
                    s_address = URLEncoder.encode(s_address,"utf-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                if(!s_name.equals("") && !s_phone.equals("") && !s_address.equals("")){
                    change();
                }else{
                    Toast.makeText(this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.delete_address:
                s_name = name.getText().toString();
                s_phone = phone.getText().toString();
                s_address = address.getText().toString();
                try{
                    s_name = URLEncoder.encode(s_name,"utf-8");
                    s_address = URLEncoder.encode(s_address,"utf-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                delete();
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

    //修改顾客地址
    private void change(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone())
                .add("old_name",location.getName()).add("old_receive_phone",location.getReceive_phone()).add("old_address",location.getAddress())
                .add("name",s_name).add("receive_phone",s_phone).add("address",s_address).add("selected",""+selected).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/UpdateAddress").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerAddressManageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CustomerAddressManageActivity.this, "地址修改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerAddressManageActivity.this, "地址修改失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //删除地址
    private void delete(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getCustomer_phone())
                .add("name",location.getName()).add("receive_phone",location.getReceive_phone()).add("address",location.getAddress()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/DeleteAddress").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomerAddressManageActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CustomerAddressManageActivity.this, "地址删除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CustomerAddressManageActivity.this, "地址删除失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //初始化界面
    private void initView(){
        name.setText(location.getName());
        phone.setText(location.getReceive_phone());
        address.setText(location.getAddress());
        if(location.isSelected()){
            yes.setChecked(true);
        }else{
            no.setChecked(true);
        }
    }
}

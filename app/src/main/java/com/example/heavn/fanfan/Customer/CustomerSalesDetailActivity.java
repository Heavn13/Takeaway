package com.example.heavn.fanfan.Customer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Rider.AcceptedOrderFragment;
import com.example.heavn.fanfan.Rider.FinishOrderFragment;
import com.example.heavn.fanfan.Rider.NewOrderFragment;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;

/**
 * 点击商店列表进入的商店信息页面，管理商品列表、商家评论，商家信息三个碎片
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerSalesDetailActivity extends BaseActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private ImageView back,head;
    private MyApp app;
    private SalesDetail salesDetail;
    private TextView name,sales_number,rank;
    private RadioGroup radioGroup;
    private RadioButton order,comment,detail;
    private CustomerSalesListFragment listFragment;
    private CustomerSalesCommentFragment commentFragment;
    private CustomerSalesDetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sales_main);

        head = findViewById(R.id.head);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        name = findViewById(R.id.name);
        sales_number = findViewById(R.id.sales);
        rank = findViewById(R.id.rank);
        app = (MyApp)getApplication();
        salesDetail = app.getSalesDetail();

        listFragment  = new CustomerSalesListFragment();
        commentFragment = new CustomerSalesCommentFragment();
        detailFragment = new CustomerSalesDetailFragment();

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        order = findViewById(R.id.order);
        order.setChecked(true);
        comment = findViewById(R.id.comment);
        comment.setChecked(false);
        detail = findViewById(R.id.detail);
        detail.setChecked(false);

        initView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId){
            case R.id.order:
                if (listFragment == null) {
                    listFragment  = new CustomerSalesListFragment();
                }
                transaction.replace(R.id.frameLayout,listFragment);
                break;
            case R.id.comment:
                if (commentFragment == null) {
                    commentFragment = new CustomerSalesCommentFragment();
                }
                transaction.replace(R.id.frameLayout, commentFragment);
                break;
            case R.id.detail:
                if (detailFragment == null) {
                    detailFragment = new CustomerSalesDetailFragment();
                }
                transaction.replace(R.id.frameLayout, detailFragment);
                break;
            default:
                break;
        }
        transaction.commit();// 事务提交
    }


    //初始化信息
    private void initView(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                name.setText(salesDetail.getUsername());
                sales_number.setText(""+salesDetail.getSales_number());
                rank.setText(""+salesDetail.getRank());

            }
        }).start();
    }
}

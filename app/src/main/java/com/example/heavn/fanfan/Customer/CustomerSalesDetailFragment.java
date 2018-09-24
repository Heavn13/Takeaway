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
import android.widget.ImageView;
import android.widget.TextView;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
import com.example.heavn.fanfan.Util.MyApp;


/**
 * 顾客端商家详细信息碎片
 * Created by Administrator on 2018/9/18 0018.
 */

public class CustomerSalesDetailFragment extends Fragment implements View.OnClickListener {
    private ImageView head;
    private MyApp app;
    private SalesDetail salesDetail;
    private TextView name,sales_number,rank,introduction,address,phone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_sales_detail,container,false);

        app = (MyApp)getActivity().getApplication();

        salesDetail = app.getSalesDetail();

        head = view.findViewById(R.id.head);
        name = view.findViewById(R.id.name);
        sales_number = view.findViewById(R.id.sales);
        rank = view.findViewById(R.id.rank);
        introduction = view.findViewById(R.id.introduction);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        phone.setOnClickListener(this);

        initView();

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.phone:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+salesDetail.getPhone()));
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //初始化商家信息
    private void initView(){
        if(!salesDetail.getHead().equals("")){
            ImageDownloadTask imgTask = new ImageDownloadTask();
            imgTask.execute(salesDetail.getHead(),head);
        }

        name.setText(salesDetail.getUsername());
        sales_number.setText(""+salesDetail.getSales_number());
        rank.setText(""+salesDetail.getRank());
        introduction.setText(salesDetail.getSummary());
        address.setText(salesDetail.getAddress());
        phone.setText(salesDetail.getPhone());
    }
}

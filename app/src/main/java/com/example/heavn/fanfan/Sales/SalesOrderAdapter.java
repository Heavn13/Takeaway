package com.example.heavn.fanfan.Sales;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Bean.Goods;
import com.example.heavn.fanfan.Bean.Order;
import com.example.heavn.fanfan.Bean.SalesOrder;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.DateUtil;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 商家订单的adapter
 * Created by Administrator on 2018/9/15 0015.
 */

public class SalesOrderAdapter extends BaseAdapter {
    private Context context;
    private List<SalesOrder> list;
    private MyApp app;

    public SalesOrderAdapter(Context context, List<SalesOrder> list) {
        this.context = context;
        this.list = list;
        app = (MyApp)context.getApplicationContext();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_sales_order, null);
            holder.id = view.findViewById(R.id.order_id);
            holder.order_time = view.findViewById(R.id.order_time);
            holder.customer_user = view.findViewById(R.id.customer_user);
            holder.receive = view.findViewById(R.id.receive);
            holder.rider_user = view.findViewById(R.id.rider_user);
            holder.address = view.findViewById(R.id.address);
            holder.arrival_time = view.findViewById(R.id.arrival_time);
            holder.rider_detail  =view.findViewById(R.id.rider_detail);
            holder.arrival = view.findViewById(R.id.arrival);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        Log.e("id",list.get(i).getId());
        holder.id.setText(list.get(i).getId());

        holder.order_time.setText(DateUtil.getTime(list.get(i).getOrder_time()));
        holder.customer_user.setText(list.get(i).getName());
        holder.address.setText(list.get(i).getAddress());
        if(list.get(i).isReceive()){
            holder.receive.setText("已接单");
        }else{
            holder.receive.setText("未接单");
        }
        //设置骑手信息
        if(!list.get(i).getRider_user().equals("")){
            holder.rider_detail.setVisibility(View.VISIBLE);
            holder.rider_user.setText(list.get(i).getRider_username());
        }else{
            holder.rider_detail.setVisibility(View.GONE);
        }
        //设置订单完成时间
        if(list.get(i).getArrival_time() != 0){
            holder.arrival.setVisibility(View.VISIBLE);
            holder.arrival_time.setText(DateUtil.getTime(list.get(i).getArrival_time()));
        }else{
            holder.arrival.setVisibility(View.GONE);
        }
        return view;

    }
    class ViewHolder{
        TextView id,order_time,customer_user,rider_user,address,arrival_time,receive;
        LinearLayout rider_detail,arrival;
    }


}

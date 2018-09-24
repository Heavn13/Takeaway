package com.example.heavn.fanfan.Rider;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.heavn.fanfan.Bean.RiderOrder;
import com.example.heavn.fanfan.Bean.SalesOrder;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesOrderAdapter;
import com.example.heavn.fanfan.Util.DateUtil;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 骑手订单的adapter
 * Created by Administrator on 2018/9/16 0016.
 */

public class RiderOrderAdapter extends BaseAdapter {
    private Context context;
    private List<RiderOrder> list;
    private MyApp app;

    public RiderOrderAdapter(Context context, List<RiderOrder> list) {
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
            view = View.inflate(context, R.layout.item_rider_order, null);
            holder.id = view.findViewById(R.id.order_id);
            holder.order_time = view.findViewById(R.id.order_time);
            holder.customer_user = view.findViewById(R.id.customer_user);
            holder.receive = view.findViewById(R.id.receive);
            holder.sales_user = view.findViewById(R.id.sales_user);
            holder.customer_address = view.findViewById(R.id.customer_address);
            holder.arrival_time = view.findViewById(R.id.arrival_time);
            holder.sales_address  = view.findViewById(R.id.sales_address);
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
        holder.customer_address.setText(list.get(i).getCustomer_address());
        holder.sales_user.setText(list.get(i).getSales_username());
        holder.sales_address.setText(list.get(i).getSales_address());
        if(!list.get(i).getRider_user().equals("")){
            holder.receive.setText("已接单");
        }else{
            holder.receive.setText("未接单");
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
        TextView id,order_time,customer_user,customer_address,sales_user,sales_address,arrival_time,receive;
        LinearLayout arrival;
    }
}

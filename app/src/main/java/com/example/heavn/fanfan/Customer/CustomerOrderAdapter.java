package com.example.heavn.fanfan.Customer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Bean.CustomerOrder;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.DateUtil;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 顾客的订单的adapter
 * Created by Administrator on 2018/9/19 0019.
 */

public class CustomerOrderAdapter extends BaseAdapter {
    private Context context;
    private List<CustomerOrder> list;
    private MyApp app;

    public CustomerOrderAdapter(Context context, List<CustomerOrder> list) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //引入布局
            view = View.inflate(context, R.layout.item_customer_order, null);
            //实例化对象
            holder.head = view.findViewById(R.id.head);
            holder.sales_name = view.findViewById(R.id.sales_name);
            holder.order_time = view.findViewById(R.id.order_time);
            holder.state = view.findViewById(R.id.state);
            holder.total_money = view.findViewById(R.id.total_money);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        //设置默认背景
        holder.head.setDefaultImageResId(R.drawable.nopicture);
        holder.head.setImageUrl(list.get(i).getSales_head(), imageLoader);
        holder.sales_name.setText(list.get(i).getSales_username());
        holder.total_money.setText("￥"+list.get(i).getTotal_price());
        holder.order_time.setText(DateUtil.getTime(list.get(i).getOrder_time()));
        if( list.get(i).isReceive() && !list.get(i).isFinish()){
            holder.state.setText("正在配送中");
        }else if(list.get(i).isReceive() && list.get(i).isFinish()){
            holder.state.setText("该订单已完成");
        }

        return view;
    }
    class ViewHolder{
        NetworkImageView head;
        TextView sales_name,order_time,total_money,state;
    }
}

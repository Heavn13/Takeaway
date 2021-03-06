package com.example.heavn.fanfan.Customer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Backstage.RiderAdapter;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesDetail;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 顾客看到的商店列表的adapter
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerSalesAdapter extends BaseAdapter {
    private Context context;
    private List<SalesDetail> list;
    private MyApp app;

    public CustomerSalesAdapter(Context context, List<SalesDetail> list) {
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
            view = View.inflate(context, R.layout.item_sales, null);
            //实例化对象
            holder.head = view.findViewById(R.id.head);
            holder.username = view.findViewById(R.id.name);
            holder.rank = view.findViewById(R.id.rank);
            holder.sales_number = view.findViewById(R.id.sales);
            holder.address = view.findViewById(R.id.address);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        //设置默认背景
        holder.head.setDefaultImageResId(R.drawable.nopicture);
        holder.head.setImageUrl(list.get(i).getHead(), imageLoader);
        holder.username.setText(list.get(i).getUsername());
        holder.rank.setText(""+list.get(i).getRank());
        holder.sales_number.setText(""+list.get(i).getSales_number());
        holder.address.setText(list.get(i).getAddress());

        return view;
    }
    class ViewHolder{
        NetworkImageView head;
        TextView username,rank,sales_number,address;
    }
}

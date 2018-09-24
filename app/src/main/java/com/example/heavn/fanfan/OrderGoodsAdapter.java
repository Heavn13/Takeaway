package com.example.heavn.fanfan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Bean.Goods;
import com.example.heavn.fanfan.Bean.OrderGoods;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 订单中商品的数量等信息的adapter
 * Created by Administrator on 2018/9/15 0015.
 */

public class OrderGoodsAdapter extends BaseAdapter {
    private Context context;
    private List<OrderGoods> list;
    private MyApp app;

    public OrderGoodsAdapter(Context context, List<OrderGoods> list) {
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
            view = View.inflate(context, R.layout.item_order_foods_list, null);
            //实例化对象
            holder.picture = view.findViewById(R.id.picture);
            holder.name = view.findViewById(R.id.name);
            holder.count = view.findViewById(R.id.count);
            holder.price = view.findViewById(R.id.price);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        //使用Volley框架下载图片
        ImageLoader imageLoader = new ImageLoader(MyApp.getHttpQueue(), BitmapCache.instance());
        //设置默认背景
        holder.picture.setDefaultImageResId(R.drawable.nopicture);
        holder.picture.setImageUrl(list.get(i).getPicture(), imageLoader);
        holder.name.setText(list.get(i).getName());
        holder.count.setText(""+list.get(i).getCount());
        holder.price.setText(""+list.get(i).getPrice()*list.get(i).getCount()*(((float)list.get(i).getDiscount()/(float)10)));

        return view;
    }
    class ViewHolder{
        NetworkImageView picture;
        TextView name,count,price;
    }
}

package com.example.heavn.fanfan.Sales;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Backstage.SalesAdapter;
import com.example.heavn.fanfan.Bean.Goods;
import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 商家商品的adapter
 * Created by Administrator on 2018/9/13 0013.
 */

public class SalesGoodsAdapter extends BaseAdapter {
    private Context context;
    private List<Goods> list;
    private MyApp app;

    public SalesGoodsAdapter(Context context, List<Goods> list) {
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
            view = View.inflate(context, R.layout.item_sales_goods, null);
            //实例化对象
            holder.picture = view.findViewById(R.id.picture);
            holder.name = view.findViewById(R.id.name);
            holder.price = view.findViewById(R.id.price);
            holder.discount = view.findViewById(R.id.discount);
            holder.sales = view.findViewById(R.id.sales);
            holder.introduction = view.findViewById(R.id.introduction);
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
        holder.introduction.setText(list.get(i).getIntroduction());
        holder.sales.setText(""+list.get(i).getSales());
        if(list.get(i).getDiscount() != 10){
            holder.discount.setText(""+list.get(i).getDiscount()+"折");
        }
        holder.price.setText(""+list.get(i).getPrice());

        return view;
    }
    class ViewHolder{
        NetworkImageView picture;
        TextView name,price,discount,introduction,sales;
    }
}

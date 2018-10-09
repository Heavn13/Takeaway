package com.example.heavn.fanfan.Customer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Bean.CustomerComment;
import com.example.heavn.fanfan.Bean.CustomerGoods;
import com.example.heavn.fanfan.Bean.Goods;
import com.example.heavn.fanfan.Bean.StoreGoodsBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Sales.SalesGoodsAdapter;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.MyApp;
import com.example.heavn.fanfan.Util.ShoppingCarHistory;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 顾客端商家商品列表的adapter
 * Created by Administrator on 2018/9/18 0018.
 */

public class CustomerSalesGoodsAdapter extends BaseAdapter {
    private Context context;
    private List<CustomerGoods> list;
    private MyApp app;
    private HashMap<String, Integer> hashMap;
    private  TextView t_count,t_price;
    private List<CustomerGoods> allGoodsList;
//    private MyListener myListener;


    public CustomerSalesGoodsAdapter(Context context, List<CustomerGoods> list,TextView t_count, TextView t_price) {
        this.context = context;
        this.list = list;
        this.t_count = t_count;
        this.t_price = t_price;
        app = (MyApp)context.getApplicationContext();
        allGoodsList = app.getAllGoodsList();
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
//            myListener = new MyListener(i);
            //引入布局
            view = View.inflate(context, R.layout.item_customer_sales_goods, null);
            //实例化对象
            holder.picture = view.findViewById(R.id.picture);
            holder.name = view.findViewById(R.id.name);
            holder.price = view.findViewById(R.id.price);
            holder.discount = view.findViewById(R.id.discount);
            holder.sales = view.findViewById(R.id.sales);
            holder.introduction = view.findViewById(R.id.introduction);
            holder.count = view.findViewById(R.id.count);
            holder.add = view.findViewById(R.id.add);
            holder.delete = view.findViewById(R.id.delete);
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
        //item中的增加购物车中商品数量的方法
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerGoods goods = (CustomerGoods) getItem(i);
                goods.setCount(goods.getCount()+1);
                //如果全局类中的HashMap为空，给它new一个出来，否则会报错
                hashMap = app.getHashMap();
                if(hashMap == null){
                    hashMap = new HashMap<>();
                }
                hashMap.put(goods.getName(),goods.getCount());
                app.setHashMap(hashMap);
                StoreGoodsBean storeGoodsBean = new StoreGoodsBean(hashMap);
                ShoppingCarHistory.getInstance().add(goods.getPhone(),storeGoodsBean);
//                Log.e("phone",goods.getPhone());
//                Log.e("count",""+ShoppingCarHistory.getInstance().getAllGoodsCount(goods.getPhone()));
                int allCount = ShoppingCarHistory.getInstance().getAllGoodsCount(goods.getPhone());
                //修改购物车数量
                t_count.setText(""+allCount);
                //修改购物车总价
                float price = 0;
                for (CustomerGoods bean : allGoodsList) {
                    if (hashMap.containsKey(bean.getName())) {
                        Integer count = hashMap.get(bean.getName());
                        price += (bean.getPrice()*count*bean.getDiscount())/(float)10;
                        Log.e("商品",bean.getName()+count);
                    }
                }
                t_price.setText(""+price);
                //刷新adapter
                notifyDataSetChanged();
            }
        });
        //item中的减少购物车中商品数量的方法
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //使用HashMap缓存购物车信息
                CustomerGoods goods = (CustomerGoods) getItem(i);
                goods.setCount(goods.getCount()-1);
                hashMap = app.getHashMap();
                //如果全局类中的HashMap为空，给它new一个出来，否则会报错
                if(hashMap == null){
                    hashMap = new HashMap<>();
                }
                hashMap.put(goods.getName(),goods.getCount());
                app.setHashMap(hashMap);
                StoreGoodsBean storeGoodsBean = new StoreGoodsBean(hashMap);
                ShoppingCarHistory.getInstance().add(goods.getPhone(),storeGoodsBean);
                Log.e("count",""+ShoppingCarHistory.getInstance().getAllGoodsCount(goods.getPhone()));
                //修改购物车数量
                int allCount = ShoppingCarHistory.getInstance().getAllGoodsCount(goods.getPhone());
                t_count.setText(""+allCount);
                //修改购物车总价
                float price = 0;
                for (CustomerGoods bean : allGoodsList) {
                    if (hashMap.containsKey(bean.getName())) {
                        Integer count = hashMap.get(bean.getName());
                        price += (bean.getPrice()*count*bean.getDiscount())/(float)10;
                        Log.e("商品",bean.getName()+count);
                    }
                }
                t_price.setText(""+price);
                //刷新adapter
                notifyDataSetChanged();
            }
        });
        //当商品数量为零时的view的显示
        if(list.get(i).getCount() > 0){
            holder.delete.setVisibility(View.VISIBLE);
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(""+list.get(i).getCount());
        }else{
            holder.delete.setVisibility(View.INVISIBLE);
            holder.count.setVisibility(View.INVISIBLE);
        }

        return view;
    }
    class ViewHolder{
        NetworkImageView picture;
        TextView name,price,discount,introduction,sales,count;
        ImageView add,delete;
    }



}

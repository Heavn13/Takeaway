package com.example.heavn.fanfan.Sales;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.heavn.fanfan.Bean.Type;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 商家商品类型的adapter
 * Created by Administrator on 2018/9/13 0013.
 */

public class SalesGoodsTypeAdapter extends BaseAdapter {
    private Context context;
    private List<Type> list;
    private MyApp app;
    public int defItem;//声明默认选中的项

    public SalesGoodsTypeAdapter(Context context, List<Type> list) {
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
            view = View.inflate(context, R.layout.item_sales_goods_type, null);
            //实例化对象
            holder.button = view.findViewById(R.id.type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        holder.button.setText(list.get(i).getType());
        if(i == defItem){
            holder.button.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        return view;
    }
    class ViewHolder{
        TextView button;
    }

}

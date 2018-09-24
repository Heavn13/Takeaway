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
import com.example.heavn.fanfan.Bean.Address;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 顾客地址列表的adapter
 * Created by Administrator on 2018/9/17 0017.
 */

public class CustomerAddressAdapter extends BaseAdapter {
    private Context context;
    private List<Address> list;
    private MyApp app;

    public CustomerAddressAdapter(Context context, List<Address> list) {
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
            view = View.inflate(context, R.layout.item_address, null);
            //实例化对象
            holder.address = view.findViewById(R.id.address);
            holder.name = view.findViewById(R.id.name);
            holder.phone = view.findViewById(R.id.phone);
            holder.selected = view.findViewById(R.id.selected);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        holder.address.setText(list.get(i).getAddress());
        holder.name.setText(list.get(i).getName());
        holder.phone.setText(list.get(i).getReceive_phone());
        if(list.get(i).isSelected()){
            holder.selected.setVisibility(View.VISIBLE);
        }else{
            holder.selected.setVisibility(View.INVISIBLE);
        }

        return view;
    }
    class ViewHolder{
        TextView address,name,phone,selected;
    }
}

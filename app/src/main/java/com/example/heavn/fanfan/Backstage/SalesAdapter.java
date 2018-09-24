package com.example.heavn.fanfan.Backstage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 后台审核商家列表的adapter
 * Created by Administrator on 2018/9/10 0010.
 */

public class SalesAdapter extends BaseAdapter {
    private Context context;
    private List<SalesBean> list;
    private MyApp app;

    public SalesAdapter(Context context, List<SalesBean> list) {
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
            view = View.inflate(context, R.layout.item_backsatge, null);
            //实例化对象
            holder.head = view.findViewById(R.id.head);
            holder.username = view.findViewById(R.id.username);
            holder.verify = view.findViewById(R.id.verify);
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
        if(list.get(i).getVerify()){
            holder.verify.setImageResource(R.drawable.verifyed);
        }else{
            holder.verify.setImageResource(R.drawable.unverify);
        }
        return view;
    }
    class ViewHolder{
        NetworkImageView head;//头像
        TextView username;//昵称
        ImageView verify;//审核状态
    }
}

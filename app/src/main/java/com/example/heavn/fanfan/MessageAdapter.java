package com.example.heavn.fanfan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.heavn.fanfan.Bean.Message;
import com.example.heavn.fanfan.Util.DateUtil;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 系统消息的adapter
 */

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> list;
    private MyApp app;

    public MessageAdapter(Context context, List<Message> list) {
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
            view = View.inflate(context, R.layout.item_message, null);
            //实例化对象
            holder.content = view.findViewById(R.id.content);
            holder.read = view.findViewById(R.id.read);
            holder.date = view.findViewById(R.id.date);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //常规控件赋值
        if(list.get(i).isRead()){
            holder.read.setVisibility(View.INVISIBLE);
        }else{
            holder.read.setImageResource(R.drawable.circle);
        }
        holder.content.setText("\u3000"+list.get(i).getContent());
        holder.date.setText(DateUtil.getTime(list.get(i).getDate()));
        return view;
    }

    class ViewHolder{
        TextView content;//内容
        ImageView read;//是否阅读
        TextView date;//日期
    }
}

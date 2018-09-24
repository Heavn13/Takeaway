package com.example.heavn.fanfan.Customer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.heavn.fanfan.Backstage.RiderAdapter;
import com.example.heavn.fanfan.Bean.CustomerComment;
import com.example.heavn.fanfan.Bean.RiderBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BitmapCache;
import com.example.heavn.fanfan.Util.DateUtil;
import com.example.heavn.fanfan.Util.MyApp;

import java.util.List;

/**
 * 顾客评论的adapter
 * Created by Administrator on 2018/9/18 0018.
 */

public class CustomerCommentAdapter extends BaseAdapter {
    private Context context;
    private List<CustomerComment> list;
    private MyApp app;

    public CustomerCommentAdapter(Context context, List<CustomerComment> list) {
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
            view = View.inflate(context, R.layout.item_comment, null);
            //实例化对象
            holder.head = view.findViewById(R.id.head);
            holder.username = view.findViewById(R.id.name);
            holder.rank_text = view.findViewById(R.id.rank_text);
            holder.linearLayout = view.findViewById(R.id.rank);
            holder.text = view.findViewById(R.id.text);
            holder.time = view.findViewById(R.id.time);
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
        //根据评分等级显示文字
        if(list.get(i).getRank() != 0){
            drawStar(holder.linearLayout,list.get(i).getRank()-1);
            if(list.get(i).getRank() == 1){
                    holder.rank_text.setText("差劲！");
            }else if(list.get(i).getRank() == 2 || list.get(i).getRank() == 3){
                holder.rank_text.setText("一般！");
            }else if(list.get(i).getRank() == 4 || list.get(i).getRank() == 5){
                holder.rank_text.setText("超赞！");
            }
        }else{
            holder.rank_text.setText("暂无评分");
        }
        if(!list.get(i).getRank_text().equals("")){
            holder.text.setText(list.get(i).getRank_text());
        }
        holder.time.setText(DateUtil.getTime(list.get(i).getTime()));

        return view;
    }
    class ViewHolder{
        NetworkImageView head;
        TextView username,rank_text,text,time;
        LinearLayout linearLayout;
    }

    //画星星
    private void drawStar(LinearLayout linearLayout, int index){
        for(int i=0;i<=index;i++){
            ImageView imageView = (ImageView) linearLayout.getChildAt(i);
            imageView.setImageResource(R.drawable.selected);
        }

    }
}

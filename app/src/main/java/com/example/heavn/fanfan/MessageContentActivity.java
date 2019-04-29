package com.example.heavn.fanfan;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.heavn.fanfan.Bean.Message;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.DateUtil;
import com.example.heavn.fanfan.Util.MyApp;


/**
 * 系统消息详情页面
 */

public class MessageContentActivity extends BaseActivity implements View.OnClickListener {
    private MyApp app;
    private Message message;
    private TextView content,date;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_content);

        app = (MyApp)getApplication();
        message = app.getMessage();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        content = findViewById(R.id.content);
        content.setText("\u3000\u3000"+message.getContent());
        date = findViewById(R.id.date);
        date.setText(DateUtil.getTime(message.getDate()));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
            default:
                break;

        }
    }

}

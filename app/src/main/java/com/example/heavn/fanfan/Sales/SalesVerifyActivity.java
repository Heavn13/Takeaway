package com.example.heavn.fanfan.Sales;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.SalesBean;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.CircleImageView;
import com.example.heavn.fanfan.Util.ImageDownloadTask;
import com.example.heavn.fanfan.Util.MyApp;
import com.example.heavn.fanfan.Util.UploadFile;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 商家修改个人信息，提交审核页面
 * Created by Administrator on 2018/9/11 0011.
 */

public class SalesVerifyActivity extends BaseActivity implements View.OnClickListener {
    private ImageView back;
    private MyApp app;
    private CircleImageView head;
    private EditText username,phone,master,address,summary;
    private Button submit;
    public static final int CHOOSE_PICTURE = 1;
    public static final int SHOW_PICTURE = 2;
    private PopupWindow popupWindow;
    //用于存储临时照片
    private File headImage = null;
    private int width,height;
    private Uri imageUri;
    private String image_path;
    private Button take_photo,choose_albums,pop_cancel;
    private String s_phone = null,s_username = null,s_master = null,s_address = null,s_summary = null;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            String request = app.getUrl()+"/UpdateSales?phone="+s_phone+"&username="+s_username+"&master="+s_master+"&address="+s_address+"&summary="+s_summary;
            if(UploadFile.uploadFile(headImage,request,"")){
                Toast.makeText(SalesVerifyActivity.this, "信息已提交审核，请等候审核结果", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SalesVerifyActivity.this, "信息提交失败", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_verify);

        app = (MyApp)getApplication();

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        head = findViewById(R.id.head);
        head.setOnClickListener(this);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        master = findViewById(R.id.master);
        address = findViewById(R.id.address);
        summary = findViewById(R.id.summary);

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);

        //获取手机屏幕宽度
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (headImage != null){
            //活动销毁删除系统中的图片
            headImage.delete();
        }
    }

    private void initView(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getSales_phone()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSales").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SalesVerifyActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                if(res.equals("false")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SalesVerifyActivity.this, "该账户查询失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
//                    Log.e("json",res);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SalesBean sales = new Gson().fromJson(res,SalesBean.class);
                            ImageDownloadTask imgTask = new ImageDownloadTask();
                            if(sales.getHead() != null)
                                imgTask.execute(sales.getHead(),head);
                            username.setText(sales.getUsername());
                            phone.setText(sales.getPhone());
                            phone.setEnabled(false);
                            if(sales.getAddress() != null)
                                address.setText(sales.getAddress());
                            if(sales.getMaster() != null)
                                master.setText(sales.getMaster());
                            if(sales.getSummary() != null)
                                summary.setText(sales.getSummary());

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.head:
                displayPopupWindow();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindowView();
                    //设置popupWindow从底部弹出
                    popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                }
                break;
            case R.id.submit:
                s_phone = phone.getText().toString();
                s_username = username.getText().toString();
                s_address = address.getText().toString();
                s_master = master.getText().toString();
                s_summary = summary.getText().toString();
                //中文字符加码
                try{
                    s_username = URLEncoder.encode(s_username,"utf-8");
                    s_master = URLEncoder.encode(s_master,"utf-8");
                    s_address = URLEncoder.encode(s_address,"utf-8");
                    s_summary = URLEncoder.encode(s_summary,"utf-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                if(headImage != null){
                    if(s_phone != null && s_username != null && s_address != null && s_master != null && s_summary != null){
                        new Thread(runnable).start();
                    }else{
                        Toast.makeText(this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "请上传图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.take_photo:
                headImage = new File(Environment.getExternalStorageDirectory(),"headImage.jpg");
                try{
                    if(headImage.exists()){
                        headImage.delete();
                    }
                    headImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //如果android7.0以上的系统，需要做个判断才能调用相机
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(SalesVerifyActivity.this, "com.example.heavn.fanfan.provider", headImage);//7.0
                } else {
                    imageUri = Uri.fromFile(headImage); //7.0以下
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,SHOW_PICTURE);
                break;
            case R.id.choose_albums:
                headImage = new File(Environment.getExternalStorageDirectory(),"headImage.jpg");
                try{
                    if(headImage.exists()){
                        headImage.delete();
                    }
                    headImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(headImage);
                Intent intent2 = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 打开手机相册,设置请求码
                startActivityForResult(intent2, CHOOSE_PICTURE);
                break;
            case R.id.pop_cancel:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            default:
                break;
        }
    }

    //弹出窗口初始化
    public void initPopupWindowView() {
        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popupwindow_photo_style_layout, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupWindow = new PopupWindow(customView, width, height);
        // 自定义view添加触摸事件，设置点击其他区域弹窗消失
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow= null;
                }
                return false;
            }
        });

        //下拉菜单按钮
        take_photo = customView.findViewById(R.id.take_photo);
        take_photo.setOnClickListener(this);
        choose_albums = customView.findViewById(R.id.choose_albums);
        choose_albums.setOnClickListener(this);
        pop_cancel = customView.findViewById(R.id.pop_cancel);
        pop_cancel.setOnClickListener(this);

    }

    //弹出窗口
    public void displayPopupWindow(){
        //关闭软键盘,防止popupWindow覆盖软键盘
        if(getCurrentFocus()!=null)
        {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //对图片的处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //选择图片并剪切
            case CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    //此处启动裁剪程序
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, SHOW_PICTURE);
                }
                break;
            //显示图片
            case SHOW_PICTURE:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //image_path = uri.getPath();
                        if(bitmap.getHeight()*bitmap.getWidth() >= 2048000){
                            sizeCompress(bitmap, headImage);
                        }
                        head.setImageBitmap(bitmap);
                        //直接获取图片的绝对路径，这么简单的方法竟然弄了两个多小时没有解决
                        image_path = headImage.getAbsolutePath();
                        //图片完成设置之后，使popupWindow消失
                        popupWindow.dismiss();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(SalesVerifyActivity.this, "错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //尺寸压缩来压缩图片
    public static void sizeCompress(Bitmap bmp, File file) {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = 14;
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

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
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.heavn.fanfan.Bean.Type;
import com.example.heavn.fanfan.R;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.Util.MyApp;
import com.example.heavn.fanfan.Util.UploadFile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 商家添加商品的人野蛮
 * Created by Administrator on 2018/9/12 0012.
 */

public class SalesAddGoodsActivity extends BaseActivity implements View.OnClickListener{
    private ImageView back,add_picture,display;
    private Boolean visibility = false;
    private MyApp app;
    private LinearLayout linearLayout;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private EditText name,introduction,price,discount,type;
    private String s_name = null,s_introduction = null,s_price = null,s_discount = null,s_type = null,s_spinner = null;
    private Button add_type,add_goods;
    private Spinner spinner;
    private List<String> typeList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    public static final int CHOOSE_PICTURE = 1;
    public static final int SHOW_PICTURE = 2;
    private PopupWindow popupWindow;
    //用于存储临时照片
    private File foodImage = null;
    private int width,height;
    private Uri imageUri;
    private String image_path;
    private Button take_photo,choose_albums,pop_cancel;
    //初始化线程
    private Runnable initRunnable = new Runnable() {
        @Override
        public void run() {
            initView();
        }
    };
    //添加商品内容线程
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            String time = ""+System.currentTimeMillis();
            String request = app.getUrl()+"/AddSalesGoods?phone="+app.getSales_phone()+"&name="+
                    s_name+"&type="+s_spinner+"&introduction="+s_introduction+"&price="+s_price+"&discount="+s_discount+"&time="+time;
//            Log.e("url",request);
            if(UploadFile.uploadFile(foodImage,request,time)){
                Toast.makeText(SalesAddGoodsActivity.this, "商品添加成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(SalesAddGoodsActivity.this, "商品添加失败", Toast.LENGTH_SHORT).show();
            }
            Looper.loop();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_add_goods);

        app = (MyApp)getApplication();

        name = findViewById(R.id.goods_name);
        introduction = findViewById(R.id.goods_introduction);
        price = findViewById(R.id.goods_price);
        discount = findViewById(R.id.goods_discount);
        type = findViewById(R.id.goods_type);

        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        display = findViewById(R.id.display);
        display.setOnClickListener(this);
        linearLayout = findViewById(R.id.linearLayout);
        add_type = findViewById(R.id.add_type);
        add_type.setOnClickListener(this);
        add_goods = findViewById(R.id.add_goods);
        add_goods.setOnClickListener(this);
        add_picture = findViewById(R.id.good_picture);
        add_picture.setOnClickListener(this);
        spinner = findViewById(R.id.spinner);

        //获取手机屏幕宽度
        WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        new Thread(initRunnable).start();
    }

    //初始化信息，主要是初始化商品类型个数
    private void initView(){
        RequestBody requestBody = new FormBody.Builder().add("phone",app.getSales_phone()).build();
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(app.getUrl()+"/GetSalesGoodsType").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        //执行Call
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SalesAddGoodsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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
                            typeList.add("热销");
                            adapter = new ArrayAdapter<>(SalesAddGoodsActivity.this, android.R.layout.simple_spinner_item,typeList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            typeList.clear();
                            typeList.add("热销");
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
                            Log.e("jsonArray",jsonArray.toString());
                            Gson gson = new Gson();
                            for (JsonElement goodsType:jsonArray) {
                                Type type = gson.fromJson(goodsType,Type.class);
                                typeList.add(type.getType());
                            }
                            adapter = new ArrayAdapter<>(SalesAddGoodsActivity.this, android.R.layout.simple_spinner_item,typeList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
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
            case R.id.add_type:
                s_type = type.getText().toString();
                if(!s_type.equals("")){
                    typeList.add(s_type);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "商品类型添加成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add_goods:
                s_spinner = (String)spinner.getSelectedItem();
                s_name = name.getText().toString();
                s_introduction = introduction.getText().toString();
                s_discount = discount.getText().toString();
                s_price = price.getText().toString();
                //中文字符加码
                try{
                    s_spinner = URLEncoder.encode(s_spinner,"utf-8");
                    s_name = URLEncoder.encode(s_name,"utf-8");
                    s_introduction = URLEncoder.encode(s_introduction,"utf-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                if(foodImage != null){
                    if(s_spinner != null && s_name != null && s_introduction != null && s_price != null && s_discount != null){
                        new Thread(runnable).start();
                    }else{
                        Toast.makeText(this, "请把内容填写完整", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "请上传图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.display:
                if(!visibility){
                    linearLayout.setVisibility(View.VISIBLE);
                    visibility = true;
                    display.setBackgroundResource(R.drawable.delete);
                }else{
                    linearLayout.setVisibility(View.GONE);
                    visibility = false;
                    display.setBackgroundResource(R.drawable.add_blue);
                }
                break;
            case R.id.good_picture:
                displayPopupWindow();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    initPopupWindowView();
                    //设置popupWindow从底部弹出
                    popupWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                }
                break;
            case R.id.take_photo:
                foodImage = new File(Environment.getExternalStorageDirectory(),"foodImage.jpg");
                try{
                    if(foodImage.exists()){
                        foodImage.delete();
                    }
                    foodImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                //如果android7.0以上的系统，需要做个判断才能调用相机
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(SalesAddGoodsActivity.this, "com.example.heavn.fanfan.provider", foodImage);//7.0
                } else {
                    imageUri = Uri.fromFile(foodImage); //7.0以下
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,SHOW_PICTURE);
                break;
            case R.id.choose_albums:
                foodImage = new File(Environment.getExternalStorageDirectory(),"foodImage.jpg");
                try{
                    if(foodImage.exists()){
                        foodImage.delete();
                    }
                    foodImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(foodImage);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (foodImage != null){
            //活动销毁删除系统中的图片
            foodImage.delete();
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
                            sizeCompress(bitmap, foodImage);
                        }
                        add_picture.setImageBitmap(bitmap);
                        //直接获取图片的绝对路径，这么简单的方法竟然弄了两个多小时没有解决
                        image_path = foodImage.getAbsolutePath();
                        //图片完成设置之后，使popupWindow消失
                        popupWindow.dismiss();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(SalesAddGoodsActivity.this, "错误", Toast.LENGTH_SHORT).show();
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

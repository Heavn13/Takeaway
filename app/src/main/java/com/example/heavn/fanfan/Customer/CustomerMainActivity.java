package com.example.heavn.fanfan.Customer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.heavn.fanfan.Sales.SalesDetailFragment;
import com.example.heavn.fanfan.Sales.SalesStoreFragment;
import com.example.heavn.fanfan.Util.BaseActivity;
import com.example.heavn.fanfan.R;
/**
 * 顾客的主界面，管理碎片
 * Created by Administrator on 2018/9/16 0016.
 */

public class CustomerMainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {
    private CustomerHomeFragment customerHomeFragment;
    private CustomerOrderFragment customerOrderFragment;
    private CustomerDetailFragment customerDetailFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationBar bottomNavigationBar;
    private int lastSelectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        /**
         * bottomNavigation 设置
         */
        bottomNavigationBar = findViewById(R.id.navigation);
        /** 导航基础设置 包括按钮选中效果 导航栏背景色等 */
        bottomNavigationBar
                .setTabSelectedListener(this)
                .setMode(BottomNavigationBar.MODE_FIXED)
                /**
                 *  setMode() 内的参数有三种模式类型：
                 *  MODE_DEFAULT 自动模式：导航栏Item的个数<=3 用 MODE_FIXED 模式，否则用 MODE_SHIFTING 模式
                 *  MODE_FIXED 固定模式：未选中的Item显示文字，无切换动画效果。
                 *  MODE_SHIFTING 切换模式：未选中的Item不显示文字，选中的显示文字，有切换动画效果。
                 */
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE)
                /**
                 *  setBackgroundStyle() 内的参数有三种样式
                 *  BACKGROUND_STYLE_DEFAULT: 默认样式 如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC
                 *                                    如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                 *  BACKGROUND_STYLE_STATIC: 静态样式 点击无波纹效果
                 *  BACKGROUND_STYLE_RIPPLE: 波纹样式 点击有波纹效果
                 */
                .setInActiveColor(R.color.gray) //未选中颜色
                .setBarBackgroundColor(R.color.white);//导航栏背景色

        /** 添加导航按钮 */
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.home, "主页").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.order_gray, "订单").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.detail, "个人中心").setActiveColorResource(R.color.blue))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise(); //initialise 一定要放在 所有设置的最后一项

        setDefaultFragment();//设置默认导航栏
    }

    /**
     * 设置默认导航栏
     */
    private void setDefaultFragment() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        customerHomeFragment = new CustomerHomeFragment();
        fragmentTransaction.replace(R.id.frameLayout, customerHomeFragment);
        fragmentTransaction.commit();
    }

    /**
     * 设置导航选中的事件
     */
    @Override
    public void onTabSelected(int position) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (customerHomeFragment == null) {
                    customerHomeFragment = new CustomerHomeFragment();
                }
                transaction.replace(R.id.frameLayout, customerHomeFragment);
                break;
            case 1:
                if (customerOrderFragment == null) {
                    customerOrderFragment = new CustomerOrderFragment();
                }
                transaction.replace(R.id.frameLayout, customerOrderFragment);
                break;
            case 2:
                if (customerDetailFragment == null) {
                    customerDetailFragment= new CustomerDetailFragment();
                }
                transaction.replace(R.id.frameLayout, customerDetailFragment);
                break;
            default:
                break;
        }

        transaction.commit();// 事务提交
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

}

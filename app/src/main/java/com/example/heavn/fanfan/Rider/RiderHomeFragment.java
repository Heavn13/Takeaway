package com.example.heavn.fanfan.Rider;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.heavn.fanfan.R;

/**
 * 骑手订单主页面碎片，管理新订单，待完成订单，完成订单三个碎片
 * Created by Administrator on 2018/9/11 0011.
 */

public class RiderHomeFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroup;
    private RadioButton new_order,accepted_order,finish_order;
    private NewOrderFragment newOrderFragment;
    private AcceptedOrderFragment acceptedOrderFragment;
    private FinishOrderFragment finishOrderFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_home,container,false);

        //不写这个在外层fragment切换时不会显示第一个
        newOrderFragment = new NewOrderFragment();
        acceptedOrderFragment = new AcceptedOrderFragment();
        finishOrderFragment = new FinishOrderFragment();
//
        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        new_order = view.findViewById(R.id.new_order);
        new_order.setChecked(true);
        accepted_order = view.findViewById(R.id.accept_order);
        accepted_order.setChecked(false);
        finish_order = view.findViewById(R.id.finish_order);
        finish_order.setChecked(false);

        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentManager fm = this.getFragmentManager();
        //开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        switch (checkedId){
            case R.id.new_order:
                if (newOrderFragment == null) {
                    newOrderFragment = new NewOrderFragment();
                }
                transaction.replace(R.id.rider_home_frameLayout,newOrderFragment);
                break;
            case R.id.accept_order:
                if (acceptedOrderFragment == null) {
                    acceptedOrderFragment = new AcceptedOrderFragment();
                }
                transaction.replace(R.id.rider_home_frameLayout, acceptedOrderFragment);
                break;
            case R.id.finish_order:
                if (finishOrderFragment == null) {
                    finishOrderFragment = new FinishOrderFragment();
                }
                transaction.replace(R.id.rider_home_frameLayout, finishOrderFragment);
                break;
            default:
                break;
        }
        transaction.commit();// 事务提交
    }
}

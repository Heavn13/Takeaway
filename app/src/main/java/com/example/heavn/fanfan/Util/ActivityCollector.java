package com.example.heavn.fanfan.Util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
/**
 * 活动管理，主要用于修改密码
 * Created by Administrator on 2018/9/10 0010.
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void add(Activity activity){
        activities.add(activity);
    }

    public static void remove(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}

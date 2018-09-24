package com.example.heavn.fanfan.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间处理的工具类
 * Created by Administrator on 2018/6/1 0001.
 */

public class DateUtil {

    //将时间戳转化成日期
    public static String getTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(time);
        String result = format.format(date);
        return result;
    }

    //获取两个日期之间间隔的天数
    public static int getDays(String beginTime,String endTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long days = 0;
        try {
            Date begin = format.parse(beginTime);
            Date end = format.parse(endTime);
            days = (end.getTime() - begin.getTime())/(24*60*60*1000);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return (int)days+1;
    }

    //日期加上一个数之后的日期
    public static String calDays(String beginTime,int day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = "";
        try {
            Date begin = sdf.parse(beginTime);
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(begin);
            gc.add(GregorianCalendar.DATE,day);
            Date next = gc.getTime();
            time = sdf.format(next);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return time;
    }

    //解析传入参数日期的年
    public static int getYearOfDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date d;
        try {
            d = format.parse(date);
            calendar.setTime(d);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return calendar.get(Calendar.YEAR);
    }

    //解析传入参数日期的月
    public static int getMonthOfDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date d;
        try {
            d = format.parse(date);
            calendar.setTime(d);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return calendar.get(Calendar.MONTH)+1;
    }

    //解析传入参数日期的年日
    public static int getDayOfDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date d;
        try {
            d = format.parse(date);
            calendar.setTime(d);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    //获取当前时间
    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(System.currentTimeMillis());
    }

    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(System.currentTimeMillis());
    }

    //获取当前时间到开始时间间隔的天数
    public static int getIndex(String beginTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long days = 0;
        try {
            Date begin = format.parse(beginTime);
            Date end = format.parse(getCurrentDate());
            days = (end.getTime() - begin.getTime())/(24*60*60*1000);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return (int)days;
    }

    //判断两个日期的大小
    public static boolean compareDate(String a, String b){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long days = 0;
        try {
            Date begin = format.parse(a);
            Date end = format.parse(b);
            days = (begin.getTime() - end.getTime())/(24*60*60*1000);
            //a比b大
            if (days > 0) return true;
            else if (days < 0) return false;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    //判断两个时间的大小
    public static boolean compareTime(String a, String b){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        long internal = 0;
        try {
            Date begin = format.parse(a);
            Date end = format.parse(b);
            internal = begin.getTime() - end.getTime();
            //a比b大
            if (internal >= 0) return true;
            else if (internal < 0) return false;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    //获取当前年份
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    //获取当前月份
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    //获取当前日期是该月的第几天
    public static int getCurrentDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    //获取当前日期是该周的第几天
    public static int getCurrentDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    //获取当前是几点
    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);//二十四小时制
    }

    //获取当前是几分
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    //获取当前秒
    public static int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

 // 根据传入的年份和月份，获取当前月份的日历分布
    public static int[][] getDayOfMonthFormat(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);//设置时间为每月的第一天
        //设置日历格式数组,6行7列
        int days[][] = new int[6][7];
        //设置该月的第一天是周几
        int daysOfFirstWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //设置本月有多少天
        int daysOfMonth = getDaysOfMonth(year, month);
        //设置上个月有多少天
        int daysOfLastMonth = getLastDaysOfMonth(year, month);
        int dayNum = 1;
        int nextDayNum = 1;
        //将日期格式填充数组
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < days[i].length; j++) {
                if (i == 0 && j < daysOfFirstWeek - 1) {
                    days[i][j] = 0;
                } else if (dayNum <= daysOfMonth) {
                    days[i][j] = dayNum++;
                } else {
                    days[i][j] = 0;
                }
            }
        }
        return days;
    }

    //根据传入的年份和月份，判断上一个有多少天
    public static int getLastDaysOfMonth(int year, int month) {
        int lastDaysOfMonth = 0;
        if (month == 1) {
            lastDaysOfMonth = getDaysOfMonth(year - 1, 12);
        } else {
            lastDaysOfMonth = getDaysOfMonth(year, month - 1);
        }
        return lastDaysOfMonth;
    }

    //根据传入的年份和月份，判断当前月有多少天
    public static int getDaysOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (isLeap(year)) {
                    return 29;
                } else {
                    return 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
        return -1;
    }

   //判断是否为闰年
    public static boolean isLeap(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        }
        return false;
    }

}

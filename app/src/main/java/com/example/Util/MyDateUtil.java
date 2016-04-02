package com.example.Util;

import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by asus-cp on 2016-03-26.
 */
public class MyDateUtil {
    private static String tag="MyDateUtil";
    /**
     * 将字符串转换成Calendar对象
     */
    public static Calendar transformStringToDate(String s){
         Calendar calendar=null;
        try {
            Context context=MyApplication.getContext();
            AlarmManager mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setTimeZone("GMT+08:00");
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");//按给定模式进行解析
            simpleDateFormat.setLenient(false);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());//用上海时区
            Log.d(tag, "默认时区=" + simpleDateFormat.getTimeZone());
            Date date= simpleDateFormat.parse(s);
            Log.d(tag,"s="+s);
            Log.d(tag,"date="+date);
            calendar=Calendar.getInstance();
            calendar.setTime(date);
            Log.d(tag, "calendar=" + calendar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 将Calendar对象按指定的规则进行格式化
     */
    public static String getFormatString(Calendar calendar){
        Calendar currentTime=Calendar.getInstance();
        Calendar today=Calendar.getInstance();
        today.set(today.get(Calendar.YEAR),today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),0,0,0);//设置成今天的0点0分0秒
        Calendar yesterday=Calendar.getInstance();
        yesterday.set(yesterday.get(Calendar.YEAR),yesterday.get( Calendar.MONTH),
                yesterday.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        yesterday.add(Calendar.DAY_OF_MONTH,-1);//在今天的基础上减去一天，就是昨天,也就是设置成昨天的0点0分0秒
        //如果是今天发的
        if(calendar!=null&&calendar.after(today)){
            int xsc=currentTime.get(Calendar.HOUR_OF_DAY)-calendar.get(Calendar.HOUR_OF_DAY);
            if(xsc<1){
                int fzc=currentTime.get(Calendar.MINUTE)-calendar.get(Calendar.MINUTE);
                if(fzc<0){
                    int fzc1=fzc+60;
                    return fzc1+"分钟前";
                }else if(fzc==0){
                    return "刚刚";
                }else{
                    return fzc+"分钟前";
                }
            }else{
                return xsc+"小时前";
            }

        }else if(calendar!=null&&calendar.before(today)&&calendar.after(yesterday)){
            String pateem="HH:mm";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(pateem);
            return "昨天"+simpleDateFormat.format(calendar.getTime());

        }else {
            String pattem = "M-dd";
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(pattem);
            return simpleDateFormat.format(calendar.getTime());
        }

    }

}

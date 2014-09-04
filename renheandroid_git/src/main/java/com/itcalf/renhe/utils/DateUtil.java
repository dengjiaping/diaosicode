package com.itcalf.renhe.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.itcalf.renhe.R;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

    private static long MINUTE = 60L * 1000L;
    private static long HOUR   = 60L * 60L * 1000L;
    private static long DAY    = 24L * 60L * 60L * 1000L;

    public static String formatToHumanReadable(Context context, Date date) {
    	if (context == null || date == null) {
            return null;
        }
    	
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        if (diff > DAY * 5) {// 如果大于5天，就用具体的时间
            SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.readable_date_common));
            return dateFormat.format(date);
        } else if (diff > DAY) {// 如果一天，就用天做时间
            return MessageFormat.format(context.getString(R.string.readable_date_days), diff / DAY);
        } else if (diff > HOUR) {
            return MessageFormat.format(context.getString(R.string.readable_date_hours), diff / HOUR);
        } else {
            int minutes = (int) (diff / MINUTE);
            if (minutes > 0) {
                return MessageFormat.format(context.getString(R.string.readable_date_minutes), diff / MINUTE);
            } else {
                return context.getString(R.string.readable_date_justnow);
            }
        }
    }
    
    public static String formatToGroupTagByDay(Context context, Date date) {
    	if (context == null || date == null) {
    		return context.getString(R.string.readable_date_more);
    	}
    	Date now = new Date();
    	long diff = now.getTime() - date.getTime();
    	if (diff > DAY * 7) {// 如果大于7天，就用具体的时间
    		Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            calendar.setTime(date);
            if (calendar.get(Calendar.YEAR) == year){
            	SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.readable_date_md));
            	return dateFormat.format(date);
            }else{
            	SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.readable_date_ymd));
            	return dateFormat.format(date);
            }
    		
    	}  else if (diff > DAY * 3) {// 如果>3天，就用天做时间
    		return MessageFormat.format(context.getString(R.string.readable_date_days), diff / DAY);
    	} else if (diff > DAY * 2 && diff <= DAY * 3) {// 如果>2&<3天，就前天做时间
    		return MessageFormat.format(context.getString(R.string.readable_date_before_yesterday), diff / DAY);
    	} else if (diff > DAY && diff <= DAY * 2) {// 如果>1&<2天，就昨天做时间
    		return MessageFormat.format(context.getString(R.string.readable_date_yesterday), diff / DAY);
    	} else if (diff > HOUR) {
    		return MessageFormat.format(context.getString(R.string.readable_date_hours), diff / HOUR);
    	} else {
    		int minutes = (int) (diff / MINUTE);
    		if (minutes > 1) {
    			return MessageFormat.format(context.getString(R.string.readable_date_minutes), diff / MINUTE);
    		} else {
    			return context.getString(R.string.readable_date_justnow);
    		}
    	}
    }

    public static String formatToGroupTag(Context context, Date date) {
        if (context == null || date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(date);
        
        if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month
            && calendar.get(Calendar.DAY_OF_MONTH) == day) {
            return context.getString(R.string.readable_date_today);
        } else if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.WEEK_OF_YEAR) == week) {
            return context.getString(R.string.readable_date_week);
        } else if (calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month) {
            return context.getString(R.string.readable_date_month);
        } else {
            return context.getString(R.string.readable_date_more);
        }

    }
}

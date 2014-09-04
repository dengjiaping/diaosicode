package com.itcalf.renhe.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 消息工具类
 * 
 * @author xp
 * 
 */
public class ToastUtil
{
    public static void showToast(Context context, String message) {
        showToast(context, message, false);
    }

    public static void showErrorToast(Context context, String message) {
        showToast(context, message, true);
    }

    private static void showToast(Context context, String message,
            boolean errorToast) {
        Toast toast;

        if (errorToast) {
            /*
             * We want the toast text to be red. The exact layout of the default
             * toast is not specified, so the following may not work in future
             * android versions. Thus, if the layout is not the way we expect,
             * we just leave the text color as is.
             */
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            View view = toast.getView();
            if (view instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) view;
                if (linearLayout.getChildCount() > 0) {
                    View child = linearLayout.getChildAt(0);
                    if (child instanceof TextView) {
                        ((TextView) child).setTextColor(
                                Color.rgb(255, 100, 100));
                    }
                }
            }
        }
        else {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        }

        toast.show();
    }
    
    public static void showNetworkError(Context context) {
    	showToast(context, "网络访问错误，请检查网络设置！", false);
    }
    
    public static void showNetworkWIFI(Context context) {
    	showToast(context, "当前使用的是WIFI网络", false);
    }
    
    public static void showNetworkMobile(Context context) {
    	showToast(context, "当前使用的是WAP网络", false);
    }

}
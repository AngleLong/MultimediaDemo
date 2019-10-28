package com.angle.multimediademo.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author : Angle
 * 创建时间 : 2019/4/17 11:59
 * 描述 :
 */
public class NewToastUtils {


    private static Toast toast;
    private static int textview_id;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void show(Context context, final String str) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    toast.setText(str);
                }
            });

        }
        if (textview_id == 0) {
            textview_id = Resources.getSystem().getIdentifier("message", "id", "android");
        }
        ((TextView) toast.getView().findViewById(textview_id)).setGravity(Gravity.CENTER);
        toast.show();
    }

    public static void show(Context context, int resId) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        if (textview_id == 0) {
            textview_id = Resources.getSystem().getIdentifier("message", "id", "android");
        }
        ((TextView) toast.getView().findViewById(textview_id)).setGravity(Gravity.CENTER);
        toast.show();
    }
}
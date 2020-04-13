package com.easyder.wrapper.base.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyder.wrapper.R;

/**
 * Auther by Winds on 2016/06/10
 * Email heardown@163.com
 */
public class ToastView {

    static Toast toast;

    /**
     * 自定义的垂直排列的图文Toast提示
     *
     * @param context
     * @param msg
     * @param resId
     * @param duration
     */
    public static void showVerticalToast(Context context, String msg, int resId, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), "", duration);
        }
        toast.setDuration(duration);
        View view = View.inflate(context.getApplicationContext(), R.layout.toast_vertical_view, null);
        ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        tv_txt.setText(msg);
        iv_image.setImageResource(resId);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 指定时间的Toast提示
     *
     * @param context
     * @param msg
     * @param resId
     */
    public static void showVerticalToast(Context context, String msg, int resId) {
        showVerticalToast(context, msg, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 指定图片和时间的Toast提示
     *
     * @param context
     * @param msg
     */
    public static void showVerticalToast(Context context, String msg) {
        showVerticalToast(context, msg, R.drawable.ic_notice, Toast.LENGTH_SHORT);
    }

    /**
     * 设置水平排列的带图片的Toast提示
     *
     * @param context
     * @param msg
     * @param resId
     * @param duration
     */
    public static void showHorizontalToast(Context context, String msg, int resId, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, "", duration);
        }
        toast.setDuration(duration);
        View view = View.inflate(context, R.layout.toast_horizontal_view, null);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
        tv_txt.setText(msg);
        iv_image.setImageResource(resId);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 指定时间的水平排列的带图片的Toast提示
     *
     * @param context
     * @param msg
     * @param resId
     */
    public static void showHorizontalToast(Context context, String msg, int resId) {
        showHorizontalToast(context, msg, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 指定时间和图片的水平排列Toast提示
     *
     * @param context
     * @param msg
     */
    public static void showHorizontalToast(Context context, String msg) {
        showHorizontalToast(context, msg, R.drawable.ic_notice, Toast.LENGTH_SHORT);
    }

    /**
     * 指定位置的文本Toast提示
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void showToast(Context context, String msg, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), "", duration);
        }
        toast.setDuration(duration);
        View view = View.inflate(context.getApplicationContext(), R.layout.toast_normal, null);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        tv_txt.setText(msg);
        toast.setView(view);

        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, context.getResources().getDisplayMetrics().heightPixels / 6);

        toast.show();
    }

    /**
     * 指定位置的文本Toast提示
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void showToast(Context context, String msg, int duration,int x,int y) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), "", duration);
        }
        toast.setDuration(duration);
        View view = View.inflate(context.getApplicationContext(), R.layout.toast_normal, null);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        tv_txt.setText(msg);
        toast.setView(view);

        toast.setGravity(Gravity.CENTER_HORIZONTAL, x, y);

        toast.show();
    }

    /**
     * 指定位置、指定时长的文本Toast提示
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * 文本Toast提示
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void showToastInCenter(Context context, int id, int duration) {
//        if (toast == null) {
        if(context==null){
            return;
        }
        toast = Toast.makeText(context.getApplicationContext(), "", duration);
//        }
        toast.setDuration(duration);
        View view = View.inflate(context.getApplicationContext(), R.layout.toast_normal, null);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        tv_txt.setText(id);
        toast.setView(view);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 文本Toast提示
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void showToastInCenter(Context context, String msg, int duration) {
//        if (toast == null) {
            if(context==null){
                return;
            }
            toast = Toast.makeText(context.getApplicationContext(), "", duration);
//        }
        toast.setDuration(duration);
        View view = View.inflate(context.getApplicationContext(), R.layout.toast_normal, null);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        tv_txt.setText(msg);
        toast.setView(view);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    /**
     * 文本Toast顶部提示
     *
     * @param context
     * @param msg
     * @param duration
     */
    public static void showToastInTop(Context context, String msg, int duration) {
        if(context==null){
            return;
        }
//        if (toast == null) {
        toast = Toast.makeText(context.getApplicationContext(), "", duration);
//        }
        toast.setDuration(duration);
        View view = View.inflate(context.getApplicationContext(), R.layout.toast_normal, null);
        TextView tv_txt = (TextView) view.findViewById(R.id.tv_txt);
        tv_txt.setText(msg);
        toast.setView(view);

        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
    /**
     * 设置View Toast提示
     *
     * @param context
     * @param duration
     */
    public static void showViewToast(Context context, View view, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, "", duration);
        }
        toast.setDuration(duration);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 指定位置、指定时长的Toast提示
     *
     * @param context
     * @param msg
     */
    public static void showToastInCenter(Context context, String msg) {
        showToastInCenter(context, msg, Toast.LENGTH_LONG);
    }


    /**
     * 展示原生的Toast提示
     *
     * @param context
     * @param msg
     */
    public static void showOrigianlToast(Context context, String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}

package com.easyder.wrapper.utils;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * 作者：Arrom
 * 日期： 2018/10/10
 * 描述：
 */
public class GlideUtil {

    private final static String TAG = "GlideUtil";

    public static <T> void display(T context, ImageView imgView, String url) {
        if (context instanceof Activity) {
            Glide.with((Activity) context).load(url).into(imgView);
        } else if (context instanceof Fragment) {
            Glide.with((Fragment) context).load(url).into(imgView);
        } else if (context instanceof android.support.v4.app.Fragment) {
            Glide.with(((android.support.v4.app.Fragment) context).getContext()).load(url).into(imgView);
        } else {
            LogUtils.e(TAG, "context disable（Activity or Fragment）");
        }
    }

    public static <T> void display(T context, ImageView imgView, int resImg) {
        if (context instanceof Activity) {
            Glide.with((Activity) context).load(resImg).into(imgView);
        } else if (context instanceof Fragment) {
            Glide.with((Fragment) context).load(resImg).into(imgView);
        } else if (context instanceof android.support.v4.app.Fragment) {
            Glide.with(((android.support.v4.app.Fragment) context).getContext()).load(resImg).into(imgView);
        } else {
            LogUtils.e(TAG, "context disable（Activity or Fragment）");
        }
    }

    public static <T> void display(T context, ImageView imgView, String url, int placeholderId) {
        if (context instanceof Activity) {
            if (placeholderId > 0) {
                RequestOptions options = new RequestOptions()
                        .placeholder(placeholderId) ;
                Glide.with((Activity) context).asBitmap().apply(options).load(url).into(imgView);
            } else {
                Glide.with((Activity) context).load(url).into(imgView);
            }
        } else if (context instanceof Fragment) {
            Glide.with((Fragment) context).load(url).into(imgView);
        }  else if (context instanceof android.support.v4.app.Fragment) {
            Glide.with(((android.support.v4.app.Fragment) context).getContext()).load(url).into(imgView);
        }  else {
            LogUtils.e(TAG, "context disable（Activity or Fragment）");
        }

    }
    public static void clear(View view) {
        Glide.with(view.getContext()).clear(view);
    }

}

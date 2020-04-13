package com.easyder.wrapper.core.manager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.easyder.wrapper.ManagerConfig;

/**
 * 本地及远程服务器的图片管理
 *
 * @author 刘琛慧
 *         date 2016/6/20.
 */
public class ImageManager {

    private static ImageManager instance;

    private ImageManager() {

    }

    public static ImageManager getDefault() {
        if (instance == null) {
            synchronized (ImageManager.class) {
                instance = new ImageManager();
            }
            return instance;
        }
        return instance;
    }


    /**
     * 加载指定URL的图片并设置到targetView
     *
     * @param context
     * @param url
     * @param targetView
     */
    public static void load(Context context, String url, final View targetView) {
        if (!url.startsWith("http")) {
            if (url.startsWith("/")) {
                url = url.substring(1, url.length());
            }
            url = ManagerConfig.getInstance().getBaseHost() + url;
        }

//        Glide.with(context).load(url).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                if (targetView instanceof ImageView) {
//                    ((ImageView) targetView).setImageDrawable(resource);
//                } else {
//                    targetView.setBackgroundDrawable(resource);
//                }
//            }
//        });
    }


    public static void load(Context context, ImageView view, String url) {
        load(context, view, url, -1);
    }

    /**
     * 初始化占位图，加载指定URL的图片并设置到targetView
     *
     * @param context
     * @param view
     * @param url
     * @param placeholder
     */
    public static void load(Context context, ImageView view, String url, int placeholder) {
        load(Glide.with(context), view, url, placeholder, -1);
    }

    public static void load(Context context, ImageView view, String url, int placeholder, int error) {
        load(Glide.with(context), view, url, placeholder, error);
    }
    public static void loadImg(Context context, ImageView view, String url, int placeholder, int error) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder).error(error);
        Glide.with(context).load(url).apply(options).into(view);
    }
    /**
     * 加载图片
     *
     * @param view
     * @param url
     * @param placeholder 值为 -1 / 0 表示不加载
     * @param error       值为 -1 / 0 表示不加载
     */
    public static void load(Context context, ImageView view, String url, BitmapTransformation transformation, int placeholder, int error) {
        load(Glide.with(context), view, url, transformation, placeholder, error);
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param view
     * @param url
     * @param placeholder 值为 -1 / 0 表示不加载
     * @param error       值为 -1 / 0 表示不加载
     */
    public static void load(RequestManager manager, ImageView view, String url, int placeholder, int error) {
        load(manager, view, url, null, placeholder, error);
    }

    /**
     * 加载图片
     *
     * @param manager
     * @param view
     * @param url
     * @param placeholder 值为 -1 / 0 表示不加载
     * @param error       值为 -1 / 0 表示不加载
     */
    public static void load(RequestManager manager, ImageView view, String url, BitmapTransformation transformation, int placeholder, int error) {
        if (url == null) {
            url = "";
        }
//        if(url.equals("")){
//
//            return;
//        }
        if (!url.startsWith("http")) {
            url = String.format("%1$s%2$s", ManagerConfig.getInstance().getBaseHost(), url.startsWith("/") ? url.substring(1, url.length()) : url);
        }
//        RequestOptions options = new RequestOptions();
//        if (placeholder != 0 && placeholder != -1) {
//            options.placeholder(placeholder);
//        }
//
//        if (transformation != null) {
//            options.transform(transformation);
//        }
//
//        if (error != 0 && error != -1) {
//            options.error(error);
//        }
//        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//        manager.load(url).apply(options).into(view);
        manager.load(url).into(view);
    }


}

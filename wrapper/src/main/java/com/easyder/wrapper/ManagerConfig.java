package com.easyder.wrapper;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.easyder.wrapper.core.network.NetInterceptor;
import com.easyder.wrapper.core.scheduler.SafeDispatchHandler;
import com.easyder.wrapper.utils.LogUtils;
import com.easyder.wrapper.utils.PreferenceUtils;
import com.easyder.wrapper.utils.SystemUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.utils.HttpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Auther:  winds
 * Data:    2018/3/12
 * Version: 1.0
 * Desc:
 */

public class ManagerConfig {
    private Application context;
    private String host;
    private Handler mMainHandler;

    private ManagerConfig() {
        mMainHandler = new SafeDispatchHandler(Looper.getMainLooper());
    }


    private static class ConfigBuilder {
        private static ManagerConfig holder = new ManagerConfig();
    }

    /**
     * 获取实例化的方法  第一次实例化 请在application   同时调用init方法
     *
     * @return
     */
    public static ManagerConfig getInstance() {
        return ConfigBuilder.holder;
    }

    /**
     * 第一次实例化调用
     *
     * @param app
     * @return
     */
    public ManagerConfig init(Application app) {
        context = app;
        return this;
    }

    /**
     * 设置通用的host 如不设置  默认为空
     *
     * @param host
     * @return
     */
    public ManagerConfig setBaseHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 获取Application对象
     *
     * @return
     */
    public Application getApplicationContext() {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        return context;
    }

    /**
     * 获取主线程handler
     *
     * @return
     */
    public Handler getMainThreadHandler() {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        return mMainHandler;
    }

    /**
     * 获取主线程threadId
     *
     * @return
     */
    public long getMainThreadId() {
        return getMainThreadHandler().getLooper().getThread().getId();
    }

    /**
     * 获取通用host 在未设置时返回空
     *
     * @return
     */
    public String getBaseHost() {
        return host == null ? "" : host;
    }

    /**
     * 初始化okgo 默认提供通用实现
     *
     * @return
     */
    public ManagerConfig initHttpClient() {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo ");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addNetworkInterceptor(new NetInterceptor());
        builder.addInterceptor(loggingInterceptor);
        builder.retryOnConnectionFailure(false);

        //全局的读取超时时间
        builder.readTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS);

        //使用数据库保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));
        builder.cookieJar(new CookieJar() {//这里可以做cookie传递，保存等操作
            ArrayList<Cookie> cookies = new ArrayList<>();
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {//可以做保存cookies操作
                LogUtils.d("cookies url: " + url.toString());
                for (Cookie cookie : cookies)
                {
                    LogUtils.d("cookies: " + cookie.toString());
                }
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {//加载新的cookies
                if(cookies == null){
                    cookies = new ArrayList<>();
                }
                if(PreferenceUtils.getPreference(context,"PREHOST",false)){
                    String domain = url.host() != null ? url.host():getBaseHost();
                    if(domain != null && domain.trim().length() > 0) {
                        Cookie cookie = new Cookie.Builder()
                                .hostOnlyDomain(domain)
                                .name("version").value("TL_stage")
                                .build();
                        cookies.add(cookie);
                        LogUtils.d("cookies: " + cookie.toString());
                    }
                }else {
                    cookies.clear();
                }
                return cookies;
            }
        });
        return initHttpClient(builder.build());
    }

    /**
     * 初始化OkGo  根据需求定制httpclient
     *
     * @param client
     * @return
     */
    public ManagerConfig initHttpClient(OkHttpClient client) {
        HttpUtils.checkNotNull(context, "please call ManagerConfig.getInstance().init() first in application!");
        OkGo.getInstance().init(context)
                .setOkHttpClient(client)
                .setRetryCount(0)     //为避免收银机生成订单时重连生成多个订单  不设置重试次数 请勿改动
                .addCommonHeaders(new HttpHeaders("user_agent", SystemUtils.getDeviceId(context)));
        return this;
    }

    /**
     * 设置log打印方式
     *
     * @param path      此路径用来配置log写入文件的路径
     * @param debugable 是否再控制台打印log
     * @param writeable 是否开启文件打印log         默认i级别及以上才会打印
     * @return
     */
    public ManagerConfig setLogConfig(String path, boolean debugable, boolean writeable) {
        LogUtils.setLogConfig(path, debugable, writeable);
        return this;
    }

}

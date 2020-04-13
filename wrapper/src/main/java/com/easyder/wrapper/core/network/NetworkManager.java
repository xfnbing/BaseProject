package com.easyder.wrapper.core.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.easyder.wrapper.BuildConfig;
import com.easyder.wrapper.base.presenter.Callback;
import com.easyder.wrapper.base.view.MvpView;
import com.easyder.wrapper.utils.Base64;
import com.easyder.wrapper.utils.Base64Utils;
import com.easyder.wrapper.utils.LogUtils;
import com.easyder.wrapper.utils.SystemUtils;
import com.easyder.wrapper.utils.UIUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.request.DeleteRequest;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.PutRequest;
import com.lzy.okgo.request.base.BodyRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author 刘琛慧
 * date 2016/5/30.
 */
public class NetworkManager {

    private static NetworkManager instance;

    private NetworkManager() {
    }

    /**
     * 单例构造方法
     *
     * @return
     */
    public static NetworkManager getDefault() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                instance = new NetworkManager();
            }
            return instance;
        }
        return instance;
    }


    /**
     * Get请求
     * 执行加载数据,如果有做数据缓存，先从缓存里面读取数据,
     * 如果缓存数据有效，返回缓存数据，如果缓存失效，重新从
     * 网路请求数据，并将数据缓存到本地SD卡,记录缓存写入时间
     *
     * @param callback 数据加载成功后的回调方法
     */
    public void doGet(RequestInfo requestInfo, final Callback callback) {
        if (!isNetworkConnected()) {    //无网络请求
            ResponseInfo responseInfo = new ResponseInfo(ResponseInfo.NO_INTERNET_ERROR);
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setUrl(requestInfo.url);
            callback.onError(responseInfo);
            return;
        }

        GetRequest getRequest = OkGo.get(requestInfo.url);
        getRequest.headers("X-Requested-With", "XMLHttpRequest");
        getRequest.headers("terNo", SystemUtils.SN);
        getRequest.headers("Bra-Id", requestInfo.braId);
        getRequest.headers("Mer-Id", requestInfo.merId);
        getRequest.headers("Request-Tk", Base64Utils.base64Encode(requestInfo.getToken()));
        getRequest.headers("OperatorCode", requestInfo.operatorCode);
        getRequest.headers("Platform", "app");

        ArrayMap<String, Object> params = requestInfo.requestParams;
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                getRequest.params(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        getRequest.execute(new ResponseCallback(callback, requestInfo));
    }


    /**
     * Post请求
     * 执行加载数据,如果有做数据缓存，先从缓存里面读取数据,
     * 如果缓存数据有效，返回缓存数据，如果缓存失效，重新从
     * 网路请求数据，并将数据缓存到本地SD卡,记录缓存写入时间
     *
     * @param requestInfo 请求体
     * @param callback    数据加载成功后的回调方法
     */
    public void doPost(RequestInfo requestInfo, final Callback callback) {
        if (!isNetworkConnected()) {
            ResponseInfo responseInfo = new ResponseInfo(ResponseInfo.NO_INTERNET_ERROR);
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setUrl(requestInfo.url);
            callback.onError(responseInfo);
            return;
        }

        PostRequest postRequest = OkGo.post(requestInfo.getUrl());
        if (requestInfo.getContentType().equals(RequestInfo.CONTENT_TYPE_JSON)
                && !requestInfo.getRequestParams().containsKey("file")) {
            postRequest.upJson(getJsonData(requestInfo));
        } else  if (requestInfo.getRequestParams().containsKey("=")) {
            postRequest.upString(requestInfo.getRequestParams().get("=").toString());
        } else {
            postRequest = (PostRequest) setParams(requestInfo.getRequestParams(), postRequest);
        }
        postRequest.headers("terNo", SystemUtils.SN);
        postRequest.headers("Bra-Id", requestInfo.braId);
        postRequest.headers("Mer-Id", requestInfo.merId);
        postRequest.headers("OperatorCode", requestInfo.operatorCode);
        postRequest.headers("Request-Tk", Base64Utils.base64Encode(requestInfo.getToken()));
        postRequest.headers("Platform", "app");
        
        if(requestInfo.headers!=null&&requestInfo.headers.size()>0){
            for (String key:requestInfo.headers.keySet()) {
                if(key!=null&&!TextUtils.isEmpty(key)){
                    postRequest.headers(key, requestInfo.headers.get(key)+"");
                }
            }
        }

//        postRequest.headers("Connection", "close");
        postRequest.execute(new ResponseCallback(callback, requestInfo));
    }

    /**
     * del请求
     * 执行加载数据,如果有做数据缓存，先从缓存里面读取数据,
     * 如果缓存数据有效，返回缓存数据，如果缓存失效，重新从
     * 网路请求数据，并将数据缓存到本地SD卡,记录缓存写入时间
     *
     * @param requestInfo 请求体
     * @param callback    数据加载成功后的回调方法
     */
    public void doDelete(RequestInfo requestInfo, final Callback callback) {
        if (!isNetworkConnected()) {
            ResponseInfo responseInfo = new ResponseInfo(ResponseInfo.NO_INTERNET_ERROR);
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setUrl(requestInfo.url);
            callback.onError(responseInfo);
            return;
        }

        DeleteRequest delRequest = OkGo.delete(requestInfo.getUrl());
        delRequest = (DeleteRequest) CustomerRequest(requestInfo, delRequest);
        // postRequest.headers("X-Requested-With", "XMLHttpRequest");
//        postRequest.headers("braId",wrap);
        delRequest.headers("terNo", SystemUtils.SN);
        delRequest.headers("Bra-Id", requestInfo.braId);
        delRequest.headers("Mer-Id", requestInfo.merId);
        delRequest.headers("Request-Tk", Base64Utils.base64Encode(requestInfo.getToken()));
        delRequest.headers("OperatorCode", requestInfo.operatorCode);
        delRequest.headers("Platform", "app");
//        delRequest.headers("Connection", "close");
        delRequest.execute(new ResponseCallback(callback, requestInfo));
    }

    private BodyRequest CustomerRequest(RequestInfo requestInfo, BodyRequest delRequest) {
        if(requestInfo.getRequestParams() != null){
            if (requestInfo.getContentType().equals(RequestInfo.CONTENT_TYPE_JSON)) {
                delRequest.upJson(getJsonData(requestInfo));
            }  else  if (requestInfo.getRequestParams().containsKey("=")) {
                delRequest.upString(requestInfo.getRequestParams().get("=").toString());
            } else  {
                delRequest =setParams(requestInfo.getRequestParams(), delRequest);
            }
        }
        return delRequest;
    }

    /**
     * 处理json数据，如果传递进来的key里包含了一个“-”的，那么这属于约定好的传递数组的信号
     */
    private String getJsonData(RequestInfo requestInfo) {
        ArrayMap<String, Object> map = requestInfo.getRequestParams();
        String result = JSON.toJSONString(map);
        if (map.keySet().contains("-")) {
            result = JSON.toJSONString(map.get("-"));
        }
        return result;
    }

    /**
     * put请求
     * 执行加载数据,如果有做数据缓存，先从缓存里面读取数据,
     * 如果缓存数据有效，返回缓存数据，如果缓存失效，重新从
     * 网路请求数据，并将数据缓存到本地SD卡,记录缓存写入时间
     *
     * @param requestInfo 请求体
     * @param callback    数据加载成功后的回调方法
     */
    public void doPut(RequestInfo requestInfo, final Callback callback) {
        if (!isNetworkConnected()) {
            ResponseInfo responseInfo = new ResponseInfo(ResponseInfo.NO_INTERNET_ERROR);
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setUrl(requestInfo.url);
            callback.onError(responseInfo);
            return;
        }

        PutRequest purRequest = OkGo.put(requestInfo.getUrl());

        if (requestInfo.getContentType().equals(RequestInfo.CONTENT_TYPE_JSON)) {
            purRequest.upJson(getJsonData(requestInfo));
        } else  if (requestInfo.getRequestParams().containsKey("=")) {
            purRequest.upString(requestInfo.getRequestParams().get("=").toString());
        }  else {
            purRequest = (PutRequest) setParams(requestInfo.getRequestParams(), purRequest);
        }
        // postRequest.headers("X-Requested-With", "XMLHttpRequest");
//        postRequest.headers("braId",wrap);
        purRequest.headers("terNo", SystemUtils.SN);
        purRequest.headers("Bra-Id", requestInfo.braId);
        purRequest.headers("Mer-Id", requestInfo.merId);
        purRequest.headers("Request-Tk", Base64Utils.base64Encode(requestInfo.getToken()));
        purRequest.headers("Platform", "app");
        purRequest.headers("OperatorCode", requestInfo.operatorCode);
//        purRequest.headers("Connection", "close");
        purRequest.execute(new ResponseCallback(callback, requestInfo));
    }

    /**
     * 上传
     *
     * @param url
     * @param params
     * @param callback
     */
    public void upload(String url, ArrayMap<String, Object> params, FileCallback callback) {
        if (params == null) {
            return;
        }

        PostRequest postRequest = OkGo.post(url);
        postRequest.headers("terNo", SystemUtils.SN);
        postRequest.headers("Platform", "app");

        setParams(params, postRequest).execute(callback);
    }


    /**
     * 设定请求参数
     *
     * @param params 请求参数
     * @param post   请求类型
     * @return
     */
    private BodyRequest setParams(ArrayMap<String, Object> params, BodyRequest post) {
        if (params == null) {
            return post;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof File) {
                post.headers("Content-Type", "multipart/form-data;boundary=" + ((File) entry.getValue()).length());
                LogUtils.d("fileloaded###########");
                post.params(entry.getKey(), (File) entry.getValue());
            } else {
                post.params(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        return post;
    }

    /**
     * 下载
     *
     * @param url
     * @param params
     * @param callback
     */
    public void download(String url, ArrayMap<String, Object> params, FileCallback callback) {
        if (params == null) {
            return;
        }
        setParams(params, OkGo.post(url)).execute(callback);
    }

    /**
     * 下载
     *
     * @param url
     * @param callback
     */
    public void doLoadFile(String url, FileCallback callback) {

        OkGo.<File>get(url).execute(callback);

    }

    /**
     * 发送string 此方法专门针对调起N5第三方支付
     *
     * @param callback
     */
    public void sendString(Callback callback, RequestInfo requestInfo) {
        PostRequest postRequest = OkGo.post(requestInfo.getUrl()).upString(requestInfo.upStr);
        postRequest.headers("X-Requested-With", "XMLHttpRequest");
        postRequest.headers("terNo", Build.SERIAL);
        postRequest.headers("braId", requestInfo.braId);
        postRequest.headers("Platform", "app");
        postRequest.execute(new ResponseCallback(callback, requestInfo));
    }

    /**
     * 检查网络是否已经连接
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) UIUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return mNetworkInfo != null && mNetworkInfo.isConnected();
    }

    /**
     * 取消指定Tag的请求
     *
     * @param view
     */
    public void cancellAll(MvpView view) {
        if (view != null) {
            OkGo.getInstance().cancelTag(view);
        }
    }

    /**
     * 销毁对象
     */
    public void destory() {
        instance = null;
    }

    public void postWithNoHost(Callback callback, RequestInfo requestInfo) {
        //   修改为单独的网络实例
        // 使用单独的网络请求实例，是因为支付系统处理时间较长，所以不能使用重试机制，
        // 因为支付系统正在处理的时候接收到重试的请求，会认为是一个新的请求来处理，
        // 这样就会造成支付系统对一个订单重复处理
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.writeTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient httpClient = builder.build();
        RequestBody bodyBuilder = RequestBody.create(MediaType.parse("application/json"), requestInfo.upStr);
        Request request = new Request.Builder()
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Accept", "application/json")
                .addHeader("content-type", "application/json;charset=UTF-8")
                .addHeader("user-agent", "Mozilla/4.0")
                .addHeader("Platform", "app")
                .addHeader("Request-Tk", Base64Utils.base64Encode(requestInfo.getToken()))
                .url(requestInfo.getUrl())
                .post(bodyBuilder)
                .build();
        final ResponseCallback callback1 = new ResponseCallback(callback, requestInfo);
        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                com.lzy.okgo.model.Response<String> response = new com.lzy.okgo.model.Response<String>();
                response.setFromCache(false);
                response.setRawCall(call);
                response.setRawResponse(null);
                response.setException(e);
                callback1.onError(response);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    callback1.onSuccess(com.lzy.okgo.model.Response.success(false, response.body().string(), call, response));
                } catch (IOException e) {
                    com.lzy.okgo.model.Response<String> erreResponse = new com.lzy.okgo.model.Response<String>();
                    erreResponse.setFromCache(false);
                    erreResponse.setRawCall(call);
                    erreResponse.setRawResponse(null);
                    erreResponse.setException(e);
                    callback1.onError(erreResponse);
                }
            }
        });
    }

}

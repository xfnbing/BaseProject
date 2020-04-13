package com.xxp.baseproject.bases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.easyder.wrapper.ManagerConfig;
import com.easyder.wrapper.base.presenter.Callback;
import com.easyder.wrapper.base.presenter.MvpBasePresenter;
import com.easyder.wrapper.core.manager.DataManager;
import com.easyder.wrapper.core.model.BaseVo;
import com.easyder.wrapper.core.network.NetworkManager;
import com.easyder.wrapper.core.network.RequestInfo;
import com.easyder.wrapper.core.network.ResponseInfo;
import com.easyder.wrapper.utils.Base64;
import com.easyder.wrapper.utils.LogUtils;
import com.easyder.wrapper.utils.RequestParams;
import com.xxp.baseproject.R;

import java.util.Map;
import java.util.Set;


/**
 * Data:    2018/4/25
 * Version: 1.0
 * Desc:
 */

public class PubPresenter extends MvpBasePresenter {

    private String getToken(){
        // 服务器需要的，登录之后的token
        return "";
    }

    @Override
    public void login() {
        super.login();
        if (isViewAttached() && getView() != null) {
            Context context = getView() instanceof Context ? (Context) getView() : ((Fragment) getView()).getContext();
            // todo to login activity
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.finish();
            }
        }
    }

    @Override
    public void onError(ResponseInfo responseInfo) {
        super.onError(responseInfo);
        if (isViewAttached()) {
            switch (responseInfo.getState()) {
                case ResponseInfo.FAILURE:  //服务器访问失败 或者返回状态值不符合约定
                    if (responseInfo.msg != null && responseInfo.msg.length() > 0 && !"-1".equals(responseInfo.msg)) {
                        showToast(ManagerConfig.getInstance().getApplicationContext().getString(R.string.service_error) + responseInfo.msg);
                    } else {
                        showToast(R.string.service_error2);
                    }
                    break;

                case ResponseInfo.LOGIC_ERROR://数据处理异常，解析过程出错
                case ResponseInfo.CACHE_PARSE_ERROR://缓存数据解析错误
                case ResponseInfo.JSON_PARSE_ERROR://Json数据解析错误 或者不是Json数据
                    showToast(R.string.data_isnull);
                    break;
                case ResponseInfo.RESPONSE_FAILURE:   //请求成功状态返回FAILURE
                    if (responseInfo.msg != null) {
                        showToast(responseInfo.msg);
                    } else if (responseInfo.msgData != null) {
                        showToast(responseInfo.msgData);
                    } else {
                        showToast(R.string.data_error);
                    }
                    break;
                case ResponseInfo.TIME_OUT: //请求超时
                    showToast(R.string.net_timeout);
                    break;
                case ResponseInfo.NO_INTERNET_ERROR:    //无网络连接
                    showToast(R.string.net_erro);
                    break;
                case ResponseInfo.SERVER_UNAVAILABLE:   //服务器无法访问
                    if (responseInfo.msg != null && !responseInfo.msg.contains("[")
                            && responseInfo.msg.length() > 0) {
                        showToast(responseInfo.msg);
                    } else {
                        showToast(R.string.api_request_fail);
                    }
                    break;
                case ResponseInfo.UN_LOGIN: //未登录或登录失效
                    if (responseInfo.msg != null && !responseInfo.msg.contains("[")
                            && responseInfo.msg.length() > 0) {
                        showToast(responseInfo.msg);
                    } else {
                        showToast(R.string.login_in_other_place);
                    }
                    login();
                    break;
                case ResponseInfo.UNSET_TYPE:   //未设定客户端
                    setClientType();
                    break;
                default:
                    break;
            }
            LogUtils.e("responseInfo*****" + JSON.toJSONString(responseInfo));
            if (responseInfo.url == null) {
                responseInfo.url = "";
            }
            getView().onError(responseInfo);

        }
    }
    /**
     * Post请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     */
    public void postData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass) {
        postData(url, params, dataClass, false);
    }

    public void postData(String url, ArrayMap<String, Object> params,Map<String, Object> headers, Class<? extends BaseVo> dataClass, boolean isJson) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setToken(getToken());
        requestInfo.setHeader(headers);
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }
        requestInfo.setRequestParams(params);
        NetworkManager.getDefault().doPost(requestInfo, this);//直接访问网络
        requestCount++;
    }


    public void postData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass, boolean isJson) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setToken(getToken());
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }
        requestInfo.setRequestParams(params);
        NetworkManager.getDefault().doPost(requestInfo, this);//直接访问网络
        requestCount++;
    }

    public void postData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass, boolean isJson,Callback callback) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setToken(getToken());
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }
        requestInfo.setRequestParams(params);
        NetworkManager.getDefault().doPost(requestInfo, callback);//直接访问网络
        requestCount++;
    }

    /**
     * Post请求，传递的是json数据
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     */
    public void postJsonData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass) {
        postData(url, params, dataClass, true);
    }

    /**
     * Get请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     */
    public void getData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }

        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_GET);
        requestInfo.setRequestParams(params);
        requestInfo.setToken(getToken());

        NetworkManager.getDefault().doGet(requestInfo, this); //直接访问网络
        requestCount++;
    }

    /**
     * put 请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     * @param isJson    上传的是否是json数据
     */
    public void putData(String url, ArrayMap<String, Object> params,Map<String,Object> headers, Class<? extends BaseVo> dataClass, boolean isJson) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }

        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_PUT);
        requestInfo.setRequestParams(params);
        requestInfo.setToken(getToken());
        if(headers != null) {
            requestInfo.setHeader(headers);
        }
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }

        DataManager.getDefault().loadData(requestInfo, this);
        requestCount++;
    }

    /**
     * del 请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     * @param isJson    上传的是否是json数据
     */
    public void delData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass, boolean isJson) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_DELETE);
        requestInfo.setRequestParams(params);
        requestInfo.setToken(getToken());
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }

        DataManager.getDefault().loadData(requestInfo, this);
        requestCount++;
    }
}

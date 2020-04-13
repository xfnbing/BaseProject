package com.easyder.wrapper.base.presenter;


import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.easyder.wrapper.ManagerConfig;
import com.easyder.wrapper.base.view.MvpView;
import com.easyder.wrapper.base.view.ToastView;
import com.easyder.wrapper.core.manager.DataManager;
import com.easyder.wrapper.core.model.BaseVo;
import com.easyder.wrapper.core.network.NetworkManager;
import com.easyder.wrapper.core.network.RequestInfo;
import com.easyder.wrapper.core.network.ResponseInfo;
import com.easyder.wrapper.utils.Base64;
import com.easyder.wrapper.utils.Base64Utils;
import com.easyder.wrapper.utils.LogUtils;
import com.easyder.wrapper.utils.RequestParams;
import com.easyder.wrapper.utils.UnicodeUtils;
import com.lzy.okgo.callback.FileCallback;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;


/**
 * @author 刘琛慧
 * date 2015/10/27.
 */
public class MvpBasePresenter extends MvpPresenter<MvpView> implements Callback {

    protected boolean needDialog = true;
    public int requestCount;

    /**
     * 请求成功，回调View层方法处理成功的结果
     *
     * @param responseInfo 包含的返回数据的BaseVo子类对象
     */
    @Override
    public void onSuccess(ResponseInfo responseInfo) {
        if (isViewAttached()) {
            requestCount--;
            getView().beforeSuccess();
            try {
                getView().showContentView(responseInfo.url, responseInfo.dataVo);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (requestCount <= 0) {
                    getView().onStopLoading();
                }
            }
        } else {
            LogUtils.e("View已被销毁，onSuccess方法无法回调showContentView方法 ==> " + viewClassName);
        }
    }

    /**
     * 请求失败，回调View层的方法处理错误信息
     *
     * @param responseInfo 包含错误码和错误信息的BaseVo子类对象
     */
    @Override
    public void onError(ResponseInfo responseInfo) {
        if (isViewAttached()) {
            requestCount--;
            getView().onStopLoading();  //todo 访问失败停止了进度条展示

        } else {
            LogUtils.e("MvpView已销毁，onError方法无法回调MvpView层的方法 ==> " + viewClassName);
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        //取消默认的还未完成的请求
        DataManager.getDefault().onViewDetach(getView());
    }


    /**
     * Get请求
     *
     * @param url
     * @param params 参数
     */
    public void getData(String url, ArrayMap<String, Object> params) {
        getData(url, params, null);
    }

    /**
     * Get请求
     *
     * @param url
     * @param dataClass
     */
    public void getData(String url, Class<? extends BaseVo> dataClass) {
        getData(url, null, dataClass);
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
        //requestInfo.setNeedMockData(true);
        DataManager.getDefault().loadData(requestInfo, this);
        requestCount++;
    }


    /**
     * Post请求
     *
     * @param url
     * @param params 参数
     */
    public void postData(String url, ArrayMap<String, Object> params) {
        postData(url, params, null);
    }

    /**
     * Post请求
     *
     * @param url
     * @param dataClass 对象类型
     */
    public void postData(String url, Class<? extends BaseVo> dataClass) {
        postData(url, null, dataClass);
    }

    /**
     * Post请求
     *
     * @param url
     * @param params    参数
     * @param dataClass 对象类型
     */
    public void postData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setRequestParams(params);

        DataManager.getDefault().loadData(requestInfo, this);
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
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setRequestParams(params);
        requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);

        DataManager.getDefault().loadData(requestInfo, this);
        requestCount++;
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @param dataClass
     */
    public void postJsonData(String url, ArrayMap<String, Object> params,Map<String, Object> headers, Class<? extends BaseVo> dataClass) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_POST);
        requestInfo.setRequestParams(params);
        requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        requestInfo.setHeader(headers);

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
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }

        DataManager.getDefault().loadData(requestInfo, this);
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
    public void putData(String url, ArrayMap<String, Object> params, Class<? extends BaseVo> dataClass, boolean isJson) {
        if (isViewAttached() && needDialog && requestCount >= 0) {
            getView().onLoading();
        }
        RequestInfo requestInfo = new RequestInfo(url, dataClass);
        requestInfo.setRequestType(RequestInfo.REQUEST_PUT);
        requestInfo.setRequestParams(params);
        if (isJson) {
            requestInfo.setContentType(RequestInfo.CONTENT_TYPE_JSON);
        }

        DataManager.getDefault().loadData(requestInfo, this);
        requestCount++;
    }

    /**
     * Post请求
     *
     * @param url
     */
    public void loadFileData(String url, FileCallback fileCallback) {

        NetworkManager.getDefault().doLoadFile(url, fileCallback);
    }




    /**
     * @param url
     * @param map
     * @param dataClass
     */
    public void upStringData(String url, Map<String, Object> map, Class<? extends BaseVo> dataClass) {

    }



    /**
     * 判断请求时是否需要Dialog
     *
     * @return 默认true
     */
    public boolean isNeedDialog() {
        return needDialog;
    }

    /**
     * 设定请求时是否需要加载进度条   需要在请求前设定
     *
     * @param needDialog 默认true
     */
    public void setNeedDialog(boolean needDialog) {
        this.needDialog = needDialog;
    }

    /**
     * 设定客户端类型
     */
    protected void setClientType() {

    }

    public int getRequestCount() {
        return requestCount;
    }

    /**
     * 登录
     */
    public void login() {

    }

    /**
     * 提示
     *
     * @param msg
     */
    protected void showToast(String msg) {
        ToastView.showToastInCenter(ManagerConfig.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }
    protected void showToast(int stringId) {
        ToastView.showToastInCenter(ManagerConfig.getInstance().getApplicationContext(), stringId, Toast.LENGTH_SHORT);
    }
}

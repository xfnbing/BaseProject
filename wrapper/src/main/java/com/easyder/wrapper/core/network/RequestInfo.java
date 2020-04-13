package com.easyder.wrapper.core.network;

import android.support.v4.util.ArrayMap;

import com.easyder.wrapper.ManagerConfig;
import com.easyder.wrapper.core.model.BaseVo;
import com.easyder.wrapper.core.model.Entity;

import java.util.Map;


/**
 * 请求体
 */
public class RequestInfo {
    public String host = ManagerConfig.getInstance().getBaseHost();
    private static String TOKEN = "";

    public static final int REQUEST_GET = 0x1; //查询数据请求
    public static final int REQUEST_POST = 0x2; //新增和修改数据请求
    public static final int REQUEST_DELETE = 0x3; //删除请求
    public static final int REQUEST_PUT = 0x4; //删除请求
    public static final int PAGE_SIZE = 10; //默认数据分页数据条数
    public static final String CONTENT_TYPE_JSON = "application/json"; //
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded"; //
    public String upStr; //直接传string
    public String braId;
    public String merId;
    public String operatorCode;
    public String payToken;//统一支付的token
    private int requestType; //请求的类型
    private int actionTye; //请求接口要做的操作类型。
    public long dataExpireTime; //数据缓存时间默认为0即不缓存数据
    public ArrayMap<String, Object> requestParams; //请求参数

    public Map<String,Object> headers;//headers


    public boolean needShowDialog = true; //是否需要显示加载对话框
    public boolean needMockData = false; //是否需要模拟数据，此标识仅用于接口已定义，但未实现的前提下才能设定为true
    public String url;
    public String api; //请求的api
    public Class<? extends BaseVo> dataClass; //请求结果Vo的class
    private Class<? extends Entity> entityClass; //请求结果entity的class
    private String contentType = CONTENT_TYPE_FORM;
    private String token;

    public RequestInfo(String api, Class<? extends BaseVo> dataClass) {
        this.api = api;
        this.dataClass = dataClass;
        if(api.startsWith("http")){
            this.url = api;
        }else{
            this.url = host + api;
        }
    }

    public RequestInfo(String api, String upStr, Class<? extends BaseVo> dataClass) {
        this.api = api;
        this.dataClass = dataClass;
        this.url = api;
        this.upStr = upStr;
    }

    public RequestInfo(String url, long dataExpireTime, Class<? extends BaseVo> dataClass) {
        this(url, dataClass);
        this.dataExpireTime = dataExpireTime;
    }

    public Class<? extends BaseVo> getDataClass() {
        return dataClass;
    }

    public void setDataClass(Class<? extends BaseVo> dataClass) {
        this.dataClass = dataClass;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public int getActionTye() {
        return actionTye;
    }

    public void setActionTye(int actionTye) {
        this.actionTye = actionTye;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public long getDataExpireTime() {
        return dataExpireTime;
    }

    public void setDataExpireTime(long dataExpireTime) {
        this.dataExpireTime = dataExpireTime;
    }

    public ArrayMap<String, Object> getRequestParams() {
        return requestParams;
    }

    public ArrayMap<String, Object> put(String key, Object value) {
        requestParams.put(key, value);
        return requestParams;
    }

    public void setRequestParams(ArrayMap<String, Object> requestParams) {
        this.requestParams = requestParams;
    }



    public Map<String,Object> getHeades(){return headers;}

    public Map<String, Object> putHeader(String key, Object value) {
        headers.put(key, value);
        return headers;
    }
    public void setHeader(Map<String, Object> headers) {
        this.headers = headers;
    }

    public boolean isNeedShowDialog() {
        return needShowDialog;
    }

    public void setNeedShowDialog(boolean needShowDialog) {
        this.needShowDialog = needShowDialog;
    }

    public boolean isNeedMockData() {
        return needMockData;
    }

    public void setNeedMockData(boolean needMockData) {
        this.needMockData = needMockData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getToken() {
        return token != null ? token : TOKEN;
    }

    public void setToken(String token) {
        this.token = token;
        if (token != null && token.length() > 0) {
            TOKEN = token;
        }
    }
}

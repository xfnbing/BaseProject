package com.easyder.wrapper.core.network;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyder.wrapper.base.presenter.Callback;
import com.easyder.wrapper.core.manager.CacheManager;
import com.easyder.wrapper.core.manager.DataManager;
import com.easyder.wrapper.core.model.BaseVo;
import com.easyder.wrapper.core.model.StringVo;
import com.easyder.wrapper.utils.LogUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Auther:  winds
 * Data:    2017/7/1
 * Desc:    响应回调
 */

public class ResponseCallback extends StringCallback {
    public Callback callback;
    public String filePath;
    public RequestInfo requestInfo;

    public ResponseCallback(Callback callback, RequestInfo requestInfo) {
        this.callback = callback;
        this.requestInfo = requestInfo;
    }

    public ResponseCallback(Callback callback, RequestInfo requestInfo, String filePath) {
        this.callback = callback;
        this.requestInfo = requestInfo;
        this.filePath = filePath;
    }

    @Override
    public void onSuccess(Response<String> response) {
        onSuccess(response.body(), response.getRawCall(), response.getRawResponse());
    }

    @Override
    public void onError(Response<String> response) {
        super.onError(response);
        LogUtils.e("content::::::::" + JSON.toJSONString(response));
        ResponseInfo responseInfo;
        Throwable error = response.getException();
        if (error instanceof SocketTimeoutException) {
            responseInfo = new ResponseInfo(ResponseInfo.TIME_OUT);
        } else {
            // 如果返回的错误信息为json信息，根据json信息里的数据进行错误信息提取
            if (response.message() != null && response.message().contains("{") && response.message().contains("}")) {
                JSONObject jsonObject = JSON.parseObject(response.message());
                responseInfo = new ResponseInfo(parseResultStatus(jsonObject.getString("rspCod")), jsonObject.getString("rspMsg"));
                responseInfo.setUrl(requestInfo.getUrl());
                String data = jsonObject.getString("data");
                // 400 ,500錯誤的時候，如果data不是空，那麽讀取data信息作爲報錯信息
                if (responseInfo.getState() == ResponseInfo.RESPONSE_FAILURE && data != null) {
                    responseInfo.setMsg(data);
                } else if (responseInfo.getState() == ResponseInfo.FAILURE) {
                    responseInfo.setMsg(error.getMessage() + "");
                }
            } else {
                responseInfo = new ResponseInfo(ResponseInfo.FAILURE);
                responseInfo.setMsg(error.getMessage() + "");
                responseInfo.setErrorObject(error);
            }
        }
        responseInfo.requestParams = requestInfo.requestParams;
        responseInfo.setUrl(requestInfo.getUrl());
        DataManager.getDefault().postCallback(callback, responseInfo);
    }


    public void onSuccess(String content, Call call, okhttp3.Response response) {
        ResponseInfo responseInfo = null;
        if (response.isSuccessful()) {
            String type = "json";
            if (response.body() != null) {
                MediaType mediaType = response.body().contentType();
                if (mediaType != null) {
                    type = mediaType.subtype();
                }
            }

            if (type.equals("json")) {
                if (requestInfo.getUrl().contains("td-uas-web")) {//区分是零售的接口和统一支付的接口
                    dispatchHtmlResult(content, type);
                } else {
                    dispatchJsonResult(content, type);
                }
            } else if (type.equals("html")) {//统一支付的返回类型
                dispatchHtmlResult(content, type);
            } else {
                LogUtils.e("content::::::::" + content);
                responseInfo = new ResponseInfo(ResponseInfo.JSON_PARSE_ERROR);
                responseInfo.requestParams = requestInfo.requestParams;
                responseInfo.setResponseType(type);
                responseInfo.setUrl(requestInfo.getUrl());
                responseInfo.setMsg("无法解析请求结果");
                try {
                    responseInfo.setRawData(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DataManager.getDefault().postCallback(callback, responseInfo);
            }
        } else {
            responseInfo = new ResponseInfo(ResponseInfo.SERVER_UNAVAILABLE);
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setUrl(requestInfo.getUrl());
            responseInfo.setMsg(response.message());
            DataManager.getDefault().postCallback(callback, responseInfo);
        }
    }

    private void dispatchHtmlResult(String response, String type) {
        ResponseInfo responseInfo;
        try {
            JSONObject jsonObject = JSON.parseObject(response);
//            if (jsonObject.containsKey("FJ_PATH")) {
//                // 进件图片上传，特殊接口，需要进行特殊处理
//                responseInfo = new ResponseInfo(200, "");
//                responseInfo.setResponseType(type);
//                responseInfo.setUrl(requestInfo.getUrl());
//                StringVo stringVo = new StringVo();
//                stringVo.rspCod = "200";
//                stringVo.rspData = response;
//                stringVo.requestParams = requestInfo.requestParams;
//                responseInfo.setDataVo(stringVo);
//                DataManager.getDefault().postCallback(callback, responseInfo);
//                return;
//            } else {
                responseInfo = new ResponseInfo(parseResultStatus(jsonObject.getString("rspCod")), jsonObject.getString("rspMsg"));
//            }
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setResponseType(type);
            responseInfo.setUrl(requestInfo.getUrl());
            String data = jsonObject.getString("rspData");
            if (responseInfo.getState() != ResponseInfo.SUCCESS) {
                DataManager.getDefault().postCallback(callback, responseInfo);
                return;
            }
            BaseVo baseVo = BaseVo.parseDataVo(response, requestInfo.getDataClass());
            if(requestInfo.getDataClass() == StringVo.class){
                LogUtils.d("case to StringVo");
                StringVo sv = (StringVo) baseVo;
                if(sv != null && jsonObject.containsKey("rspData")) {
                    sv.rspData = jsonObject.getString("rspData");
                }else if(sv != null && jsonObject.containsKey("rspList")) {
                    sv.rspData = jsonObject.getString("rspList");
                }else if(sv != null && jsonObject.containsKey("data")) {
                    sv.rspData = jsonObject.getString("data");
                }
            }
            if (baseVo != null) {
                baseVo.requestParams = requestInfo.requestParams;
            }
            responseInfo.setDataVo(baseVo);
            DataManager.getDefault().postCallback(callback, responseInfo);
            //缓存数据
            if (requestInfo.getDataExpireTime() > 0 && !TextUtils.isEmpty(data)) {
                String key = CacheManager.getDefault().sortUrl(requestInfo.getUrl(), requestInfo.getRequestParams());
                CacheManager.getDefault().writeToCache(key, data);
            }
        } catch (JSONException ex) {
            responseInfo = new ResponseInfo(ResponseInfo.JSON_PARSE_ERROR);
            LogUtils.e(ex);
            responseInfo.setMsg(ex.getMessage());
            DataManager.getDefault().postCallback(callback, responseInfo);
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.e("数据处理异常，原始数据：" + response);

            responseInfo = new ResponseInfo(ResponseInfo.LOGIC_ERROR);
            responseInfo.setResponseType(type);
            responseInfo.setMsg("数据处理异常，原始数据：" + response);
            DataManager.getDefault().postCallback(callback, responseInfo);
        }
    }


    private void dispatchJsonResult(String response, String type) {
        ResponseInfo responseInfo;
        try {
            JSONObject jsonObject = JSON.parseObject(response);

            if (requestInfo.getUrl().contains("service/pay")) { //有点难看...
                responseInfo = new ResponseInfo(200, jsonObject.getString("message"));
            } else if (jsonObject.containsKey("rspCod")) {
                responseInfo = new ResponseInfo(parseResultStatus(jsonObject.getString("rspCod")), jsonObject.getString("rspMsg"));
            }
//            else if (jsonObject.containsKey("FJ_PATH")) {
//                // 进件图片上传，特殊接口，需要进行特殊处理
//                responseInfo = new ResponseInfo(200, "");
//                responseInfo.setResponseType(type);
//                responseInfo.setUrl(requestInfo.getUrl());
//                StringVo stringVo = new StringVo();
//                stringVo.rspCod = "200";
//                stringVo.rspData = response;
//                stringVo.requestParams = requestInfo.requestParams;
//                responseInfo.setDataVo(stringVo);
//                DataManager.getDefault().postCallback(callback, responseInfo);
//                return;
//            }
            else {
                responseInfo = new ResponseInfo(parseResultStatus(jsonObject.getString("code")), jsonObject.getString("message"));
            }
            responseInfo.requestParams = requestInfo.requestParams;
            responseInfo.setResponseType(type);
            responseInfo.setUrl(requestInfo.getUrl());
            String data = jsonObject.getString("data");
            if (responseInfo.getState() != ResponseInfo.SUCCESS) {
                // 400 ,500錯誤的時候，如果data不是空，那麽讀取data信息作爲報錯信息

                LogUtils.d("content::::::::" + response);
                if (responseInfo.getState() == ResponseInfo.RESPONSE_FAILURE && data != null) {
                    responseInfo.msgData = responseInfo.getMsg();
                    responseInfo.setMsg(data);
                } else {
                    responseInfo.msgData = data;
                }
                DataManager.getDefault().postCallback(callback, responseInfo);
                return;
            }

            BaseVo baseVo = BaseVo.parseDataVo(response, requestInfo.getDataClass());
            if(requestInfo.getDataClass() == StringVo.class){
                LogUtils.d("case to StringVo");
                StringVo sv = (StringVo) baseVo;
                if(sv != null && jsonObject.containsKey("rspData")) {
                    sv.rspData = jsonObject.getString("rspData");
                }else if(sv != null && jsonObject.containsKey("rspList")) {
                    sv.rspData = jsonObject.getString("rspList");
                }else if(sv != null && jsonObject.containsKey("data")) {
                    sv.rspData = jsonObject.getString("data");
                }
            }
            //需要后台配合,即使没有数据也要返回字段
//            if (responseInfo.getState() != ResponseInfo.SUCCESS && baseVo == null) {
//                responseInfo.setState(ResponseInfo.JSON_PARSE_ERROR);
//                responseInfo.setMsg("请求结果数据解析失败！");
//            }
            if (baseVo != null) {
                baseVo.requestParams = requestInfo.requestParams;
            }
            responseInfo.setDataVo(baseVo);
            DataManager.getDefault().postCallback(callback, responseInfo);
            //缓存数据
            if (requestInfo.getDataExpireTime() > 0 && !TextUtils.isEmpty(data)) {
                String key = CacheManager.getDefault().sortUrl(requestInfo.getUrl(), requestInfo.getRequestParams());
                CacheManager.getDefault().writeToCache(key, data);
            }

        } catch (JSONException ex) {
            responseInfo = new ResponseInfo(ResponseInfo.JSON_PARSE_ERROR);
            LogUtils.e(ex);
            responseInfo.setMsg(ex.getMessage());
            DataManager.getDefault().postCallback(callback, responseInfo);
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.e("数据处理异常，原始数据：" + response);
            responseInfo = new ResponseInfo(ResponseInfo.LOGIC_ERROR);
            responseInfo.setResponseType(type);
            DataManager.getDefault().postCallback(callback, responseInfo);
        }
    }


    public int parseResultStatus(String status) {
        if ("success".equals(status) || "200".equals(status) || "succ".equals(status)) {
            return ResponseInfo.SUCCESS;
        } else if ("failure".equals(status)) {
            return ResponseInfo.RESPONSE_FAILURE;
        } else if ("407".equals(status) || "_SSO_ERR".equals(status)) {
            return ResponseInfo.UN_LOGIN;
        } else if ("400".equals(status) || "500".equals(status)) {
            return ResponseInfo.RESPONSE_FAILURE;
        } else if ("999".equals(status)) {
            return ResponseInfo.RESPONSE_FAILURE;
        }
        return ResponseInfo.FAILURE;
    }
}

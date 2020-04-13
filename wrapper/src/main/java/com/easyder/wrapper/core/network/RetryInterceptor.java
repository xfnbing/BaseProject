package com.easyder.wrapper.core.network;

import android.support.annotation.NonNull;

import com.easyder.wrapper.utils.LogUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private static final String TAG = "RetryInterceptor";

    private int maxRetry = 5;//最大重试次数

    //    间隔时间
    private long increaseDelay = 200;


    public RetryInterceptor() {
    }

    public RetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public RetryInterceptor(int maxRetry, long increaseDelay) {
        this.maxRetry = maxRetry;
        this.increaseDelay = increaseDelay;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        RetryWrapper retryWrapper = proceed(chain);

        while (retryWrapper.isNeedReTry()) {
            retryWrapper.retryNum++;
            LogUtils.e(TAG, "url::" + retryWrapper.request.url().toString() + " retry Num::" + retryWrapper.retryNum);
            try {
                Thread.sleep(increaseDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            proceed(chain, retryWrapper.request, retryWrapper);
        }
        return retryWrapper.response() == null ? chain.proceed(chain.request()) : retryWrapper.response();
    }

    private RetryWrapper proceed(Chain chain) throws IOException {
        Request request = chain.request();
        RetryWrapper retryWrapper = new RetryWrapper(request, maxRetry);

        proceed(chain, request, retryWrapper);

        return retryWrapper;
    }

    private void proceed(Chain chain, Request request, RetryWrapper retryWrapper)  {
        try {
            Response response = chain.proceed(request);
            retryWrapper.setResponse(response);
        } catch (Exception e) {
            //e.printStackTrace();
            LogUtils.e(TAG, e.getLocalizedMessage());
        }
    }

    static class RetryWrapper {
        volatile int retryNum = 0;//假如设置为3次重试的话，则最大可能请求5次（默认1次+3次重试 + 最后一次默认）
        Request request;
        Response response;
        private int maxRetry;

        public RetryWrapper(Request request, int maxRetry) {
            this.request = request;
            this.maxRetry = maxRetry;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        Response response() {
            return this.response;
        }

        Request request() {
            return this.request;
        }

        public boolean isSuccessful() {
            return response != null && response.isSuccessful();
        }

        public boolean isNeedReTry() {
            return !isSuccessful() && retryNum < maxRetry;
        }

        public void setRetryNum(int retryNum) {
            this.retryNum = retryNum;
        }

        public void setMaxRetry(int maxRetry) {
            this.maxRetry = maxRetry;
        }
    }
}
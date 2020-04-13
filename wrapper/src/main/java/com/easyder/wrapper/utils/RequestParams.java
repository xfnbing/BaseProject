package com.easyder.wrapper.utils;

import android.support.v4.util.ArrayMap;

import java.io.Serializable;

/**
 * Auther:  winds
 * Data:    2017/4/19
 * Desc:
 */

public class RequestParams {
    ArrayMap<String, Object> params;

    public RequestParams() {
        this.params = new ArrayMap<>();
    }

    public RequestParams put(String key, Object value) {
        params.put(key, value);
        return this;
    }
    public RequestParams remove(String key){
        params.remove(key);
        return this;
    }
    /**
     * 去除null 空字符串 -1 加入集合
     *
     * @param key
     * @param value
     * @return
     */
    public RequestParams putNonNull(String key, Serializable value) {
        if (value != null) {
            if (value instanceof String) {
                if ((((String) value).trim().length() == 0)) {
                    return this;
                }
            }

            if (value instanceof Integer) {
                if (((Integer) value) == -1) {
                    return this;
                }
            }
            params.put(key, value);
        }
        return this;
    }

    public Object get(String key) {
        return params.get(key);
    }

    public ArrayMap<String, Object> get() {
        LogUtils.info("--> " + params.toString());
        return params;
    }
}

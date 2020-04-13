package com.easyder.wrapper.utils;


/**
 * 作者：Arrom
 * 日期： 2018/9/4
 * 描述：
 */
public class Base64Utils {

    /**
     * base64加密
     *
     * @param str
     * @param charset
     * @return
     */
    private static String base64Encode(String str, String charset) {
        try {
            return new String(Base64.encodeBase64(str.getBytes(charset)), charset);
        } catch (Exception e) {
            throw new RuntimeException("base64 encode error", e);
        }
    }

    public static String base64Encode(String str) {
        return base64Encode(str, "UTF-8");
    }

}

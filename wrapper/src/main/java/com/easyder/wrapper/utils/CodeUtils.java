package com.easyder.wrapper.utils;

import java.nio.charset.Charset;

/**
 * Created by Lookey on 2018/9/17.
 * description:
 */

public class CodeUtils {
    /**
     * 将str字符串转换成bcd编码的字节数组，比如字符串"123456"转成byte[] {0x12, 0x34, 0x56}
     * @param asc
     * @return
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        len /= 2;

        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes(Charset.forName("utf-8"));

        for (int p = 0; p < len; ++p) {
            int j; //字节高4位
            int k; //字节低4位

            if ((abt[(2 * p)] >= 97) && (abt[(2 * p)] <= 122)) {
                //字符a-z
                j = abt[(2 * p)] - 97 + 10;
            }
            else if ((abt[(2 * p)] >= 65) && (abt[(2 * p)] <= 90)) {
                //字符A-Z
                j = abt[(2 * p)] - 65 + 10;
            }
            else {
                //数字0-9
                j = abt[(2 * p)] - 48;
            }

            if ((abt[(2 * p + 1)] >= 97) && (abt[(2 * p + 1)] <= 122)) {
                k = abt[(2 * p + 1)] - 97 + 10;
            }
            else if ((abt[(2 * p + 1)] >= 65) && (abt[(2 * p + 1)] <= 90)) {
                k = abt[(2 * p + 1)] - 65 + 10;
            }
            else {
                k = abt[(2 * p + 1)] - 48;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 将bcd编码的字节数组转换成str字符串，比如byte[] {0x12, 0x34, 0x56}转成字符串"123456"
     * @param bytes
     * @return
     */
    public static String bcd2Str(byte[] bytes) {
        //str字符串的长度为原来的2倍
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            //得到高4位二进制转换后的十进制值
            byte left = (byte) ((bytes[i] & 0xF0) >>> 4);
            //得到低4位二进制转换后的十进制值
            byte right = (byte) (bytes[i] & 0x0F);
            //根据ASCII码表转成str表示的数字
            temp.append(String.format("%c",
                    new Object[]{Integer.valueOf(left + 48)}));
            temp.append(String.format("%c",
                    new Object[]{Integer.valueOf(right + 48)}));
        }
        return temp.toString();
    }
    /**
     * byte数组转为16进制的str
     * iso8583传输的数据是先由人看得懂的str转成byte数组传给后台的
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}

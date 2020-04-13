package com.easyder.wrapper.utils;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by Lookey on 2018/9/14.
 * description:
 */

public class Iso8583Util {


    private static Map map8583Definition = null;// 8583报文64域定义器
    public static String packet_encoding = "UTF-8";//报文编码 UTF-8 GBK

    public static void init(Context context) {
        if (map8583Definition == null) {
            Properties properties = new Properties();
            try {
                properties.load(context.getAssets().open("config_64iso8583.properties"));
                map8583Definition = new HashMap(properties);
                LogUtils.d("hello:" + map8583Definition.get("FIELD003"));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 8583报文初始位图:64位01字符串
    public static String getInitBitMap() {
        String initBitMap = "00000000" + "00000000" + "00000000" + "00000000" +
                "00000000" + "00000000" + "00000000" + "00000000";
        return initBitMap;
    }

//    /**
//     * @param args
//     */
//    public static void main(String[] args) {
//        try {
//            //***********************组装8583报文测试--start***********************//
//            TreeMap filedMap=new TreeMap();//报文域
//            filedMap.put("FIELD003", "1799");//交易码
//            filedMap.put("FIELD013", "2013-11-06");//交易日期
//            filedMap.put("FIELD008", "12345678901");//账号
//            filedMap.put("FIELD033", "aa索隆bb");//注意这个域是变长域!
//            filedMap.put("FIELD036", "123456");//注意这个域是变长域!
//
//            byte[] send=make8583(filedMap);
//            System.out.println("完成组装8583报文=="+new String(send,packet_encoding)+"==");
//            //***********************组装8583报文测试--end***********************//
//
//
//            //***********************解析8583报文测试--start***********************//
//            Map back=analyze8583(send);
//            System.out.println("完成解析8583报文=="+back.toString()+"==");
//            //***********************解析8583报文测试--end***********************//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * 组装8583报文
     *
     *  总长度(2Bytes)+TPDU(5Bytes)+MTI(2Bytes)+64BitMap(8Bytes)+Data(数据)
     * @param
     * @return
     */
    public static byte[] make8583(TreeMap filedMap,byte[] mti) {
        byte[] tpdu=CodeUtils.str2Bcd("6002970000");//TPDU
        byte[] whoe8583 = null;

        try {
            //bitMap+data
            byte[] bitmapAndValue = formatValueTo8583(filedMap, getInitBitMap());

            //组装  总长度(2Bytes)+TPDU(5Bytes)+MTI(2Bytes)+64BitMap(8Bytes)+Data(数据)
            byte[] package8583 = null;
            package8583 = arrayApend(tpdu, mti);
            package8583 = arrayApend(package8583, bitmapAndValue);
            //计算总长度
            int totalLength=package8583.length;
            String tLength=String.valueOf(totalLength);
            String finallengthStr = strCopy("0", (4 - tLength.length())) + tLength;//2
            byte[] totalLengthBy=CodeUtils.str2Bcd(finallengthStr);
            package8583 = arrayApend(totalLengthBy, package8583);

            return package8583;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到位图+域值的byte合集
     *
     * @param filedMap
     * @param bitMap64
     * @return
     */
    public static byte[] formatValueTo8583(TreeMap filedMap, String bitMap64) {
        byte[] fieldByte1 = null;
        Iterator it = filedMap.keySet().iterator();
        for (; it.hasNext(); ) {
            String fieldName = (String) it.next();//例如FIELD005
            String fieldValue = (String) filedMap.get(fieldName);//字段值"121212"
            byte[] fieldValueArray = null;
            try {
                //将域值编码转换，保证报文编码统一
                fieldValue = new String(fieldValue.getBytes(packet_encoding), packet_encoding);
                // 数据域名称FIELD开头的为64域
                String fieldNo = fieldName.substring(5, 8);//例如005
                // 组二进制位图串
                bitMap64 = change16bitMapFlag(fieldNo, bitMap64);
                // 获取域定义信息
                String[] fieldDef = map8583Definition.get("FIELD" + fieldNo).toString().split(",");
                String defType = fieldDef[0];//类型定义,例bcd
                String defLen = fieldDef[1];//长度定义,变长表示的是长度的byte长度
                boolean isFixLen = true;//是否定长判断
                if (defLen.startsWith("VAR")) {//变长域
                    isFixLen = false;
                    defLen = defLen.substring(3);//获取VAR2后面的2
                }
                // 判断是否为变长域
                if (!isFixLen) {
                    // 变长域(变长域最后组装成的效果：例如变长4位，定义var4，这里的4是指长度str值占4位(byte[]则用2位表示)，字段值是123456，最后结果就是0006123456)
                    int defLen1 = Integer.valueOf(defLen);
                    //将长度值组装入字段
                    fieldValueArray = getVaryStrValue(fieldValue, defLen1, defType);//变长处理
                } else {//定长域
                    fieldValueArray = getFixFieldValue(fieldValue, defType);//定长处理
                }
                fieldByte1 = arrayApend(fieldByte1, fieldValueArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end for

        byte[] bitmaps64 = get16BitByteFromStr(bitMap64);//64位位图byte[]
        //最终,位图和域值装到了一起
        return arrayApend(bitmaps64, fieldByte1);
    }

    /**
     * 改变64位图中的标志为1
     *
     * @param fieldNo
     * @param res
     * @return
     */
    public static String change16bitMapFlag(String fieldNo, String res) {
        int indexNo = Integer.parseInt(fieldNo);
        res = res.substring(0, indexNo - 1) + "1" + res.substring(indexNo);
        LogUtils.d("indexNo:" + indexNo + "***" + res);
        return res;
    }

    /**
     * 解析8583报文
     *
     * @param content8583
     */
    public static Map analyze8583(byte[] content8583) {
        TreeMap filedMap = new TreeMap();
        try {
            // 取位图
            byte[] bitMap16byte = new byte[16];
            System.arraycopy(content8583, 0, bitMap16byte, 0, 16);
            // 16位图转2进制位图64位字符串
            String bitMap64Str = get16BitMapStr(bitMap16byte);

            //记录当前位置,从位图后开始遍历取值
            int pos = 16;
            // 遍历64位图，取值。注意从FIELD002开始
            for (int i = 1; i < bitMap64Str.length(); i++) {
                String filedValue = "";//字段值
                String filedName = "FIELD" + getNumThree((i + 1));//FIELD005

                if (bitMap64Str.charAt(i) == '1') {
                    // 获取域定义信息
                    String[] fieldDef = map8583Definition.get(filedName).toString().split(",");
                    String defType = fieldDef[0];//类型定义例string
                    String defLen = fieldDef[1];//长度定义,例20
                    boolean isFixLen = true;//是否定长判断

                    if (defLen.startsWith("VAR")) {//变长域
                        isFixLen = false;
                        defLen = defLen.substring(3);//获取VAR2后面的2
                    }
                    // 截取该域信息
                    if (!isFixLen) {//变长域
                        int defLen1 = Integer.valueOf(defLen);//VAR2后面的2
                        String realLen1 = new String(content8583, pos, defLen1, packet_encoding);//报文中实际记录域长,例如16,023
                        int realAllLen = defLen1 + Integer.valueOf(realLen1);//该字段总长度（包括长度值占的长度）
//						filedValue = new String(content8583, pos+defLen1, Integer.valueOf(realLen1), packet_encoding);
                        byte[] filedValueByte = new byte[Integer.valueOf(realLen1)];
                        System.arraycopy(content8583, pos + defLen1, filedValueByte, 0, filedValueByte.length);
                        filedValue = new String(filedValueByte, packet_encoding);
                        pos += realAllLen;//记录当前位置
                    } else {//定长域
                        int defLen2 = Integer.valueOf(defLen);//长度值占的位数
                        filedValue = new String(content8583, pos, defLen2, packet_encoding);
                        pos += defLen2;//记录当前位置
                    }
                    filedMap.put(filedName, filedValue);
                }
            }//end for
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filedMap;
    }

    //********************************以下是工具方法,有些没有使用到***********************************************************//

    /**
     * 复制字符
     *
     * @param str
     * @param count
     * @return
     */
    public static String strCopy(String str, int count) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 将setContent放入set（考虑到数组越界）
     *
     * @param set
     * @param setContent
     * @return
     */
    public static byte[] setToByte(byte[] set, byte[] setContent) {
        byte[] res = new byte[set.length];
        if (set == null || setContent == null) {

        } else {
            if (set.length < setContent.length) {

            } else {
                System.arraycopy(setContent, 0, res, 0, setContent.length);
            }
        }
        return res;
    }

    public static byte[] setToByte(byte[] set, String setContentStr) {
        byte[] res = new byte[set.length];
        byte[] setContent;
        try {
            setContent = setContentStr.getBytes(packet_encoding);
            res = setToByte(res, setContent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getPacketLen(int len) {
        String res = "";
        String lenStr = String.valueOf(len);
        int lenC = 4 - lenStr.length();
        res = strCopy("0", lenC) + lenStr;
        return res;
    }

    public static String getPacketLen(String lenStr) {
        String res = "";
        if (lenStr == null) {

        } else {
            res = getPacketLen(Integer.valueOf(lenStr));
        }
        return res;
    }


    /**
     * 返回a和b的组合,实现累加功能
     *
     * @param a
     * @param b
     * @return
     */
    public static byte[] arrayApend(byte[] a, byte[] b) {
        int a_len = (a == null ? 0 : a.length);
        int b_len = (b == null ? 0 : b.length);
        byte[] c = new byte[a_len + b_len];
        if (a_len == 0 && b_len == 0) {
            return null;
        } else if (a_len == 0) {
            System.arraycopy(b, 0, c, 0, b.length);
        } else if (b_len == 0) {
            System.arraycopy(a, 0, c, 0, a.length);
        } else {
            System.arraycopy(a, 0, c, 0, a.length);
            System.arraycopy(b, 0, c, a.length, b.length);
        }
        return c;
    }


    /**
     * 位图操作
     * <p>
     * 把16位图的字节数组转化成64位01字符串
     *
     * @param
     * @return
     */
    public static String get16BitMapStr(byte[] bitMap16) {
        String bitMap64 = "";
        // 16位图转2进制位图64位字符串
        for (int i = 0; i < bitMap16.length; i++) {
            int bc = bitMap16[i];
            bc = (bc < 0) ? (bc + 256) : bc;
            String bitnaryStr = Integer.toBinaryString(bc);//二进制字符串
            // 左补零，保证是8位
            String rightBitnaryStr = strCopy("0", Math.abs(8 - bitnaryStr.length())) + bitnaryStr;//位图二进制字符串
            // 先去除多余的零，然后组装64域二进制字符串
            bitMap64 += rightBitnaryStr;
        }
        return bitMap64;
    }

    /**
     * 位图操作
     * <p>
     * 把64位01字符串转化成16位图的字节数组
     *
     * @param
     * @return
     */
    public static byte[] get16BitByteFromStr(String str_64) {
        LogUtils.d("str64:" + str_64);
        byte[] bit16 = new byte[8];
        try {
            if (str_64 == null || str_64.length() != 64) {
                return null;
            }
            // 64域位图二进制字符串转8位16进制
            byte[] tmp = str_64.getBytes(packet_encoding);
            int weight;//权重
            byte[] strout = new byte[64];
            int i, j, w = 0;
            for (i = 0; i < 8; i++) {
                weight = 0x0080;
                for (j = 0; j < 8; j++) {
                    strout[i] += ((tmp[w]) - '0') * weight;
                    weight /= 2;
                    w++;
                }
                bit16[i] = strout[i];

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bit16;
    }


    /**
     * 从完整的8583报文中获取位图（16字节数组）
     *
     * @param packet
     * @return
     */
    public static byte[] getPacketHeaderMap(byte[] packet) {
        byte[] packet_header_map = new byte[16];
        if (packet == null || packet.length < 16) {
            return null;
        }
        for (int i = 0; i < 16; i++) {
            packet_header_map[i] = packet[i];
        }
        return packet_header_map;
    }

    /**
     * 从完整的8583报文中获取16位图，转化成64位的01字符串
     *
     * @param content8583
     * @return
     */
    public static String get16BitMapFrom8583Byte(byte[] content8583) {
        // 取位图
        byte[] bitMap16 = getPacketHeaderMap(content8583);
        // 16位图转2进制位图64位字符串
        String bitMap64 = get16BitMapStr(bitMap16);

        return bitMap64;
    }


    //返回字段号码，例如005
    public static String getNumThree(int i) {
        String len = "";
        String iStr = String.valueOf(i);
        len = strCopy("0", 3 - iStr.length()) + iStr;
        return len;
    }

    /**
     * 获取字符串变长值
     *
     * @param valueStr
     * @param defLen   标识长度的字符串的length必须是偶数,这样bcd编码才能是正确的
     * @return
     */
    public static byte[] getVaryStrValue(String valueStr, int defLen, String defType) {
        try {
            byte[] lengthStrByte = null;
            byte[] valueStrByte = null;
            if (defType.contains("bcd")) { //bcd类型编码
                valueStrByte = CodeUtils.str2Bcd(valueStr);
            } else {
                valueStrByte = valueStr.getBytes(packet_encoding);
            }

            int len = valueStrByte.length;//字段实际长度
            String len1 = String.valueOf(len);

            LogUtils.d("valueStr:" + valueStr);


            String fixLen = strCopy("0", (defLen - len1.length())) + len1;
            LogUtils.d("fixLen:" + fixLen);
            lengthStrByte = CodeUtils.str2Bcd(fixLen);//变长域的长度都用bcd编码???????

            //组装
            byte[] package8583 = null;
            package8583 = arrayApend(lengthStrByte, valueStrByte);

            return package8583;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字段值做定长处理，不足定长则在后面补空格
     *
     * @param valueStr
     * @return
     */

    public static byte[] getFixFieldValue(String valueStr, String defType) {
        try {
            byte[] valueStrByte = null;
            if (defType.contains("bcd")) { //bcd类型编码
                valueStrByte = CodeUtils.str2Bcd(valueStr);
            } else {
                valueStrByte = valueStr.getBytes(packet_encoding);
            }
            //长度的判断使用转化后的字节数组长度，因为中文在不同的编码方式下，长度是不同的，GBK是2，UTF-8是3，按字符创长度算就是1.
            //解析报文是按照字节来解析的，所以长度以字节长度为准，防止中文带来乱码
            return valueStrByte;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getPacket_encoding() {
        return packet_encoding;
    }

    public static void setPacket_encoding(String packet_encoding) {
        packet_encoding = packet_encoding;
    }

    public static Map getMap8583Definition() {
        return map8583Definition;
    }

    public static void setMap8583Definition(Map map8583Definition) {
        map8583Definition = map8583Definition;
    }


    /**
     * 发送iso8583报文并得到返回字符串
     *
     * @param isoMessage
     * @return String
     */
    public static byte[] networkTransport(byte[] isoMessage)  {

        Socket socket= null;
        OutputStream out= null;
        InputStream in= null;
        byte[] data= null;

        try {
            //创建Socket对象，连接服务器
            socket = new Socket("1.1.1.194", 18012);//测试ip
            socket.setSoTimeout(8000);
            //通过客户端的套接字对象Socket方法，获取字节输出流，将数据写向服务器
            out = socket.getOutputStream();
            out.write(isoMessage);
            out.flush();

            //读取服务器发回的数据，使用socket套接字对象中的字节输入流
            in = socket.getInputStream();
            data = new byte[1024];
            in.read(data);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        finally{
            try {
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
                if(socket!=null){
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public static byte[] sendMessage(byte[] PackedRequestData){
        BufferedOutputStream outStream = null;
        BufferedReader receive_PackedResponseData = null;
        Socket connection = null;
        try {
            connection = new Socket("1.1.1.194", 18012);

        if (connection.isConnected()) {
            outStream = new BufferedOutputStream(
                    connection.getOutputStream());
            outStream.write(PackedRequestData);
            outStream.flush();
            System.out.println("-----Request sent to server---");
        }

        /** Receive Response */
        if (connection.isConnected()) {
            receive_PackedResponseData = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            LogUtils.d("-- RESPONSE recieved---"+CodeUtils.bytesToHexString(receive_PackedResponseData.readLine().getBytes()));
            return receive_PackedResponseData.readLine().getBytes();

            // System.out.println("Response : "+ISOUtil.hexString(receive_PackedResponseData.readLine().getBytes()));
        }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally{
            try {
                if(outStream!=null){
                    outStream.close();
                }
                if(receive_PackedResponseData!=null){
                    receive_PackedResponseData.close();
                }
                if(connection!=null){
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

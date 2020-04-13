package com.easyder.wrapper.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @描述: 图像压缩工具
 */
public final class ImageCompressUtil {
    /** 默认的压缩文件后缀 */
    public static final String DEFAULT_FILE_SUFFIX = "_b.jpg";

    /**
     * 获取默认的压缩图片地址. 文件名加上 #DEFAULT_FILE_SUFFIX
     * @param filepath
     * @return
     */
    public static File getDefaultCompressFile(String filepath){
        File origin = new File(filepath);
        StringBuilder sb = new StringBuilder(origin.getAbsolutePath().length() + 16);
        sb.append(origin.getParentFile().getAbsolutePath()+"/");
        String fileName = origin.getName();
        int dot = fileName.lastIndexOf('.');
        if (dot != -1){
            fileName = fileName.substring(0, dot + 1);
        }
        sb.append(fileName);
        sb.append(DEFAULT_FILE_SUFFIX);
        return new File(sb.toString());
    }
    
    /**
     * 压缩普通图片(768 * 1024)
     * @param filepath
     * @param outpath
     * @return
     */
    public static String compressPicture(String filepath, String outpath) throws CompressException{
//        return compress(filepath, outpath, 480, 800);
        return compress(filepath, outpath, 768, 1024);
    }

    /**
     * 压缩头像(96 * 96)
     * @param filepath
     * @param outpath
     * @return
     */
    public static String compressAvatar(String filepath, String outpath) throws CompressException{
        return compress(filepath, outpath, 96, 96);
    }

    /**
     * 压缩图片尺寸
     * 
     * @param filepath
     * @param outpath
     * @param rw
     *            请求的宽
     * @param rh
     *            请求的高
     * @return  压缩后的图片路径
     */
    public static String compress(String filepath, String outpath, int rw, int rh) throws CompressException {
        if (TextUtils.isEmpty(filepath)){
            throw new CompressException("Image file path not be blank or null");
        }
        
        File originFile = new File(filepath);
        if (!originFile.exists()){
            // 文件不存在
            originFile = null;
            throw new CompressException(filepath + " no found.");
        }
        originFile = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        
        // 获取宽，高,不做真实解码
        options.inJustDecodeBounds = true;

        // 解码，注：此时返回的bitmap为空，在options中有图片的真实宽，高
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
        final int picWidth = options.outWidth;
        final int picHeight = options.outHeight;

        // 设定图片文件为原大小的1/N
        options.inSampleSize = computeSampleSize(picWidth, picHeight, rw, rh);  // 品质 高
        //options.inSampleSize = ImageUtil.getSampleSize(options, rw, rh);  // 品质 低
//        options.inSampleSize = ImageUtil.computeSampleSize(options, 800, rw * rh); // 品质 中
//        options.inSampleSize = computeSampleSize2(picWidth, picHeight);   // 品质 中
        // 设定做真实解码
        options.inJustDecodeBounds = false;

        // 设定灰度
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // 设定，解码不占用系统核心内存，随时可以释放
        options.inInputShareable = true;
        options.inPurgeable = true;

        // 真实解码
        // Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(filepath, options);
        } catch (Exception e) {
            throw new CompressException("Error to load image from " + filepath, e);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // jpg 编码
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)) {
            throw new CompressException("Can not commpress image to JPEG");
        }

        byte[] imageData = stream.toByteArray();

        // copy 数据
        byte[] result = imageData.clone();

        // 关闭stream
        try {
            stream.close();
        } catch (Exception e) {
            throw new CompressException("Can not close bitmap stream", e);
        }

        // 储存图片至指定路径
        writeDataToFile(result, outpath);
        
        imageData = null;
        result = null;
//        if (bitmap != null){
//            bitmap.recycle();
//        }
        bitmap = null;
        
        return outpath;
    }

    /**
     * 压缩图片尺寸
     *            请求的宽
     *            请求的高
     *            低精度，压缩比例非常高
     * @return  压缩后的图片路径
     */
    public static String compressSmall(String filepath) throws CompressException {
        if (TextUtils.isEmpty(filepath)){
            throw new CompressException("Image file path not be blank or null");
        }

        File originFile = new File(filepath);
        if (!originFile.exists()){
            // 文件不存在
            originFile = null;
            throw new CompressException(filepath + " no found.");
        }
        originFile = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        // 获取宽，高,不做真实解码
        options.inJustDecodeBounds = true;

        // 解码，注：此时返回的bitmap为空，在options中有图片的真实宽，高
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
        final int picWidth = options.outWidth;
        final int picHeight = options.outHeight;

        // 设定图片文件为原大小的1/N
//        options.inSampleSize = computeSampleSize(picWidth, picHeight, rw, rh);  // 品质 高
        //options.inSampleSize = ImageUtil.getSampleSize(options, rw, rh);  // 品质 低
//        options.inSampleSize = ImageUtil.computeSampleSize(options, 800, rw * rh); // 品质 中
        options.inSampleSize = computeSampleSize2(picWidth, picHeight);   // 品质 中
        // 设定做真实解码
        options.inJustDecodeBounds = false;

        // 设定灰度
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // 设定，解码不占用系统核心内存，随时可以释放
        options.inInputShareable = true;
        options.inPurgeable = true;

        // 真实解码
        // Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(filepath, options);
        } catch (Exception e) {
            throw new CompressException("Error to load image from " + filepath, e);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        // jpg 编码
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)) {
            throw new CompressException("Can not commpress image to JPEG");
        }

        byte[] imageData = stream.toByteArray();

        // copy 数据
        byte[] result = imageData.clone();

        // 关闭stream
        try {
            stream.close();
        } catch (Exception e) {
            throw new CompressException("Can not close bitmap stream", e);
        }

        // 储存图片至指定路径
        writeDataToFile(result, filepath);

        imageData = null;
        result = null;
//        if (bitmap != null){
//            bitmap.recycle();
//        }
        bitmap = null;

        return filepath;
    }
    
    /**
     * 计算最小内存比
     * @param pw  原图片宽
     * @param ph  原图片高
     * @param rw  请求的宽
     * @param rh  请求的高
     * @return
     */
    private static int computeSampleSize(int pw, int ph, int rw, int rh){
        int scaleW = 1;
        if (rw > 0 && pw > 0 && pw > rw) {
            scaleW = pw / rw;
        }

        int scaleH = 1;
        if (rh > 0 && ph > 0 && ph > rh) {
            scaleH = ph / rh;
        }
        
        return scaleW > scaleH ? scaleH : scaleW;
    }
    
    /**
     * 计算最小内存比，不同的压缩算法, 压缩比高.
     * 以320 * 480 为基数
     * @param pw  原图片宽
     * @param ph  原图片高
     * @return
     */
    public static int computeSampleSize2(int pw, int ph){
        int sampleSize = 1;
        while ((pw / sampleSize > 320 * 2) || (ph / sampleSize > 480 * 2)) {
            sampleSize *= 2;
        }
        return sampleSize;
    }    
    
    /**
     * 输出数据到文件
     * @param data
     * @param outpath
     */
    private static void writeDataToFile(byte[] data, String outpath) throws CompressException{
        if (TextUtils.isEmpty(outpath)){
            throw new CompressException("The file path to write is blank or null. " + outpath);
        }
        ensureFileDir(outpath);
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outpath));
            try {
                out.write(data);
                out.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                throw new CompressException("Write file to " + outpath + " error", e);
           }finally{
               try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new CompressException("Error to save data to file " + outpath, e);
        }
    }
    
    /**
     * 确保文件目录存在
     * @param filepath
     */
    private static void ensureFileDir(String filepath){
        File f = new File(filepath);
        File dir = f.getParentFile();
        if (dir != null && !dir.exists()){
            dir.mkdirs();
        }
        dir = null;
        f = null;
    }
    
    
    /**
     * 图片压缩异常
     * @author holmes
     *
     */
    public static class CompressException extends Exception {

        /**
         * 
         */
        private static final long serialVersionUID = -6344851415604198563L;

        public CompressException() {
            super();
            // TODO Auto-generated constructor stub
        }

        public CompressException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
            // TODO Auto-generated constructor stub
        }

        public CompressException(String detailMessage) {
            super(detailMessage);
            // TODO Auto-generated constructor stub
        }

        public CompressException(Throwable throwable) {
            super(throwable);
            // TODO Auto-generated constructor stub
        }
        
        
        
    }
}

package com.grapro.chatapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//https://blog.csdn.net/Cs_hnu_scw/article/details/74011674 保存图片到数据库blob格式
public class ImageUtil {
    public static String imgToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.NO_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;

    }

    /**
     * 将Base64编码转换为图片
     *
     * @param base64Str
     * @param path
     * @return true
     */
    public static boolean base64ToFile(String base64Str, String path) {
        byte[] data = Base64.decode(base64Str, Base64.NO_WRAP);
        for (int i = 0; i < data.length; i++) {
            if (data[i] < 0) {
                //调整异常数据
                data[i] += 256;
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(path);
            os.write(data);
            os.flush();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double base64ImgSize(String base64) {
        if (null == base64 || base64.isEmpty()) {
            return 0;
        }
        int base64ImgSize = base64.length() / 1024;
        double imgSize = base64ImgSize * 0.75;
        Log.e("图片压缩","base64图片大小=" + base64ImgSize + "kb" + " 图片真实大小=" + imgSize + "kb");
        return imgSize;
    }

    /**
     * 将Base64编码转换为图片Bitmap
     */
    public static Bitmap base64ToFile(String base64Str) {
        byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }
}

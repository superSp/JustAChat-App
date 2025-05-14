package com.grapro.chatapplication.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;

public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * Assets下文件复制到sdcard
     *
     * @param context
     * @param assetsFileName
     * @param sdcardFilePath
     * @return
     */
    public static boolean copyAssets2Sdcard(Context context, String assetsFileName, String sdcardFilePath) {
        if (context == null) {
            return false;
        }
        if (TextUtils.isEmpty(assetsFileName) || TextUtils.isEmpty(sdcardFilePath)) {
            return false;
        }
        File sdcardFile = new File(sdcardFilePath);
        if (!sdcardFile.isFile()) {
            return false;
        }
        if (sdcardFile.exists()) {
            sdcardFile.delete();
        }
        //
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(assetsFileName);
            out = new FileOutputStream(sdcardFilePath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            //
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 复制文件（从一个位置 复制到 Sdcard另外的一个位置）
     *
     * @param fromFilePath
     * @param toFilePath
     */
    public static boolean copyFile(String fromFilePath, String toFilePath) {
        if (TextUtils.isEmpty(fromFilePath) || TextUtils.isEmpty(toFilePath)) {
            return false;
        }
        File fromFile = new File(fromFilePath);
        if (!fromFile.exists()) {
            return false;
        }
        if (!fromFile.canRead()) {
            return false;
        }
        //
        File toFile = new File(toFilePath);
        if (!toFile.exists()) {
            deleteFiles(toFile.getPath());
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        try {
            InputStream inStream = new FileInputStream(fromFile);
            OutputStream outStream = new FileOutputStream(toFile);
            byte[] bytes = new byte[1024];
            int i = 0;
            // 将内容写到新文件当中
            while ((i = inStream.read(bytes)) > 0) {
                outStream.write(bytes, 0, i);
            }
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    // ########################################################

    /**
     * 删除 文件 或 文件夹
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFiles(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        // 如果是文件
        if (file.isFile()) {
            return file.delete();
        } else
            // 如果是文件夹
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 文件夹没有内容,删除文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 删除文件夹内容
                boolean reslut = true;
                for (File item : file.listFiles()) {
                    reslut = reslut && item.delete();
                }
                // 删除文件夹
                return reslut && file.delete();
            }
        return false;
    }


    // ########################################################

    /**
     * 字节数组写入 filePath 路径对应的文件
     *
     * @param bytes
     * @param filePath
     * @return
     */
    public static boolean writeBytes2File(byte[] bytes, String filePath) {
        if (bytes == null || bytes.length == 0 || TextUtils.isEmpty(filePath)) {
            Log.d(TAG, "Trying to save null or 0 length strWb or path");
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            // 将数组中的数据写入到文件中。每行各数据之间TAB间隔
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return true;
    }


    /**
     * 将 String 写入到 path 路径对应的文件
     */
    public static void writeStr2File(String str, String filePath) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(filePath)) {
            Log.d(TAG, "Trying to save null or 0 length strWb or path");
            return;
        }
        File toFile = new File(filePath);
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists()) {
            toFile.delete();
        }
        try {
            toFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "IOException：" + e.getMessage());
            toFile = null;
        } finally {
            if (null != toFile && null != str) {
                OutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(toFile);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "FileNotFoundException：" + e.getMessage());
                    outStream = null;
                } finally {
                    if (null != outStream) {
                        try {
                            outStream.write(str.getBytes("utf-8"));
                            outStream.flush();
                        } catch (IOException e) {
                            Log.e(TAG, "IOException：" + e.getMessage());
                        } finally {
                            try {
                                if (null != outStream) {
                                    outStream.close();
                                }
                            } catch (IOException e) {
                                Log.d(TAG, "IOException" + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 从文件中读取String字符数据
     */
    public static String readStrByFilePath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                int len = fis.available();
                if (len > 0) {
                    byte[] buf = new byte[len];
                    fis.read(buf);
                    //
                    String string = new String(buf, "UTF-8");
                    return string;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return null;
    }


    // ########################################################

    /**
     * 获取指定文件名字
     *
     * @param file
     * @return
     */
    public static String getFileName(File file) {
        final String name = file.getName();
        if (!TextUtils.isEmpty(name)) {
            final int ext = name.lastIndexOf(".");
            if (ext > 0) {
                return name.substring(0, ext);
            }
        }
        return name;
    }

    /**
     * 获取 文件(或文件夹下所有文件) 的大小
     *
     * @param filePath
     * @return 字节大小 bytes
     * @throws Exception
     */
    public static long getFileSize(String filePath) throws Exception {
        long size = 0;
        if (TextUtils.isEmpty(filePath)) {
            return size;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return size;
        }
        if (file.isFile()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            return fis.available();
        } else if (file.isDirectory()) {
            File flist[] = file.listFiles();
            for (int i = 0; i < flist.length; i++) {
                size = size + getFileSize(flist[i].getPath());
            }
        }
        return size;
    }

    // ########################################################

    // 注解仅存在于源码中，在class字节码文件中不包含
    @Retention(RetentionPolicy.SOURCE)
    // 限定取值范围为
    @IntDef({SizeType.SIZETYPE_B, SizeType.SIZETYPE_KB, SizeType.SIZETYPE_MB, SizeType.SIZETYPE_GB})
    public @interface SizeType {
        // 获取文件大小单位为B的double值
        int SIZETYPE_B = 1;
        // 获取文件大小单位为KB的double值
        int SIZETYPE_KB = 2;
        // 获取文件大小单位为MB的double值
        int SIZETYPE_MB = 3;
        // 获取文件大小单位为GB的double值
        int SIZETYPE_GB = 4;
    }

    /**
     * 格式化为对应的单位：B、KB、MB、GB （保留两位小数）
     *
     * @param filePath
     * @param sizeType
     * @return
     */
    public static double getFormatFileSize(String filePath, @FileUtil.SizeType int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            blockSize = getFileSize(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatFileSize(blockSize, sizeType);
    }


    /**
     * 按指定单位格式化文件大小
     *
     * @param fileSize 字节
     * @param sizeType SIZETYPE_B SIZETYPE_KB SIZETYPE_MB SIZETYPE_GB
     * @return
     */
    private static double formatFileSize(long fileSize, @FileUtil.SizeType int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SizeType.SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileSize));
                break;
            case SizeType.SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileSize / 1024));
                break;
            case SizeType.SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileSize / 1048576));
                break;
            case SizeType.SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileSize / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 读取对应路径下的文件，以字节流的形式返回
     *
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static byte[] readZipFileToByteArray(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            return new byte[0];
        }
        return readFileToByteArray(file);
    }

    /**
     * 读取输入的文件，以字节流的形式返回
     *
     * @param file file文件
     * @return
     * @throws IOException
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }


}


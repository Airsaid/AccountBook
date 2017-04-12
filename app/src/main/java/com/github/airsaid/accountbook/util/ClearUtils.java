package com.github.airsaid.accountbook.util;

import android.content.Context;
import android.os.Environment;

import com.github.airsaid.accountbook.constants.AppConfig;

import java.io.File;
import java.math.BigDecimal;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/12
 * @desc 当前应用数据清除工具类，主要功能有清除内/外缓存，清除数据库，清除 sharedPreference，清除 files 和清除自定义目录。
 */
public class ClearUtils  {

    private ClearUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 清除本应用内部缓存 (/data/data/com.xxx.xxx/cache)
     *
     * @param context 上下文
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库 (/data/data/com.xxx.xxx/databases)
     *
     * @param context 上下文
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File(context.getFilesDir().getPath()
                + context.getPackageName() + "/databases"));
    }

    /**
     * 清除本应用 SharedPreference (/data/data/com.xxx.xxx/shared_prefs)
     *
     * @param context 上下文
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File(context.getFilesDir().getPath()
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 按名字清除本应用数据库
     *
     * @param context 上下文
     * @param dbName 数据库名称
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除 /data/data/com.xxx.xxx/files 下的内容
     *
     * @param context 上下文
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * 清除外部cache下的内容 (/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context 上下文
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     *
     * @param filePath 文件路径
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * 清除本应用所有的数据
     *
     * @param context 上下文
     * @param filePath 文件路径
     */
    public static void cleanApplicationData(Context context, String... filePath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        for (String fp : filePath) {
            cleanCustomCache(fp);
        }
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件。 此操作较危险，请慎用；
     *
     * @param directory 文件夹 File 对象
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                // delete file and folder. It's dangerous!
                if (item.isDirectory()) {
                    deleteFilesByDirectory(item);
                } else {
                    item.delete();
                }
            }
        }
    }

    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取当前应用的缓存大小
     */
    public static String getCacheSize() {
        return getFormatSize(getFolderSize(getAppCacheFile()));
    }

    /**
     * 清除当前应用缓存
     */
    public static void clearCache(){
        File file = getAppCacheFile();
        deleteFilesByDirectory(file);
    }

    /**
     * 获取缓存目录文件
     */
    public static File getAppCacheFile(){
        File file = new File(AppConfig.CACHE_PATH);
        if(!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}

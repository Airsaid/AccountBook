package com.github.airsaid.accountbook.constants;

import android.os.Environment;

import java.io.File;

/**
 * Created by zhouyou on 2016/6/22.
 * Class desc:
 *
 * APP 配置信息类，用与存储配置APP中的配置。
 */
public class AppConfig {

    /**
     * SD卡目录
     */
    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory() + File.separator;

    /**
     * 缓存目录名字
     */
    public static final String CACHE_DIR_NAME = "cache";

    /**
     * 缓存目录路径
     */
    public static final String CACHE_PATH = SDCARD_PATH.concat(CACHE_DIR_NAME.concat(File.separator));

    /**
     * 文件缓存目录路径
     */
    public static final String CACHE_FILE_PATH = CACHE_PATH.concat("file");

    /**
     * 图片缓存目录路径
     */
    public static final String CHCHE_IMAGE_PATH =  CACHE_PATH.concat("image");

    /**
     * 缓存大小
     */
    public static final int CACHE_SIZE = 104857600;// 100MB

    /**
     * 支出分类
     */
    public static final int TYPE_COST = 1;

    /**
     * 收入分类
     */
    public static final int TYPE_INCOME = 2;

    /** 分页条目 */
    public static final int LIMIT = 12;

}

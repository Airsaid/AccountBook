package com.github.airsaid.accountbook.constants;

import android.os.Environment;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.util.UiUtils;

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
    public static final String CACHE_DIR_NAME = UiUtils.getString(R.string.app_name);

    /**
     * 缓存目录路径
     */
    public static final String CACHE_PATH = SDCARD_PATH.concat(CACHE_DIR_NAME);

    /**
     * 文件缓存目录路径
     */
    public static final String CACHE_FILE_PATH = CACHE_PATH.concat(File.separator).concat("file");

    /**
     * 图片缓存目录路径
     */
    public static final String CHCHE_IMAGE_PATH = CACHE_PATH.concat(File.separator).concat("image");

    /**
     * 缓存大小
     */
    public static final int CACHE_SIZE = 104857600;// 100MB

    /** 缓存时间 */
    public static final int CACHE_DATE = 60 * 60 * 24;// 一天总秒数

    /** 支出分类 */
    public static final int TYPE_COST = 1;

    /** 收入分类 */
    public static final int TYPE_INCOME = 2;

    /** 申请帐薄消息类型 */
    public static final int TYPE_MSG_APPLY_BOOK = 1;

    /** 系统提示消息类型 */
    public static final int TYPE_MSG_SYSTEM = 2;

    /** 分页条目 */
    public static final int LIMIT = 12;

}

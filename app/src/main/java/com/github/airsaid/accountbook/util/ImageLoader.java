package com.github.airsaid.accountbook.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.Api;
import com.github.airsaid.accountbook.widget.transform.CropCircleTransformation;

import java.io.File;

/**
 * Created by zhouyou on 2016/11/21.
 * Class desc:
 *
 * 图片加载类，对 Glide 进行了封装。
 */
public class ImageLoader {

    // 默认占位图片资源
    private static final int PLACEHOLDER_IMAGE = R.mipmap.ic_default;
    // 加载错误显示图片
    private static final int ERROR_IMAGE = R.mipmap.ic_default;
    // 默认头像
    private static final int DEFAULT_ICON = R.mipmap.ic_default_icon;

    private static ImageLoader sImageLoader = new ImageLoader();
    private static Object sObject;

    private ImageLoader(){}

    public static ImageLoader getIns(Activity activity){
        return getInstance(activity);
    }

    public static ImageLoader getIns(android.support.v4.app.Fragment fragment){
        return getInstance(fragment);
    }

    public static ImageLoader getIns(android.app.Fragment fragment){
        return getInstance(fragment);
    }

    public static ImageLoader getIns(Context context){
        return getInstance(context);
    }

    private static ImageLoader getInstance(Object object){
        sObject = object;
        return sImageLoader;
    }

    /**
     * 加载圆形头像
     */
    public void loadIcon(String url, ImageView imageView){
        loadImageView(url, DEFAULT_ICON, DEFAULT_ICON, new CropCircleTransformation(getContext(sObject)), imageView);
    }

    /**
     * 从网络加载普通图片
     */
    public void load(String url, ImageView imageView){
        loadImageView(url, PLACEHOLDER_IMAGE, ERROR_IMAGE, null, imageView);
    }

    /**
     * 从网络加载普通图片，指定占位图。
     */
    public void load(String url, ImageView imageView, int placeholderId, int errorId){
        loadImageView(url, placeholderId, errorId, null, imageView);
    }

    /**
     * 从本地加载普通图片
     */
    public void load(Integer resId, ImageView imageView){
        loadImageView(resId, PLACEHOLDER_IMAGE, ERROR_IMAGE, null, imageView);
    }

    /**
     * 从本地加载普通图片，指定占位图。
     */
    public void load(Integer resId, ImageView imageView, int placeholderId, int errorId){
        loadImageView(resId, placeholderId, errorId, null, imageView);
    }

    /**
     * 从文件中加载普通图片
     */
    public void load(File file, ImageView imageView){
        loadImageView(file, PLACEHOLDER_IMAGE, ERROR_IMAGE, null, imageView);
    }

    /**
     * 从 uri 加载普通图片
     */
    public void load(Uri uri, ImageView imageView){
        loadImageView(uri, PLACEHOLDER_IMAGE, ERROR_IMAGE, null, imageView);
    }

    /**
     * 从字节数组加载普通图片
     */
    public void load(Byte[] bytes, ImageView imageView){
        loadImageView(bytes, PLACEHOLDER_IMAGE, ERROR_IMAGE, null, imageView);
    }

    /**
     * 从网络加载圆形图片
     */
    public void loadCircle(String url, ImageView imageView){
        loadImageView(url, -1, -1, new CropCircleTransformation(getContext(sObject)), imageView);
    }

    /**
     * 从本地加载圆形图片
     */
    public void loadCircle(Integer resId, ImageView imageView){
        loadImageView(resId, -1, -1, new CropCircleTransformation(getContext(sObject)), imageView);
    }

    private void loadImageView(Object object, int placeholderId, int errorId, Transformation transform, ImageView imageView){
        DrawableRequestBuilder builder = load(object, with());
        if(builder != null){
            // 设置占位图
            if(placeholderId != -1) builder.placeholder(placeholderId);
            // 设置加载错误时占位图
            if(errorId != -1)       builder.error(errorId);
            // 设置 transform
            if(transform != null)   builder.bitmapTransform(transform);
            // 加载到图片
            builder.into(imageView);
        }
    }

    private DrawableRequestBuilder load(Object object, RequestManager with){
        DrawableRequestBuilder builder = null;
        if(object instanceof String){
            String imageUrl = (String) object;
            if(!imageUrl.startsWith("http://")){
                imageUrl = Api.IMG_SERVER_URL.concat(imageUrl);
            }
            builder = with.load(imageUrl);
        }else if(object instanceof Integer){
            builder = with.load((Integer) object);
        }else if(object instanceof File){
            builder = with.load((File) object);
        }else if(object instanceof Uri){
            builder = with.load((Uri) object);
        }else if(object instanceof Byte[]){
            builder = with.load((Byte[]) object);
        }
        return builder;
    }

    private RequestManager with(){
        RequestManager with;
        if(sObject instanceof Activity && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && !((Activity) sObject).isDestroyed()){
            with = Glide.with((Activity) sObject);
        }else if(sObject instanceof android.support.v4.app.Fragment){
            with = Glide.with((android.support.v4.app.Fragment) sObject);
        }else if(sObject instanceof android.app.Fragment){
            with = Glide.with((android.app.Fragment) sObject);
        }else if(sObject instanceof Context){
            with = Glide.with((Context) sObject);
        }else{
            with = Glide.with(UiUtils.getContext());
        }
        return with;
    }

    private Context getContext(Object object) {
        Context context;
        if(object instanceof android.app.Fragment){
            context = ((android.app.Fragment) object).getActivity();
        }else if(object instanceof android.support.v4.app.Fragment){
            context = ((android.support.v4.app.Fragment) object).getActivity();
        }else{
            context = (Activity) object;
        }
        return context;
    }


}

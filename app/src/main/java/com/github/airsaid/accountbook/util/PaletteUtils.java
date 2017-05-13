package com.github.airsaid.accountbook.util;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/12
 * @desc
 */
public class PaletteUtils {

    public void setMainColor(final View view, Bitmap bitmap) {
        //首先获取一个Palette.Builder（稍后用到）
        Palette.Builder b = new Palette.Builder(bitmap);
        //设置好我们需要获取到多少种颜色
        b.maximumColorCount(1);
        Log.d("chenlongbo", String.valueOf(b.generate().getSwatches().size()));
        //异步的进行颜色分析
        b.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                //获取颜色列表的第一个
                Palette.Swatch swatch = palette.getSwatches().get(0);
                if (swatch != null) {
                    view.setBackgroundColor(swatch.getRgb());
                } else {
                    Log.e("chenlongbo", "swatch为空");
                }
            }
        });
    }
}

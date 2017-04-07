package com.github.airsaid.accountbook.util;

import android.util.TypedValue;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/7
 * @desc 尺寸相关操作工具类。
 */
public class DimenUtils {

    private DimenUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * dp 单位转换为 px
     */
    public static int dp2px(float dpValue){
        return (int)(dpValue * (UiUtils.getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    /**
     * px 单位转换为 dp
     */
    public static int px2dp(float pxValue){
        return (int)(pxValue / (UiUtils.getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    /**
     * sp 单位转换为 px
     */
    public static int sp2Px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, UiUtils.getContext().getResources().getDisplayMetrics());
    }

    /**
     * px 单位转换为 sp
     */
    public static float px2Sp(float pxVal) {
        return (pxVal / UiUtils.getContext().getResources().getDisplayMetrics().scaledDensity);
    }


}

package com.github.airsaid.accountbook.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/6
 * @desc 封装动画操作相关工具类。
 */
public class AnimUtils {

    public static final String TRANSLATION_X = "translationX";
    public static final String TRANSLATION_Y = "translationY";
    public static final String TRANSLATION_Z = "translationZ";
    public static final String ROTATION      = "rotation";
    public static final String ROTATION_X    = "rotationX";
    public static final String ROTATION_Y    = "rotationY";
    public static final String SCALE_X       = "scaleX";
    public static final String SCALE_Y       = "scaleY";
    public static final String ALPHA         = "alpha";

    private AnimUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 对指定 View 开始抖动动画。
     * @param view      指定的 View。
     * @param duration  动画执行时间，默认 500 毫秒。
     */
    public static void startVibrateAnim(View view, int duration){
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, ROTATION, 0f, -2f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, -20f, 0f, -20f, 0f, -20f, 0f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(view, ROTATION, -2f, 0f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim1).before(anim2).before(anim3);
        animSet.setDuration(duration == -1 ? 500 : duration);
        animSet.start();
    }



}

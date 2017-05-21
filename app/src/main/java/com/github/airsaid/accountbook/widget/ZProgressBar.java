package com.github.airsaid.accountbook.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;

import com.github.airsaid.accountbook.R;


/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/19
 * @desc 一个可动态配置颜色、弧度，带动画的自定义 ProgressBar。
 */
public class ZProgressBar extends ProgressBar {

    private final Context mContext;

    /** 背景颜色（默认为灰色） */
    private int mBackgroundColor  = Color.LTGRAY;
    /** 进度条颜色（默认为红色） */
    private int mProgressColor = Color.RED;
    /** 背景弧度（默认为 0） */
    private float mRadius = 0f;
    /** 动画时长（默认 500 毫秒） */
    private int mDuration = 500;

    public ZProgressBar(Context context) {
        this(context, null);
    }

    public ZProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
        createDrawable();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.ZProgressBar);
        mBackgroundColor = a.getColor(R.styleable.ZProgressBar_zpb_backgroundColor, mBackgroundColor);
        mProgressColor = a.getColor(R.styleable.ZProgressBar_zpb_progressColor, mProgressColor);
        mRadius = a.getDimension(R.styleable.ZProgressBar_zpb_radius, mRadius);
        mDuration = a.getInt(R.styleable.ZProgressBar_zpb_duration, mDuration);
        a.recycle();
    }

    private void createDrawable(){
        Drawable[] layers = new Drawable[2];
        Drawable background = makeBackground();
        Drawable progress = makeProgress();
        ClipDrawable clip = new ClipDrawable(progress
                , Gravity.LEFT, ClipDrawable.HORIZONTAL);
        layers[0] = background;
        layers[1] = clip;
        LayerDrawable layer = new LayerDrawable(layers);
        layer.setId(0, android.R.id.background);
        layer.setId(1, android.R.id.progress);
        setProgressDrawable(layer);
    }

    /**
     * 设置带动画进度
     * @param progress 进度
     */
    public void setAnimProgress(int progress){
        startAnimator(progress);
    }

    private void startAnimator(int progress){
        ValueAnimator animator = ValueAnimator.ofInt(0, progress);
        animator.setDuration(mDuration);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                setProgress(value);
            }
        });
    }

    /**
     * 设置默认背景颜色
     * @param color 色值
     */
    public void setDefBackgroundColor(int color){
        this.mBackgroundColor = color;
        createDrawable();
    }

    /**
     * 设置进度条颜色
     * @param color 色值
     */
    public void setProgressColor(int color){
        this.mProgressColor = color;
        createDrawable();
    }

    /**
     * 设置背景弧度
     * @param radius 弧度
     */
    public void setRadius(float radius){
        this.mRadius = radius;
        createDrawable();
    }

    /**
     * 设置动画时长
     * @param duration 时长
     */
    public void setDuration(int duration){
        this.mDuration = duration;
    }

    /**
     * 生成背景样式 drawable
     * @return drawable
     */
    private Drawable makeBackground(){
        return createShape(mRadius, mBackgroundColor);
    }

    /**
     * 生成 Progress 样式 drawable
     * @return drawable
     */
    private Drawable makeProgress(){
        return createShape(mRadius, mProgressColor);
    }

    /**
     * 根据 radius 和 color 来创建 ShapeDrawable
     * @param radius 弧度
     * @param color  颜色
     * @return drawable
     */
    private Drawable createShape(float radius, int color){
        ShapeDrawable shape = new ShapeDrawable();
        // 设置弧度
        radius = dp2px(radius);
        float[] outerRadii = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundShape = new RoundRectShape(outerRadii, null, null);
        shape.setShape(roundShape);
        // 设置颜色
        shape.getPaint().setColor(color);
        return shape;
    }

    private int dp2px(float dpValue){
        return (int)(dpValue * (mContext.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
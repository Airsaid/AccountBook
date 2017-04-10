package com.github.airsaid.accountbook.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/10
 * @desc 通用 Item 布局。
 */
public class CommonItemLayout extends FrameLayout{

    private TextView mTxtLeft;
    private TextView mTxtRight;
    private ImageView mImgRight;

    private Context mContext;
    // 左侧文字
    private String mLeftText;
    // 右侧文字
    private String mRightText;
    // 左侧文字颜色
    private int mLeftTextColor = Color.BLACK;
    // 右侧文字颜色
    private int mRightTextColor = Color.BLACK;
    // 右侧图片
    private int mRightImageId = -1;
    // 左侧图片
    private Drawable mLeftImage = null;

    public CommonItemLayout(Context context) {
        this(context, null);
    }

    public CommonItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttrs(attrs);
        initView();
        setData();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CommonItemLayout);
        mLeftText = a.getString(R.styleable.CommonItemLayout_cil_leftText);
        mRightText = a.getString(R.styleable.CommonItemLayout_cil_rightText);
        mLeftTextColor = a.getColor(R.styleable.CommonItemLayout_cil_leftTextColor, mLeftTextColor);
        mRightTextColor = a.getColor(R.styleable.CommonItemLayout_cil_rightTextColor, mRightTextColor);
        mRightImageId = a.getResourceId(R.styleable.CommonItemLayout_cil_rightImage, mRightImageId);
        mLeftImage = a.getDrawable(R.styleable.CommonItemLayout_cil_leftImage);
        a.recycle();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_common_item_layout, this, true);
        mTxtLeft = (TextView) findViewById(R.id.txt_left);
        mTxtRight = (TextView) findViewById(R.id.txt_right);
        mImgRight = (ImageView) findViewById(R.id.img_right);
    }

    private void setData() {
        mTxtLeft.setTextColor(mLeftTextColor);
        mTxtRight.setTextColor(mRightTextColor);

        if(mLeftText != null)   mTxtLeft.setText(mLeftText);
        if(mRightText != null)  mTxtRight.setText(mRightText);
        setRightImage(mRightImageId);
        if(mLeftImage != null)
            mLeftImage.setBounds(0, 0, DimenUtils.dp2px(22), DimenUtils.dp2px(12));
        mTxtLeft.setCompoundDrawables(null, null, mLeftImage, null);
    }

    public void setLeftText(String text){
        mTxtLeft.setText(text);
    }

    public void setRightTextColor(int resId){
        mTxtRight.setTextColor(resId);
    }

    public void setRightText(String text){
        mTxtRight.setText(text);
    }

    public void setRightText(int resId){
        mTxtRight.setText(resId);
    }

    public String getRightText(){
        return mTxtRight.getText().toString();
    }

    public void setLeftImage(int leftImageId){
        mLeftImage = UiUtils.getDrawable(leftImageId);
        setData();
    }

    public void setRightImage(int rightImageId){
        if(rightImageId != -1){
            mImgRight.setVisibility(View.VISIBLE);
            mImgRight.setImageResource(rightImageId);
        }else{
            mImgRight.setVisibility(View.GONE);
        }
    }

}

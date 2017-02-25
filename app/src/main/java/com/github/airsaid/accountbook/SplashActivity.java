package com.github.airsaid.accountbook;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.util.SPUtils;

import butterknife.BindView;


/**
 * Created by zhouyou on 2016/9/1.
 * Class desc: 闪屏页 Activity
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.txt_app_name)
    TextView mTxtAppName;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "wwfoot.ttf");
        mTxtAppName.setTypeface(typeface);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 判断是否是第一次登录
                boolean isFirstLogin = (boolean) SPUtils.getSP(mContext
                        , AppConstants.KEY_IS_FIRST_LOGIN, true);
                Intent intent = new Intent();
                if (isFirstLogin) {
                    intent.setClass(mContext, MainActivity.class);
                } else {
                    intent.setClass(mContext, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}

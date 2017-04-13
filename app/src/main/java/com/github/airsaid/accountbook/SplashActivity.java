package com.github.airsaid.accountbook;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.mvp.login.LoginActivity;
import com.github.airsaid.accountbook.mvp.main.MainActivity;
import com.github.airsaid.accountbook.util.AppUtils;
import com.github.airsaid.accountbook.util.UserUtils;

import butterknife.BindView;


/**
 * Created by zhouyou on 2016/9/1.
 * Class desc: 闪屏页 Activity
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.txt_app_name)
    TextView mTxtAppName;
    @BindView(R.id.txt_version)
    TextView mTxtVersion;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        // 设置新回复通知
        FeedbackAgent agent = new FeedbackAgent(mContext);
        agent.sync();

        Typeface typeface = Typeface.createFromAsset(getAssets(), "wwfoot.ttf");
        mTxtAppName.setTypeface(typeface);
        setVersion();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                // 判断用户是否已经登录
                if (UserUtils.checkLogin()) {
                    // 进入首页
                    intent.setClass(mContext, MainActivity.class);
                } else {
                    // 进入登录页
                    intent.setClass(mContext, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    /**
     * 设置版本号
     */
    private void setVersion() {
        mTxtVersion.setText("V".concat(AppUtils.getAppVersionName()));
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        mTxtVersion.startAnimation(anim);
        anim.setDuration(2000);
        anim.start();
    }
}

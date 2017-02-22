package com.github.airsaid.accountbook.widget.slideback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.airsaid.accountbook.BuildConfig;
import com.github.airsaid.accountbook.R;


/**
 * 这个Activity实现了可以滑动左侧边缘退出Activity的功能，类似iOS的交互行为。
 *
 * <p>调用 {@link #setSlideable(boolean)} 方法来设置是否支持滑动 </p>
 * <p>调用 {@link #setPreviousActivitySlideFollow(boolean)}
 * 方法来设置前一个activity是否跟随滑动 </p>
 *
 * @author lihong
 * @since 2016/03/10
 */
public class SlideBackActivity extends ActivityInterfaceImpl implements SlideFrameLayout.SlidingListener {
    /**
     * DEBUG
     */
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * TAG
     */
    private static final String TAG = "SlideActivity";

    /**
     * 后面Activity的预览View的初始偏移量
     */
    private float mBackPreviewViewInitOffset;

    /**
     * 是否可以滑动
     */
    private boolean mSlideable = true;

    /**
     * 后面的Activity是否跟随滑动
     */
    private boolean mPreviousActivitySlideFollow = true;

    /**
     * 是否打断调用{@link #finish()}方法
     */
    private boolean mInterceptFinish = false;

    /**
     * 前一个界面的activity
     */
    private Activity mPreviousActivity;

    /**
     * 是否需要查找activity，如果前一个activity没有的话，就没必要每次查找
     */
    private boolean mNeedFindActivityFlag = true;

    /**
     * 是否要停止activity
     */
    private boolean mNeedFinishActivityFlag = false;

    /**
     * SlideFrameLayout对象
     */
    private SlideFrameLayout mSlideFrameLayout;

    /**
     * Activity的生命周期回调
     */
    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacksImpl() {
        @Override
        public void onActivityDestroyed(Activity activity) {
            super.onActivityDestroyed(activity);
            onPreviousActivityDestroyed(activity);
        }
    };

    /**
     * 结束activity的任务
     */
    private Runnable mFinishTask = new Runnable() {
        @Override
        public void run() {
            if (DEBUG) {
                Log.i(TAG, "SlideActivity mFinishTask.run()   finish activity.");
            }
            doRealFinishForSlide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        if (mSlideable) {
            // 如果找不到前一个activity的content view，则不能滑动，典型的场景就是由外部app打开单独的一界面
            // 例如从通知栏中打开消息中心界面，所以可能当前进程就一个消息中心的activity，此时就不能滑动退出
            View previewView = getPreviousActivityContentView();
            if (previewView == null) {
                mSlideable = false;
            }
        }

        if (!mSlideable) {
            super.setContentView(view);
            return;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        // 屏幕宽的-1/3
        mBackPreviewViewInitOffset = -(1.f / 3) * metrics.widthPixels;

        mSlideFrameLayout = new SlideFrameLayout(this);
        int size = ViewGroup.LayoutParams.MATCH_PARENT;
        // 将内容View添加进容器中
        SlideFrameLayout.LayoutParams params = new SlideFrameLayout.LayoutParams(size, size);
        mSlideFrameLayout.addView(view, params);

        // 初始化
        mSlideFrameLayout.setShadowResource(R.drawable.sliding_back_shadow);
        mSlideFrameLayout.setSlideable(mSlideable);
        mSlideFrameLayout.setSlidingListener(this);

        super.setContentView(mSlideFrameLayout);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (slideOffset <= 0) {
            mInterceptFinish = false;
            offsetPreviousSnapshot(0);
        } else if (slideOffset < 1) {
            mInterceptFinish = true;
            offsetPreviousSnapshot(mBackPreviewViewInitOffset * (1 - slideOffset));
        } else {
            mInterceptFinish = false;
            offsetPreviousSnapshot(0);

            // Modified by lihong 2016/09/11 begin
            //
            // FixBug：在某此系统上面（例如YunOS），滑动退出时可能会闪一下，原因是
            // 调用 finish() 方法的时机太早了，当滑动松开手后，当前 activity 上面的 SlideFrameLayout
            // 仍然会继续滑动一段距离，而在这个过程中，SlideFrameLayout 中的绘制上一个 activity 内容的 PreviewView
            // 可能会继续绘制，就会看起来闪烁一下，如果将 PreviewView 背景设置为红色，就会清晰看到这样的效果。
            //
            // 解决办法：在这里先记录需要关闭 activity 的标志量，在滑动结束后 continueSettling(view, boolean) 里面
            // 再执行真正的关闭界面的操作，当然，这里为了确保界面能关闭，做了一个延迟的任务
            //
            // 记录需要关闭当前界面的flag
            mNeedFinishActivityFlag = true;
            // 作一个延迟任务，确保当前 activity 是始终能被关闭的
            mSlideFrameLayout.postDelayed(mFinishTask, 500);
            //finish();
            //overridePendingTransition(0, 0);
            //
            // Modified by lihong 2016/09/11 end
        }
    }

    @Override
    public void continueSettling(View panel, boolean settling) {
        // 如果需要关闭 activity 并且滚动结束时
        if (mNeedFinishActivityFlag && !settling) {
            // 移除task任务
            mSlideFrameLayout.removeCallbacks(mFinishTask);
            doRealFinishForSlide();
        }
    }

    @Override
    public void finish() {
        if (!mInterceptFinish) {
            super.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        release();
    }

    /**
     * 设置边缘的阴影的资源
     *
     * @param resId
     */
    public void setShadowResource(int resId) {
        if (mSlideFrameLayout != null) {
            mSlideFrameLayout.setShadowResource(resId);
        }
    }

    /**
     * 当滑动返回时调用，派生类可以重写这个方法，例如可以在这个方法中作一些统计工作，来记录关闭activity的行为
     */
    public void onSlideBack() {

    }

    /**
     * 是否可以滑动关闭
     */
    public boolean isSlideable() {
        return mSlideable;
    }

    /**
     * 设置是否可以滑动关闭Activity
     */
    public void setSlideable(boolean slideable) {
        mSlideable = slideable;

        if (mSlideFrameLayout != null) {
            mSlideFrameLayout.setSlideable(slideable);
        }
    }

    /**
     * 下一层的Activity是否跟着滑动，默认为true
     *
     * @param flag true/false
     */
    public void setPreviousActivitySlideFollow(boolean flag) {
        mPreviousActivitySlideFollow = flag;
    }

    /**
     * 执行finish动作
     */
    private void doRealFinishForSlide() {
        finish();
        overridePendingTransition(0, 0);
        onSlideBack();
    }

    /**
     * 得到上一个activity的根view
     *
     * @return 前一个界面的content view
     */
    private View getPreviousActivityContentView() {
        Activity previousActivity = getPreviousPreviewActivity();
        if (null != previousActivity) {
            return previousActivity.findViewById(android.R.id.content);
        }

        return null;
    }

    /**
     * 得到前一个preview的activity
     *
     * @return activity
     */
    private Activity getPreviousPreviewActivity() {
        Activity previousActivity = mPreviousActivity;
        if (previousActivity != null && previousActivity.isFinishing()) {
            previousActivity = null;
            mPreviousActivity = null;
        }

        if (previousActivity == null && mNeedFindActivityFlag) {
            previousActivity = ActivityStackManager.getPreviousActivity(this);
            mPreviousActivity = previousActivity;
            if (null == previousActivity) {
                mNeedFindActivityFlag = false;
            }

            // 如果前一个activity销毁后，主动则变量置为null，防止内存泄漏和滑动退出的异常情况
            if (previousActivity instanceof ActivityInterface) {
                ((ActivityInterface) previousActivity).setActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
            }
        }

        return previousActivity;
    }

    /**
     * 移动前一个activity的快照
     */
    private void offsetPreviousSnapshot(float translateX) {
        View view = getPreviousActivityContentView();
        if (view == null) {
            throw new NullPointerException("NullPointerException");
        }
        if (view != null && mSlideFrameLayout != null) {
            // 如果前一个界面不跟随一起滑动的话，把平移的值设置为0
            if (!mPreviousActivitySlideFollow) {
                translateX = 0;
            }

            mSlideFrameLayout.offsetPreviousSnapshot(view, translateX);
        }
    }

    /**
     * 前一个activity销毁时调用
     */
    private void onPreviousActivityDestroyed(Activity activity) {
        if (activity == mPreviousActivity) {
            if (DEBUG) {
                Log.d(TAG, "onPreviousActivityDestroyed(), previous activity destroy. Current activity = "
                    + this.getLocalClassName() + " Previous activity = " + activity.getLocalClassName());
            }

            release();

            // 尝试去新找一个预览activity
            mPreviousActivity = getPreviousPreviewActivity();

            if (DEBUG) {
                Log.i(TAG , "    try to find previous activity = "
                    + ((mPreviousActivity != null) ? mPreviousActivity.getLocalClassName() : "null"));
            }

            // 找不到前一个activity，则不能滑动
            if (null == mPreviousActivity) {
                mNeedFindActivityFlag = false;
                setSlideable(false);
            }
        }
    }

    /**
     * 释放一些状态
     */
    private void release() {
        // 去掉activity生命周期的回调，防止当前界面被前一个界面引用
        if (mPreviousActivity != null) {
            if (mPreviousActivity instanceof ActivityInterface) {
                ((ActivityInterface) mPreviousActivity).setActivityLifecycleCallbacks(null);
            }
        }

        mPreviousActivity = null;
    }
}
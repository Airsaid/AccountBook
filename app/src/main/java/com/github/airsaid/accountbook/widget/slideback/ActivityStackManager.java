package com.github.airsaid.accountbook.widget.slideback;

import android.app.Activity;
import android.util.Log;

import com.github.airsaid.accountbook.BuildConfig;

import java.util.LinkedList;

/**
 * @author lihong
 * @since 2016/10/28
 */
final class ActivityStackManager {
    /**
     * TAG
     */
    private static final String TAG = "BaseActivity";

    /**
     * DEBUG
     */
    private static final boolean DEBUG = BuildConfig.DEBUG;

    /**
     * 窗口栈。
     */
    private static LinkedList<Activity> mActivityStack = new LinkedList<>();

    /**
     * 得到指定activity前一个activity的实例
     *
     * @param curActivity 当前activity
     * @return 可能为null
     */
    public static Activity getPreviousActivity(Activity curActivity) {
        final LinkedList<Activity> activities = mActivityStack;
        Activity preActivity = null;
        for (int cur = activities.size() - 1; cur >= 0; cur--) {
            Activity activity = activities.get(cur);
            if (activity == curActivity) {
                int pre = cur - 1;
                if (pre >= 0) {
                    preActivity = activities.get(pre);
                }
            }
        }

        return preActivity;
    }

    /**
     * 从栈管理队列中移除该Activity。
     *
     * @param activity Activity。
     */
    public static synchronized void removeFromStack(Activity activity) {
        mActivityStack.remove(activity);

        printActivityStack();
    }

    /**
     * 将Activity加入栈管理队列中。
     *
     * @param activity Activity。
     */
    public static synchronized void addToStack(Activity activity) {
        // 移到顶端。
        mActivityStack.remove(activity);
        mActivityStack.add(activity);

        printActivityStack();
    }

    /**
     * 清除栈队列中的所有Activity。
     */
    public static synchronized void clearTask() {
        int size = mActivityStack.size();
        if (size > 0) {
            Activity[] activities = new Activity[size];
            mActivityStack.toArray(activities);

            for (Activity activity : activities) {
                activity.finish();
            }
        }
    }

    /**
     * 获得Activity栈
     */
    public static synchronized Activity[] getActivityStack() {
        Activity[] activities = new Activity[mActivityStack.size()];
        return mActivityStack.toArray(activities);
    }

    /**
     * 打印出当前activity stack的信息
     */
    private static void printActivityStack() {
        if (DEBUG) {
            int size = mActivityStack.size();
            if (size > 0) {
                Log.d(TAG, "Activity stack begin ======== ");
                Log.d(TAG, "    The activity stack: ");
                for (int i = size - 1; i >= 0; --i) {
                    Activity activity = mActivityStack.get(i);
                    Log.i(TAG, "    Activity" + (i + 1) + " = " + activity.getClass().getSimpleName());
                }
                Log.d(TAG, "Activity stack end ========== ");
            }
        }
    }
}

package com.github.airsaid.accountbook.util;

import android.app.Activity;

import java.util.Stack;


/**
 * Created by zhouyou on 2016/6/24.
 * Class desc:
 *
 * 自定义 Activity 管理栈。
 */
public class ActivityManager {

    private static Stack<Activity> activityStack;
    private static ActivityManager instance;
    private Activity currActivity;

    private ActivityManager() {}

    /**
     * 获取实例
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 将当前 Activity 推入栈中
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 弹出栈顶 Activity
     */
    public void popActivity(Activity activity) {
        if(activity == null || activityStack == null) {
            return;
        }
        if(activityStack.contains(activity)) {
            activityStack.remove(activity);
        }
        currActivity = activity;
    }

    /**
     * 退出栈中所有 Activity
     */
    public void popAllActivity() {
        popAllActivityExceptOne(null);
    }

    /**
     * 退出栈中除指定的 Activity 外的所有 Activity
     */
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = getTopActivity();
            if(null != activity && !activity.getClass().equals(cls)){
                destroyActivity(activity);
            }else{
                break;
            }
        }
    }

    /**
     * 销毁指定 Activity
     */
    public void destroyActivity(Activity activity) {
        if(activity != null){
            activity.finish();
            if(activityStack.contains(activity))
                activityStack.remove(activity);
        }
    }

    /**
     * 获取当前栈顶 Activity
     */
    public Activity getTopActivity() {
        if(activityStack == null || activityStack.empty()) {
            return null;
        }
        return activityStack.lastElement();
    }

    /**
     * 获取当前 Activity
     */
    public Activity getCurrActivity(){
        return currActivity;
    }

    /**
     * 获取栈中 Activity 的数量
     */
    public int getStackSize(){
        if(activityStack == null) return 0;

        return activityStack.size();
    }
}
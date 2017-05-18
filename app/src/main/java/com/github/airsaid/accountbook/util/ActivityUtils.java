/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.airsaid.accountbook.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.greenrobot.greendao.annotation.NotNull;

/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {

    private static Fragment mCurFragment = null;

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     *
     */
    public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                              @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    /**
     * 切换 Fragment
     */
    public static void switchFragment(@NonNull FragmentManager fragmentManager, Fragment targetFragment, int frameId) {
        switchFragment(fragmentManager, targetFragment, frameId,  0, 0);
    }

    /**
     * 带动画切换 Fragment
     */
    public static void switchFragment(@NonNull FragmentManager fragmentManager
            , @NotNull Fragment targetFragment, int frameId, int enter, int exit) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(enter != 0 && exit != 0) transaction.setCustomAnimations(enter, exit);
        if (!targetFragment.isAdded()) {
            if (mCurFragment != null) transaction.hide(mCurFragment);
            transaction.add(frameId, targetFragment).commit();
        } else {
            if (mCurFragment != null) transaction.hide(mCurFragment);
            transaction.show(targetFragment).commit();
        }
        mCurFragment = targetFragment;
    }

}

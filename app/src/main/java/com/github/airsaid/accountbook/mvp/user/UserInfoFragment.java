package com.github.airsaid.accountbook.mvp.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.CommonItemLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/10
 * @desc 个人资料 Fragment
 */
public class UserInfoFragment extends BaseFragment implements UserInfoContract.View {

    @BindView(R.id.img_icon)
    ImageView mImgIcon;
    @BindView(R.id.rlt_update_icon)
    RelativeLayout mRltUpdateIcon;
    @BindView(R.id.cil_username)
    CommonItemLayout mCilUsername;
    @BindView(R.id.cil_sex)
    CommonItemLayout mCilSex;
    @BindView(R.id.cil_age)
    CommonItemLayout mCilAge;

    private UserInfoContract.Presenter mPresenter;

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_info, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        setUserInfo();
    }

    /**
     * 设置用户信息。
     */
    private void setUserInfo() {
        User user = UserUtils.getUser();
        if(user != null){
            String username = user.getUsername();
            int sex = user.getSex();
            int age = user.getAge();
            mCilAge.setRightText(String.valueOf(age));
            mCilUsername.setRightText(username);
            mCilSex.setRightText(UserUtils.getSexText(sex));
        }
    }

    @OnClick({R.id.rlt_update_icon, R.id.cil_username, R.id.cil_sex, R.id.cil_age})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlt_update_icon:  // 修改头像
                showUpdateIconDialog();
                break;
            case R.id.cil_username:     // 修改用户名
                showUpdateUsernameDialog();
                break;
            case R.id.cil_sex:          // 修改性别
                showUpdateSexDialog();
                break;
            case R.id.cil_age:          // 修改年龄
                showUpdateAgeDialog();
                break;
        }
    }

    @Override
    public void showUpdateIconDialog() {

    }

    @Override
    public void showUpdateUsernameDialog() {

    }

    @Override
    public void showUpdateSexDialog() {

    }

    @Override
    public void showUpdateAgeDialog() {

    }
}

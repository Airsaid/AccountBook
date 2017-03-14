package com.github.airsaid.accountbook.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:43
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class LoginFragment extends BaseFragment implements LoginContract.View, View.OnFocusChangeListener {

    @BindView(R.id.img_head)
    ImageView mImgHead;
    @BindView(R.id.til_phone)
    TextInputLayout mTilPhone;
    @BindView(R.id.til_password)
    TextInputLayout mTilPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;

    private LoginContract.Presenter mPresenter;
    private EditText mEdtPhone;
    private EditText mEdtPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        mEdtPhone = mTilPhone.getEditText();
        mEdtPassword = mTilPassword.getEditText();
        mEdtPhone.setText("18600808560");
        mEdtPassword.setText("123456");
        mEdtPassword.setOnFocusChangeListener(this);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            ProgressUtils.show(mContext);
        } else {
            ProgressUtils.dismiss();
        }
    }

    @Override
    public void showLoginSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_login_success));
    }

    @Override
    public void showLoginFail(String msg) {
        ToastUtils.show(mContext, msg);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        String username = mEdtPhone.getText().toString();
        String password = mEdtPassword.getText().toString();
        User user = new User();
        user.username = username;
        user.password = password;
        if (mPresenter.checkUserInfo(user)) {
            mPresenter.login(user);
        } else {
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_uname_or_pwd));
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        mImgHead.setImageResource(b ? R.mipmap.ic_login_pwd : R.mipmap.ic_login_name);
    }
}

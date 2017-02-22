package com.github.airsaid.accountbook.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
public class LoginFragment extends BaseFragment implements LoginContract.View {

    @BindView(R.id.edt_username)
    EditText mEdtUsername;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;

    private LoginContract.Presenter mPresenter;

    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if(active){
            ProgressUtils.show(mContext);
        }else{
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
        String username = mEdtUsername.getText().toString();
        String password = mEdtPassword.getText().toString();
        User user = new User();
        user.username = username;
        user.password = password;
        if(mPresenter.checkUserInfo(user)){
            mPresenter.login(user);
        }else{
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_uname_or_pwd));
        }
    }
}

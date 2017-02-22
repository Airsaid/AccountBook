package com.github.airsaid.accountbook.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.login.LoginActivity;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @Date 2017/2/22 23:14
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class RegisterFragment extends BaseFragment implements RegisterContract.View {

    @BindView(R.id.edt_username)
    AutoCompleteTextView mEdtUsername;
    @BindView(R.id.edt_password)
    EditText mEdtPassword;

    private RegisterContract.Presenter mPresenter;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {

    }

    public void setLoadingIndicator(boolean active) {
        if(active){
            ProgressUtils.show(mContext);
        }else{
            ProgressUtils.dismiss();
        }
    }

    @Override
    public void showRegisterSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_register_success));
        startActivity(new Intent(mContext, LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void showRegisterFail(String msg) {
        ToastUtils.show(mContext, msg);
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @OnClick(R.id.btn_register)
    public void onClick() {
        // 注册
        String username = mEdtUsername.getText().toString();
        String password = mEdtPassword.getText().toString();
        User user = new User();
        user.username = username;
        user.password = password;
        if(mPresenter.checkUserInfo(user)){
            mPresenter.register(user);
        }else{
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_uname_or_pwd));
        }
    }
}

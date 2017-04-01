package com.github.airsaid.accountbook.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.forgetpwd.ForgetPasswordActivity;
import com.github.airsaid.accountbook.register.RegisterActivity;
import com.github.airsaid.accountbook.ui.dialog.VerifyPhoneDialog;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.RegexUtils;
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

    private EditText mEdtPhone;
    private EditText mEdtPassword;
    private LoginContract.Presenter mPresenter;
    private VerifyPhoneDialog mVerifyPhoneDialog;

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
        if(mEdtPassword == null) return;
        mEdtPassword.setOnFocusChangeListener(this);

        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = charSequence.toString();
                if (!RegexUtils.checkPhone(phone)) {
                    mTilPhone.setError(UiUtils.getString(R.string.hint_right_phone));
                } else {
                    mTilPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
                if (!RegexUtils.checkPassword(password)) {
                    mTilPassword.setError(UiUtils.getString(R.string.hint_right_password));
                } else {
                    mTilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
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
        UiUtils.enterHomePage(mContext);
    }

    @Override
    public void showLoginFail(Error e) {
        // 判断手机号是否未验证
        if(215 == e.code){
            // 提示用户验证手机
            Snackbar.make(mImgHead, UiUtils.getString(R.string.hint_verify_phone),  Snackbar.LENGTH_LONG)
                    .setAction(UiUtils.getString(R.string.verify), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String phone = mEdtPhone.getText().toString();
                            if(!RegexUtils.checkPhone(phone)){
                                ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
                            }else{
                                mPresenter.requestPhoneVerify(phone);
                            }

                            mVerifyPhoneDialog = new VerifyPhoneDialog();
                            mVerifyPhoneDialog.show(getChildFragmentManager(), "dialog");
                            mVerifyPhoneDialog.setOnVerifyPhoneCallback(new VerifyPhoneDialog.OnVerifyPhoneCallback() {
                                @Override
                                public void onVerifySuccess(String code) {
                                    mPresenter.verifyPhone(code);
                                }

                                @Override
                                public void onVerifyFail(String msg) {
                                    ToastUtils.show(mContext, msg);
                                }
                            });
                        }
                    }).show();
        }else{
            ToastUtils.show(mContext, e.getMessage());
        }
    }

    @Override
    public void showSendVerifyCodeSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_send_code));
    }

    @Override
    public void showSendVerifyCodeFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showVerifyPhoneSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_verify_phone_success));
        mVerifyPhoneDialog.dismiss();
    }

    @Override
    public void showVerifyPhoneFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @OnClick({R.id.btn_login, R.id.txt_forget_password, R.id.txt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.txt_forget_password:
                startActivity(new Intent(mContext, ForgetPasswordActivity.class));
                break;
            case R.id.txt_register:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
        }
    }

    private void login(){
        String phone = mEdtPhone.getText().toString();
        String password = mEdtPassword.getText().toString();
        User user = new User();
        user.phone = phone;
        user.password = password;
        if (mPresenter.checkUserInfo(user)) {
            if(!mTilPhone.isErrorEnabled() && !mTilPassword.isErrorEnabled()){
                mPresenter.login(user);
            }else{
                ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone_or_password));
            } 
        } else {
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_name_or_pwd));
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        mImgHead.setImageResource(b ? R.mipmap.ic_login_pwd : R.mipmap.ic_login_name);
    }
}

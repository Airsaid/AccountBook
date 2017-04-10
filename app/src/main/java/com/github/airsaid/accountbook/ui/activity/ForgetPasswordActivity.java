package com.github.airsaid.accountbook.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.source.UserDataSource;
import com.github.airsaid.accountbook.data.source.UserRepository;
import com.github.airsaid.accountbook.util.RegexUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.widget.VerifyCodeButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/3/17
 * @desc 忘记密码 Activity 逻辑比较简单，就不用 MVP 架构来写了，否则类要爆炸了！
 */
public class ForgetPasswordActivity extends BaseActivity {

    @BindView(R.id.til_phone)
    TextInputLayout mTilPhone;
    @BindView(R.id.til_code)
    TextInputLayout mTilCode;
    @BindView(R.id.btn_get_code)
    VerifyCodeButton mBtnGetCode;
    @BindView(R.id.til_password)
    TextInputLayout mTilPassword;

    private EditText mEdtPhone;
    private EditText mEdtCode;
    private EditText mEdtPassword;
    private UserRepository mRepository;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_forget_password));
        mEdtPhone = mTilPhone.getEditText();
        mEdtCode = mTilCode.getEditText();
        mEdtPassword = mTilPassword.getEditText();

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
        mEdtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String code = charSequence.toString();
                if(!RegexUtils.checkCode(code)){
                    mTilCode.setError(UiUtils.getString(R.string.hint_right_code));
                }else{
                    mTilCode.setErrorEnabled(false);
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

        mRepository = new UserRepository();
    }

    @OnClick({R.id.btn_get_code, R.id.btn_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code: // 获取验证码
                getCode();
                break;
            case R.id.btn_finish:   // 完成
                forgetPasswordFinish();
                break;
        }
    }

    private void getCode() {
        String phone = mEdtPhone.getText().toString();
        if(!RegexUtils.checkPhone(phone)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
        }else{
            mRepository.requestPasswordResetBySmsCode(phone, new UserDataSource.RequestMobileCodeCallback() {
                @Override
                public void requestSuccess() {
                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_send_code));
                    mBtnGetCode.start();
                }

                @Override
                public void requestFail(Error e) {
                    ToastUtils.show(mContext, e.getMessage());
                }
            });
        }
    }

    private void forgetPasswordFinish() {
        String phone = mEdtPhone.getText().toString();
        String code = mEdtCode.getText().toString();
        String newPassword = mEdtPassword.getText().toString();
        if(!RegexUtils.checkPhone(phone)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
        }else if(!RegexUtils.checkCode(code)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_code));
        }else if(!RegexUtils.checkPassword(newPassword)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_password));
        }else{
            mRepository.resetPasswordBySmsCode(code, newPassword, new UserDataSource.UpdatePasswordCallback() {
                @Override
                public void updateSuccess() {
                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_update_password_success));
                    finish();
                }

                @Override
                public void updateFail(Error e) {
                    ToastUtils.show(mContext, e.getMessage());
                }
            });
        }
    }
}

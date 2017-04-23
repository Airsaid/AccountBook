package com.github.airsaid.accountbook.mvp.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.ui.dialog.VerifyPhoneDialog;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.RegexUtils;
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

    @BindView(R.id.til_phone)
    TextInputLayout mTilPhone;
    @BindView(R.id.til_password)
    TextInputLayout mTilPassword;

    private EditText mEdtPhone;
    private EditText mEdtPassword;
    private RegisterContract.Presenter mPresenter;
    private VerifyPhoneDialog mVerifyPhoneDialog;

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        mEdtPhone = mTilPhone.getEditText();
        mEdtPassword = mTilPassword.getEditText();

        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
            public void afterTextChanged(Editable editable) {
            }
        });
        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void setLoadingIndicator(boolean active) {
        if (active) {
            ProgressUtils.show(mContext);
        } else {
            ProgressUtils.dismiss();
        }
    }

    @Override
    public void showRegisterSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_send_code));
        mVerifyPhoneDialog = new VerifyPhoneDialog(mContext);
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

    @Override
    public void showRegisterFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showVerifyPhoneSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_register_success));
        mVerifyPhoneDialog.dismiss();
        finish();
    }

    @Override
    public void showVerifyPhoneFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @OnClick({R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register: // 注册
                register();
                break;
        }
    }

    private void register() {
        String password = mEdtPassword.getText().toString();
        String phone = mEdtPhone.getText().toString();
        if (!RegexUtils.checkPhone(phone)) {
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
        } else if (!RegexUtils.checkPassword(password)) {
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_password));
        } else {
            User user = new User();
            user.phone = phone;
            user.password = password;
            mPresenter.register(user);
        }
    }
}

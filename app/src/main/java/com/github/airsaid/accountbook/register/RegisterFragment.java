package com.github.airsaid.accountbook.register;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.User;
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
    @BindView(R.id.til_code)
    TextInputLayout mTilCode;
    @BindView(R.id.til_password)
    TextInputLayout mTilPassword;
    @BindView(R.id.btn_get_code)
    AppCompatButton mBtnGetCode;
    
    private RegisterContract.Presenter mPresenter;
    private EditText mEdtPassword;
    private EditText mEdtPhone;
    private EditText mEdtCode;

    private TimeCount mTimeCount;

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
        mEdtCode = mTilCode.getEditText();
        mEdtPassword = mTilPassword.getEditText();
        mTimeCount = new TimeCount(60000, 1000);

        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = charSequence.toString();
                if(!RegexUtils.checkPhone(phone)){
                    mTilPhone.setError(UiUtils.getString(R.string.hint_right_phone));
                }else{
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
                if(!RegexUtils.checkPassword(password)){
                    mTilPassword.setError(UiUtils.getString(R.string.hint_right_password));
                }else{
                    mTilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtnGetCode.setClickable(false);
            mBtnGetCode.setBackgroundResource(R.color.gray);
            mBtnGetCode.setText(String.valueOf(millisUntilFinished / 1000 + "s"));
        }

        @Override
        public void onFinish() {
            mBtnGetCode.setClickable(true);
            mBtnGetCode.setText(UiUtils.getString(R.string.reset_get));
            mBtnGetCode.setSupportBackgroundTintList(UiUtils.getColorList(R.drawable.bg_buttton_default));
        }
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
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_register_success));
        getActivity().finish();
    }

    @Override
    public void showRegisterFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showGetCodeSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_get_code_success));
        // 开始倒计时
        mTimeCount.start();
    }

    @Override
    public void showGetCodeFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @OnClick({R.id.btn_get_code, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code: // 获取验证码
                getCode();
                break;
            case R.id.btn_register: // 注册
                register();
                break;
        }
    }

    private void getCode() {
        String phone = mEdtPhone.getText().toString();
        if(!RegexUtils.checkPhone(phone)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
        }else{
            mPresenter.getCode(phone);
        }
    }

    private void register() {
        String password = mEdtPassword.getText().toString();
        String phone = mEdtPhone.getText().toString();
        String code = mEdtCode.getText().toString();
        if(!RegexUtils.checkPhone(phone)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
        }else if(!RegexUtils.checkCode(code)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_code));
        }else if(!RegexUtils.checkPhone(password)){
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_password));
        }else{
            User user = new User();
            user.phone = phone;
            user.password = password;
            mPresenter.register(user);
        }
    }
}

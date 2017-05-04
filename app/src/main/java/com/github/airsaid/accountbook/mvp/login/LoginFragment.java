package com.github.airsaid.accountbook.mvp.login;

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
import com.github.airsaid.accountbook.base.BaseApplication;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.data.TypeDao;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.AccountDataSource;
import com.github.airsaid.accountbook.data.source.AccountRepository;
import com.github.airsaid.accountbook.mvp.register.RegisterActivity;
import com.github.airsaid.accountbook.ui.activity.ForgetPasswordActivity;
import com.github.airsaid.accountbook.ui.dialog.VerifyPhoneDialog;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.RegexUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

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
                    mTilPhone.setErrorEnabled(true);
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
    public void showLoginSuccess() {
        final User user = UserUtils.getUser();
        String objectId = user.getObjectId();
        createDefaultType(objectId);
        CrashReport.setUserId(objectId);
        final AccountRepository repository = new AccountRepository();
        repository.queryDefaultBook(user, new AccountDataSource.QueryDefaultBookCallback() {
            @Override
            public void querySuccess(AccountBook book) {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_login_success));
                UiUtils.enterHomePage(mContext);
            }

            @Override
            public void queryFail(Error e) {
                ProgressUtils.dismiss();
                ToastUtils.show(mContext, UiUtils.getString(R.string.toast_login_fail));
            }
        });
    }

    @Override
    public void showLoginFail(Error e) {
        ProgressUtils.dismiss();
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
                                ProgressUtils.show(mContext);
                                mPresenter.requestPhoneVerify(phone);
                            }

                            mVerifyPhoneDialog = new VerifyPhoneDialog(mContext);
                            mVerifyPhoneDialog.show(getChildFragmentManager(), "dialog");
                            mVerifyPhoneDialog.setOnVerifyPhoneCallback(new VerifyPhoneDialog.OnVerifyPhoneCallback() {
                                @Override
                                public void onVerifySuccess(String code) {
                                    ProgressUtils.show(mContext);
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
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_send_code));
    }

    @Override
    public void showSendVerifyCodeFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showVerifyPhoneSuccess() {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_verify_phone_success));
        mVerifyPhoneDialog.dismiss();
    }

    @Override
    public void showVerifyPhoneFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    /**
     * 创建默认的支出、收入分类。
     */
    @Override
    public void createDefaultType(String uid) {
        // 判断是否已经创建过
        TypeDao dao = BaseApplication.getInstance().getSession().getTypeDao();
        List<Type> list = dao.queryBuilder().where(TypeDao.Properties.Uid.eq(uid)).list();
        // 没有创建时创建默认分类数据
        if(list == null || list.size() <= 0){
            saveTypeData(uid, dao, AppConfig.TYPE_COST);
            saveTypeData(uid, dao, AppConfig.TYPE_INCOME);
        }
    }

    private void saveTypeData(String uid, TypeDao dao, int type){
        String[] types;
        if(type == AppConfig.TYPE_COST){
            types = getResources().getStringArray(R.array.account_cost_type);
        }else{
            types = getResources().getStringArray(R.array.account_income_type);
        }
        for (int i = 0; i < types.length; i++) {
            Type t = new Type();
            t.setUid(uid);
            t.setIndex(i);
            t.setType(type);
            t.setName(types[i]);
            t.setIcon(type == AppConfig.TYPE_COST ? "ic_cost_type_".concat(String.valueOf(i))
                    : "ic_income_type_".concat(String.valueOf(i)));
            dao.insert(t);
        }
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
                ProgressUtils.show(mContext, UiUtils.getString(R.string.load_login));
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

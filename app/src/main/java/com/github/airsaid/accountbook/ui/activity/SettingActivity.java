package com.github.airsaid.accountbook.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVUser;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.source.UserDataSource;
import com.github.airsaid.accountbook.data.source.UserRepository;
import com.github.airsaid.accountbook.util.ClearUtils;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.RegexUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.CommonItemLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/12
 * @desc 设置 Activity
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.cil_update_phone)
    CommonItemLayout mCilUpdatePhone;
    @BindView(R.id.cil_update_password)
    CommonItemLayout mCilUpdatePassword;
    @BindView(R.id.cil_clear)
    CommonItemLayout mCilClear;
    @BindView(R.id.cil_about)
    CommonItemLayout mCilAbout;
    private UserRepository mRepository;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_setting;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.setting));
        mRepository = new UserRepository();
        setData();
    }

    private void setData(){
        User user = UserUtils.getUser();
        if(user != null){
            String phone = user.getMobilePhoneNumber();
            mCilUpdatePhone.setRightText(phone);
            mCilClear.setRightText(ClearUtils.getCacheSize());
        }
    }

    @OnClick({R.id.cil_update_phone, R.id.cil_update_password, R.id.cil_clear, R.id.cil_about, R.id.llt_login_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cil_update_phone:     // 修改手机号
                showUpdatePhoneDialog();
                break;
            case R.id.cil_update_password:  // 修改密码
                showUpdatePwdDialog();
                break;
            case R.id.cil_clear:            // 清除缓存
                showClearCacheDialog();
                break;
            case R.id.cil_about:            // 关于 APP
                startActivity(new Intent(mContext, AboutPageActivity.class));
                break;
            case R.id.llt_login_out:        // 退出登录
                showLoginOutDialog();
                break;
        }
    }

    /**
     * 显示修改手机号 Dialog
     */
    private void showUpdatePhoneDialog() {
        final AppCompatEditText editText = new AppCompatEditText(mContext);
        editText.setHint(UiUtils.getString(R.string.hint_input_new_phone));
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_update_phone))
                .setCancelable(false)
                .setView(editText, DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_send_code), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String newPhone = editText.getText().toString();
                        User user = UserUtils.getUser();
                        if(!RegexUtils.checkPhone(newPhone)){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_right_phone));
                        }else if(newPhone.equals(user.getMobilePhoneNumber())){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_phone_not_equal));
                        }else{
                            ProgressUtils.show(mContext, UiUtils.getString(R.string.load_send_code));
                            user.setMobilePhoneNumber(newPhone);
                            mRepository.saveUserInfo(user, new UserDataSource.SaveUserInfoCallback() {
                                @Override
                                public void saveSuccess() {
                                    dialog.dismiss();
                                    showInputCodeDialog();
                                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_send_code));

                                    setData();
                                }

                                @Override
                                public void saveFail(Error e) {
                                    ProgressUtils.dismiss();
                                    ToastUtils.show(mContext, e.getMessage());
                                }
                            });
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    /**
     * 显示输入验证码 Dialog
     */
    private void showInputCodeDialog(){
        final AppCompatEditText editText = new AppCompatEditText(mContext);
        editText.setHint(UiUtils.getString(R.string.hint_code));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_update_phone))
                .setCancelable(false)
                .setView(editText, DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f))
                .setPositiveButton(UiUtils.getString(R.string.dialog_finish), null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code = editText.getText().toString();
                        if(!RegexUtils.checkCode(code)){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_right_code));
                        }else{
                            ProgressUtils.show(mContext, UiUtils.getString(R.string.load_verify));
                            mRepository.verifyPhone(code, new UserDataSource.VerifyPhoneCallback() {
                                @Override
                                public void verifySuccess() {
                                    dialog.dismiss();
                                    ProgressUtils.dismiss();
                                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_update_phone_success));
                                }

                                @Override
                                public void verifyFail(Error e) {
                                    ToastUtils.show(mContext, e.getMessage());
                                }
                            });
                        }
                    }
                });
            }
        });
        dialog.show();
    }


    /**
     * 显示修改密码 Dialog
     */
    private void showUpdatePwdDialog() {
        final AppCompatEditText editText = new AppCompatEditText(mContext);
        editText.setHint(UiUtils.getString(R.string.hint_input_new_pwd));
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_update_pwd))
                .setCancelable(false)
                .setView(editText, DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f), DimenUtils.dp2px(16f))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_finish), null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPwd = editText.getText().toString();
                        if(!RegexUtils.checkPassword(newPwd)){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_right_password));
                        }else{
                            User user = UserUtils.getUser();
                            user.setPassword(newPwd);
                            ProgressUtils.show(mContext, UiUtils.getString(R.string.load_update));
                            mRepository.saveUserInfo(user, new UserDataSource.SaveUserInfoCallback() {
                                @Override
                                public void saveSuccess() {
                                    ProgressUtils.dismiss();
                                    alertDialog.dismiss();
                                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_update_success));
                                }

                                @Override
                                public void saveFail(Error e) {
                                    ProgressUtils.dismiss();
                                    ToastUtils.show(mContext, e.getMessage());
                                }
                            });
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    /**
     * 显示确认清除缓存 Dialog
     */
    private void showClearCacheDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_clear_cache))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClearUtils.clearCache();
                        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_clear_cache_success));
                        setData();
                    }
                }).create().show();
    }

    /**
     * 显示确认退出 Dialog
     */
    private void showLoginOutDialog(){
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title))
                .setMessage(UiUtils.getString(R.string.dialog_content_login_out))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AVUser.logOut();
                        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_login_out_success));
                        UiUtils.enterLoginPage(mContext, true);
                    }
                }).create().show();
    }
}

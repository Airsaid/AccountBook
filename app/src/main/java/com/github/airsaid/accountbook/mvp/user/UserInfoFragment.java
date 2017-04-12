package com.github.airsaid.accountbook.mvp.user;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.avos.avoscloud.AVFile;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.util.DimenUtils;
import com.github.airsaid.accountbook.util.ImageLoader;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.CommonItemLayout;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.util.List;

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
     * 设置用户信息
     */
    private void setUserInfo() {
        User user = UserUtils.getUser();
        if(user != null){
            String username = user.getUsername();
            int sex = user.getSex();
            int age = user.getAge();
            mCilAge.setRightText(String.valueOf(age).concat(UiUtils.getString(R.string.year)));
            mCilSex.setRightText(UserUtils.getSexText(sex));
            mCilUsername.setRightText(username);
            AVFile avatar = user.getAvatar();
            if(avatar != null){
                ImageLoader.getIns(this).loadIcon(avatar.getUrl(), mImgIcon);
            }
        }
    }

    @OnClick({R.id.rlt_update_icon, R.id.cil_username, R.id.cil_sex, R.id.cil_age})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlt_update_icon:  // 修改头像
                showUpdateIcon();
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

    /**
     * 显示修改头像页面
     */
    @Override
    public void showUpdateIcon() {
        FunctionConfig config = new FunctionConfig();
        config.setType(LocalMediaLoader.TYPE_IMAGE);
        config.setCompress(true);
        config.setMaxSelectNum(1);
        config.setSelectMode(2);
        config.setShowCamera(true);
        config.setEnablePreview(true);
        config.setEnableCrop(true);
        config.setCopyMode(FunctionConfig.CROP_MODEL_DEFAULT);
        config.setPreviewColor(UiUtils.getColor(R.color.textWhite));
        config.setCompleteColor(UiUtils.getColor(R.color.textWhite));
        config.setBottomBgColor(UiUtils.getColor(R.color.colorAccent));
        config.setPreviewBottomBgColor(UiUtils.getColor(R.color.colorAccent));
        PictureConfig.init(config);
        // 启动相册并设置回调函数
        PictureConfig.getPictureConfig().openPhoto(mContext, new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> list) {
                String path = "";
                if(list != null && list.size() > 0){
                    LocalMedia media = list.get(0);
                    // 判断是否压缩过
                    if(media.isCompressed()){
                        // 压缩过，取压缩图：media.getCompressPath();
                        path = media.getCompressPath();
                    }else{
                        // 取原图：media.getPath();
                        path = media.getPath();
                        // 判断是否裁剪
                        if(media.getCutPath() != null){
                            // 取裁剪图
                            path = media.getCutPath();
                        }
                    }
                }

                try {
                    File avatarFile = new File(path);
                    AVFile avatar = AVFile.withAbsoluteLocalPath(avatarFile.getName(), avatarFile.getPath());
                    User user = UserUtils.getUser();
                    user.setAvatar(avatar);
                    ProgressUtils.show(mContext, UiUtils.getString(R.string.load_update));
                    mPresenter.saveUserInfo(user);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(mContext, UiUtils.getString(R.string.toast_upload_fail));
                }
            }
        });
    }

    /**
     * 显示修改用户名 Dialog
     */
    @Override
    public void showUpdateUsernameDialog() {
        // 回显用户名
        final AppCompatEditText editText = new AppCompatEditText(mContext);
        String username = mCilUsername.getRightText();
        editText.setText(username);
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_update_username))
                .setView(editText, DimenUtils.dp2px(15f), DimenUtils.dp2px(15f), DimenUtils.dp2px(15f), DimenUtils.dp2px(15f))
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_affirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = editText.getText().toString();
                        if(TextUtils.isEmpty(newUsername) || newUsername.length() > 16){
                            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_username_length));
                        }else{
                            User user = UserUtils.getUser();
                            user.setUsername(newUsername);
                            ProgressUtils.show(mContext, UiUtils.getString(R.string.load_update));
                            mPresenter.saveUserInfo(user);
                        }
                    }
                }).create().show();
    }

    /**
     * 显示修改性别 Dialog
     */
    @Override
    public void showUpdateSexDialog() {
        final String[] sexs = UiUtils.getStringArray(R.array.update_sex);
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_update_sex))
                .setItems(sexs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = UserUtils.getUser();
                        user.setSex(which + 1);
                        ProgressUtils.show(mContext, UiUtils.getString(R.string.load_update));
                        mPresenter.saveUserInfo(user);
                    }
                }).create().show();
    }

    /**
     * 显示修改年龄 Dialog
     */
    @Override
    public void showUpdateAgeDialog() {
        String[] ages = new String[131];
        for (int i = 0; i < 131; i++) {
            ages[i] = String.valueOf(i).concat(UiUtils.getString(R.string.year));
        }
        new AlertDialog.Builder(mContext)
                .setTitle(UiUtils.getString(R.string.dialog_title_update_sex))
                .setItems(ages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = UserUtils.getUser();
                        user.setAge(which);
                        ProgressUtils.show(mContext, UiUtils.getString(R.string.load_update));
                        mPresenter.saveUserInfo(user);
                    }
                }).create().show();
    }

    /**
     * 保存用户信息成功
     */
    @Override
    public void saveUserInfoSuccess() {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_update_success));
        setUserInfo();
    }

    /**
     * 保存用户信息失败
     */
    @Override
    public void saveUserInfoFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }
}

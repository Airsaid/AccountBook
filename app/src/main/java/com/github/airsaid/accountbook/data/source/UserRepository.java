package com.github.airsaid.accountbook.data.source;


import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @date 2017/2/22 22:52
 * @blog http://blog.csdn.net/airsaid
 * @desc
 */
public class UserRepository implements UserDataSource {

    /**
     * 用户注册，注册成功后会发送验证码给用户，此时验证完成后才算注册完成。
     * @param user
     * @param callback
     */
    @Override
    public void register(User user, final RegisterCallback callback) {
        AVUser avUser = new AVUser();
        String phone = user.phone;
        String password = user.password;
        avUser.setUsername(phone);
        avUser.setPassword(password);
        avUser.setMobilePhoneNumber(phone);
        avUser.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                if (e == null) {
                    callback.registerSuccess();
                } else {
                    callback.registerFail(new Error(e));
                }
            }
        });
    }

    /**
     * 用户登录，登录失败后需要判断是否是因为手机号未验证，如果是则让用户验证。
     * @param user
     * @param callback
     */
    @Override
    public void login(User user, final LoginCallback callback) {
        AVUser.logInInBackground(user.phone, user.password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if(e == null){
                    callback.loginSuccess();
                }else{
                    callback.loginFail(new Error(e));
                }
            }
        });
    }

    /**
     * 根据验证码校验手机号。
     * @param code
     * @param callback
     */
    @Override
    public void verifyPhone(String code, final VerifyPhoneCallback callback) {
        AVUser.verifyMobilePhoneInBackground(code, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    callback.verifySuccess();
                } else {
                    callback.verifyFail(new Error(e));
                }
            }
        });
    }

    /**
     * 根据手机号请求验证码。
     * @param phone
     * @param callback
     */
    @Override
    public void requestPhoneVerify(String phone, final sendVerifyCodeCallback callback) {
        AVUser.requestMobilePhoneVerifyInBackground(phone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    callback.sendVerifyCodeSuccess();
                } else {
                    callback.sendVerifyCodeFail(new Error(e));
                }
            }
        });
    }
}

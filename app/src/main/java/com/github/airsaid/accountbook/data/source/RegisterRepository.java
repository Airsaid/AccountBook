package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @date 2017/2/22 22:52
 * @blog http://blog.csdn.net/airsaid
 * @desc
 */
public class RegisterRepository implements RegisterDataSource {

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
                    // successfully
                } else {
                    // failed
                }
            }
        });


        AVUser.signUpOrLoginByMobilePhoneInBackground(phone, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if(e == null){
                    callback.registerSuccess(new User(avUser));
                }else{
                    callback.registerFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void getCode(String phone, final GetCodeCallback callback) {
        AVUser.requestLoginSmsCodeInBackground(phone, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    callback.getSuccess();
                }else{
                    callback.getFail(new Error(e));
                }
            }
        });
    }
}

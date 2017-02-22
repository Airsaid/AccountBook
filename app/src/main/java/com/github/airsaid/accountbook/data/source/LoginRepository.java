package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:52
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class LoginRepository implements LoginDataSource {

    @Override
    public void login(User user, final LoginCallback callback) {
        AVUser.logInInBackground(user.username, user.password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                if(e == null){
                    callback.loginSuccess();
                }else{
                    callback.loginFail(e.getMessage());
                }
            }
        });
    }
}

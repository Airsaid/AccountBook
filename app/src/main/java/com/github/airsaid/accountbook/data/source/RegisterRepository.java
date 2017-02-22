package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.github.airsaid.accountbook.data.User;

/**
 * @author Airsaid
 * @Date 2017/2/22 22:52
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class RegisterRepository implements RegisterDataSource {

    @Override
    public void register(User user, final RegisterCallback callback) {
        AVUser avUser = new AVUser();
        avUser.setUsername(user.username);
        avUser.setPassword(user.password);
        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    callback.registerSuccess();
                }else{
                    callback.registerFail(e.getMessage());
                }
            }
        });
    }
}

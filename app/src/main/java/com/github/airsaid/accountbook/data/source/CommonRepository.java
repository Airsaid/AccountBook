package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.github.airsaid.accountbook.data.AboutApp;
import com.github.airsaid.accountbook.data.Error;

/**
 * @author Airsaid
 * @Date 2017/4/24 21:43
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class CommonRepository implements CommonDataSource {

    @Override
    public void aboutApp(final GetAboutAppInfoCallback callback) {
        AVQuery<AboutApp> query = AVQuery.getQuery(AboutApp.class);
        query.getFirstInBackground(new GetCallback<AboutApp>() {
            @Override
            public void done(AboutApp aboutApp, AVException e) {
                if(e == null){
                    callback.getSuccess(aboutApp);
                }else{
                    callback.getFail(new Error(e));
                }
            }
        });
    }
}

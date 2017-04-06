package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.github.airsaid.accountbook.constants.ApiConstant;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.i.Callback;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc
 */
public class AccountRepository implements AccountDataSource {

    @Override
    public void saveAccount(Account account, final Callback callback) {
        AVObject a = new AVObject(ApiConstant.TAB_ACCOUNT);
        a.put(ApiConstant.TYPE,  account.type);
        a.put(ApiConstant.CTYPE, account.cType);
        a.put(ApiConstant.MONEY, account.money);
        a.put(ApiConstant.DATE,  account.date);
        a.put(ApiConstant.NOTE,  account.note);
        a.put(ApiConstant.UID,   AVUser.getCurrentUser());
        a.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    callback.requestSuccess();
                }else{
                    callback.requestFail(new Error(e));
                }
            }
        });
    }
}

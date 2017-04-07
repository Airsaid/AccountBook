package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.github.airsaid.accountbook.constants.ApiConstant;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.util.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    @Override
    public void queryAccount(String startDate, String endDate, int page, final QueryAccountListCallback callback) {
        AVQuery<AVObject> startDateQuery = new AVQuery<>(ApiConstant.TAB_ACCOUNT);
        startDateQuery.whereGreaterThanOrEqualTo(ApiConstant.DATE,
                DateUtils.getDateWithDateString(startDate, DateUtils.FORMAT_MAIN_TAB));

        AVQuery<AVObject> endDateQuery = new AVQuery<>(ApiConstant.TAB_ACCOUNT);
        endDateQuery.whereLessThan(ApiConstant.DATE
                , DateUtils.getDateWithDateString(endDate, DateUtils.FORMAT_MAIN_TAB));

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null){
                    List<Account> accounts = new ArrayList<>();
                    for (AVObject a : list) {
                        int type = a.getInt(ApiConstant.TYPE);
                        String money = a.getString(ApiConstant.MONEY);
                        String cType = a.getString(ApiConstant.CTYPE);
                        Date date = a.getDate(ApiConstant.DATE);
                        String note = a.getString(ApiConstant.NOTE);
                        accounts.add(new Account(type, money, cType, date, note));
                    }
                    callback.querySuccess(accounts);
                }else{
                    callback.queryFail(new Error(e));
                }
            }
        });
    }
}

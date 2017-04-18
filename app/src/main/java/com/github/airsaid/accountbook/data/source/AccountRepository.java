package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.constants.Api;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc
 */
public class AccountRepository implements AccountDataSource {

    @Override
    public void saveAccount(User user, final Account account, final Callback callback) {
        queryDefaultBook(user, new QueryDefaultBookCallback() {
            @Override
            public void querySuccess(AccountBook book) {
                if(book != null){
                    long bid = book.getBid();
                    account.setBid(bid);
                    account.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null){
                                callback.requestSuccess();
                            }else{
                                callback.requestFail(new Error(e));
                            }
                        }
                    });
                }else{
                    Error error = new Error();
                    error.message = UiUtils.getString(R.string.toast_add_fail_login_reset);
                    callback.requestFail(error);
                }
            }

            @Override
            public void queryFail(Error e) {
                callback.requestFail(e);
            }
        });
    }

    @Override
    public void queryAccounts(User user, final String startDate, final String endDate, int page, final QueryAccountListCallback callback) {
        queryDefaultBook(user, new QueryDefaultBookCallback() {
            @Override
            public void querySuccess(AccountBook book) {
                long bid = book.getBid();
                AVQuery<Account> startDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
                startDateQuery.whereEqualTo(Api.BID, bid);
                startDateQuery.whereGreaterThanOrEqualTo(Api.DATE,
                        DateUtils.getDateWithDateString(startDate, DateUtils.FORMAT_MAIN_TAB));

                AVQuery<Account> endDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
                endDateQuery.whereEqualTo(Api.BID, bid);
                endDateQuery.whereLessThan(Api.DATE,
                        DateUtils.getDateWithDateString(endDate, DateUtils.FORMAT_MAIN_TAB));

                AVQuery<Account> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
                query.orderByDescending(Api.DATE);// 按时间，降序排列
                query.findInBackground(new FindCallback<Account>() {
                    @Override
                    public void done(List<Account> list, AVException e) {
                        if(e == null){
                            callback.querySuccess(list);
                        }else{
                            callback.queryFail(new Error(e));
                        }
                    }
                });
            }

            @Override
            public void queryFail(Error e) {
                callback.queryFail(e);
            }
        });
    }

    @Override
    public void queryDefaultBook(User user, final AccountDataSource.QueryDefaultBookCallback callback) {
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.OWNER, user);
        query.whereEqualTo(Api.IS_CURRENT, true);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if(e == null){
                    callback.querySuccess(list != null && list.size() > 0 ? list.get(0) : null);
                }else{
                    callback.queryFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void createDefaultBook(User user, final Callback callback) {
        AccountBook book = new AccountBook();
        book.setOwner(user);
        book.setCurrent(true);
        book.setName(UiUtils.getString(R.string.def_book_name));
        book.setCover(UiUtils.getString(R.string.def_book_cover));
        book.setScene(UiUtils.getString(R.string.def_book_scene));
        book.addShare(user);
        book.saveInBackground(new SaveCallback() {
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
    public void queryBooks(User user, final QueryBooksCallback callback) {
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.OWNER, user);
        query.include(Api.SHARES);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if(e == null){
                    callback.querySuccess(list);
                }else{
                    callback.queryFail(new Error(e));
                }
            }
        });
    }
}

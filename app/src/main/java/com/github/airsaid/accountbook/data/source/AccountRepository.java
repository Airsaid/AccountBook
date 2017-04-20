package com.github.airsaid.accountbook.data.source;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
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

    @Override
    public void addShareBook(final User user, long bid, final Callback callback) {
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.BID, bid);
        query.getFirstInBackground(new GetCallback<AccountBook>() {
            @Override
            public void done(final AccountBook accountBook, AVException e) {
                if(e == null){
                    if(accountBook == null){
                        Error error = new Error();
                        error.message = UiUtils.getString(R.string.toast_query_book_empty);
                        callback.requestFail(error);
                    }else{
                        if(accountBook.getShares().contains(user)){
                            Error error = new Error();
                            error.message = UiUtils.getString(R.string.toast_has_add_share_book);
                            callback.requestFail(error);
                        }else{
                            accountBook.addShare(user);
                            accountBook.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        AccountBook book = new AccountBook();
                                        book.setOwner(user);
                                        book.setBid(accountBook.getBid());
                                        book.setName(accountBook.getName());
                                        book.setScene(accountBook.getScene());
                                        book.setShare(accountBook.getShares());
                                        book.setCover(accountBook.getCover());
                                        addBook(book, new Callback() {
                                            @Override
                                            public void requestSuccess() {
                                                callback.requestSuccess();
                                            }

                                            @Override
                                            public void requestFail(Error e) {
                                                callback.requestFail(e);
                                            }
                                        });
                                    }else{
                                        callback.requestFail(new Error(e));
                                    }
                                }
                            });
                        }
                    }
                }else{
                    callback.requestFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void addBook(AccountBook book, final Callback callback) {
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
    public void setCurrentBook(User user, final long bid, final Callback callback) {
        // 查询当前用户所有帐薄
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.OWNER, user);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if(e == null){
                    // 设置帐薄 ID 相同帐薄为当前帐薄
                    for (AccountBook book : list) {
                        book.setCurrent(book.getBid() == bid);
                    }
                    AVObject.saveAllInBackground(list, new SaveCallback() {
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
                    callback.requestFail(new Error(e));
                }
            }
        });

    }

    @Override
    public void editBook(final AccountBook book, final Callback callback) {
        // 查找该账簿 id　对应的所有账簿
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.BID, book.getBid());
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if(e == null){
                    // 统一修改
                    for (AccountBook accountBook : list) {
                        accountBook.setName(book.getName());
                        accountBook.setScene(book.getScene());
                        accountBook.setCover(book.getCover());
                    }

                    AVObject.saveAllInBackground(list, new SaveCallback() {
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
                    callback.requestFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void deleteBook(long bid, final Callback callback) {
        // 查找该账簿 id　对应的所有账簿
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.BID, bid);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if(e == null){
                    // 统一删除
                    AVObject.deleteAllInBackground(list, new DeleteCallback() {
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
                    callback.requestFail(new Error(e));
                }
            }
        });
    }
}

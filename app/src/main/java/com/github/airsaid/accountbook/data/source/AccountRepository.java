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
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Msg;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.LogUtils;
import com.github.airsaid.accountbook.util.SPUtils;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.ArrayList;
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
    public void saveAccount(final User user, final Account account, final Callback callback) {
        long bid = SPUtils.getDefBookBid();
        if(bid != 0){
            account.setBid(bid);
            account.setOwner(user);
            account.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        callback.requestSuccess();
                    } else {
                        callback.requestFail(new Error(e));
                    }
                }
            });
        }else{
            queryDefaultBook(user, new QueryDefaultBookCallback() {
                @Override
                public void querySuccess(AccountBook book) {
                    saveAccount(user, account, callback);
                }

                @Override
                public void queryFail(Error e) {
                    callback.requestFail(e);
                }
            });
        }
    }

    @Override
    public void deleteAccount(Account account, final Callback callback) {
        account.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    callback.requestSuccess();
                } else {
                    callback.requestFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void queryAccounts(User user, String startDate, String endDate, int type, int page, final QueryAccountsCallback callback) {
        AVQuery<Account> startDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
        if(type != -1)
            startDateQuery.whereEqualTo(Api.TYPE, type);
        startDateQuery.whereEqualTo(Api.OWNER, user);
        startDateQuery.whereGreaterThanOrEqualTo(Api.DATE,
                DateUtils.getDateWithDateString(startDate, DateUtils.FORMAT_MAIN_TAB));

        AVQuery<Account> endDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
        if(type != -1)
            endDateQuery.whereEqualTo(Api.TYPE, type);
        endDateQuery.whereEqualTo(Api.OWNER, user);
        endDateQuery.whereLessThan(Api.DATE,
                DateUtils.getDateWithDateString(endDate, DateUtils.FORMAT_MAIN_TAB));

        AVQuery<Account> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
        query.orderByDescending(Api.DATE);// 按时间，降序排列
        query.include(Api.OWNER);
        if(page != -1){
            query.limit(AppConfig.LIMIT);
            query.skip((page - 1) * AppConfig.LIMIT);
        }
        query.findInBackground(new FindCallback<Account>() {
            @Override
            public void done(List<Account> list, AVException e) {
                if (e == null) {
                    callback.querySuccess(list);
                } else {
                    callback.queryFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void queryDefBookAccounts(final User user, final String startDate, final String endDate
            , final int type, final int page, final boolean isQueryMe, final QueryAccountListCallback callback) {
        queryDefaultBook(user, new QueryDefaultBookCallback() {
            @Override
            public void querySuccess(AccountBook book) {
                long bid = book.getBid();
                final List<User> shares = book.getShares();
                AVQuery<Account> startDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
                if(type != -1)
                    startDateQuery.whereEqualTo(Api.TYPE, type);
                startDateQuery.whereEqualTo(Api.BID, bid);
                if(isQueryMe)
                    startDateQuery.whereEqualTo(Api.OWNER, user);
                startDateQuery.whereGreaterThanOrEqualTo(Api.DATE,
                        DateUtils.getDateWithDateString(startDate, DateUtils.FORMAT_MAIN_TAB));

                AVQuery<Account> endDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
                if(type != -1)
                    endDateQuery.whereEqualTo(Api.TYPE, type);
                endDateQuery.whereEqualTo(Api.BID, bid);
                if(isQueryMe)
                    endDateQuery.whereEqualTo(Api.OWNER, user);
                endDateQuery.whereLessThan(Api.DATE,
                        DateUtils.getDateWithDateString(endDate, DateUtils.FORMAT_MAIN_TAB));

                AVQuery<Account> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
                query.include(Api.OWNER);
                if(page != -1){
                    query.limit(AppConfig.LIMIT);
                    query.skip((page - 1) * AppConfig.LIMIT);
                }
                query.orderByDescending(Api.DATE);// 按时间，降序排列
                query.findInBackground(new FindCallback<Account>() {
                    @Override
                    public void done(List<Account> list, AVException e) {
                        if (e == null) {
                            callback.querySuccess(list);
                            callback.shareUsers(shares.size());
                        } else {
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
    public void queryCountAccounts(final User user, final String startDate, final String endDate
            , final int queryType, final int type, final QueryAccountsCallback callback) {
        if(queryType == 1){// 查询指定用户指定日期内所有账目信息
            queryAccounts(user, startDate, endDate, type, -1, callback);
        }else if(queryType == 2){// 查询指定用户的当前帐薄内所有账目信息
            queryDefBookAccounts(user, startDate, endDate, type, -1, false, new QueryAccountListCallback(){
                @Override
                public void querySuccess(List<Account> list) {
                    callback.querySuccess(list);
                }

                @Override
                public void shareUsers(int count) {
                }

                @Override
                public void queryFail(Error e) {
                    callback.queryFail(e);
                }
            });
        }else if(queryType == 3){// 查询指定用户的当前帐薄内指定用户的账目信息
            queryDefBookAccounts(user, startDate, endDate, type, -1, true, new QueryAccountListCallback(){
                @Override
                public void querySuccess(List<Account> list) {
                    callback.querySuccess(list);
                }

                @Override
                public void shareUsers(int count) {
                }

                @Override
                public void queryFail(Error e) {
                    callback.queryFail(e);
                }
            });
        }
    }

    @Override
    public void queryDefBookTotalMoney(final User user, final String startDate, final String endDate, final QueryBookTotalMoneyCallback callback) {
        long bid = SPUtils.getDefBookBid();
        if (bid != 0) {
            AVQuery<Account> startDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
            startDateQuery.whereEqualTo(Api.BID, bid);
            startDateQuery.whereGreaterThanOrEqualTo(Api.DATE,
                    DateUtils.getDateWithDateString(startDate, DateUtils.FORMAT_MAIN_TAB));

            AVQuery<Account> endDateQuery = new AVQuery<>(Api.TAB_ACCOUNT);
            endDateQuery.whereEqualTo(Api.BID, bid);
            endDateQuery.whereLessThan(Api.DATE,
                    DateUtils.getDateWithDateString(endDate, DateUtils.FORMAT_MAIN_TAB));

            AVQuery<Account> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
            query.include(Api.OWNER);
            query.findInBackground(new FindCallback<Account>() {
                @Override
                public void done(List<Account> list, AVException e) {
                    if (e == null) {
                        double costTotalMoney = 0;
                        double incomeTotalMoney = 0;
                        for (Account account : list) {
                            double money = Double.parseDouble(account.getMoney());
                            if (AppConfig.TYPE_COST == account.getType()) {
                                costTotalMoney = ArithUtils.add(costTotalMoney, money);
                            } else {
                                incomeTotalMoney = ArithUtils.add(incomeTotalMoney, money);
                            }
                        }
                        callback.querySuccess(costTotalMoney, incomeTotalMoney);
                    } else {
                        callback.queryFail(new Error(e));
                    }
                }
            });
        }else{
            // 设置默认帐薄 id
            queryDefaultBook(user, new QueryDefaultBookCallback() {
                @Override
                public void querySuccess(AccountBook book) {
                    queryDefBookTotalMoney(user, startDate, endDate, callback);
                }

                @Override
                public void queryFail(Error e) {
                    callback.queryFail(e);
                }
            });
        }
    }

    @Override
    public void queryDefaultBook(final User user, final AccountDataSource.QueryDefaultBookCallback callback) {
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.OWNER, user);
        query.whereEqualTo(Api.IS_CURRENT, true);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if (e == null) {
                    // 判断是否有默认帐薄
                    if(list != null && list.size() > 0){
                        // 设置默认帐薄 id，并回调默认帐薄数据
                        AccountBook book = list.get(0);
                        long bid = book.getBid();
                        SPUtils.setDefBookBid(bid);
                        callback.querySuccess(book);
                    }else{
                        // 创建默认帐薄
                        createDefaultBook(user, new CreateBookCallback() {
                            @Override
                            public void createSuccess(AccountBook book) {
                                callback.querySuccess(book);
                            }

                            @Override
                            public void createFail(Error e) {
                                callback.queryFail(e);
                            }
                        });
                    }
                } else {
                    callback.queryFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void createDefaultBook(User user, final CreateBookCallback callback) {
        final AccountBook book = new AccountBook();
        book.setOwner(user);
        book.setCurrent(true);
        book.setName(UiUtils.getString(R.string.def_book_name));
        book.setCover(UiUtils.getString(R.string.def_book_cover));
        book.setScene(UiUtils.getString(R.string.def_book_scene));
        book.addShare(user);
        book.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    long bid = book.getBid();
                    SPUtils.setDefBookBid(bid);
                    callback.createSuccess(book);
                } else {
                    callback.createFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void queryBooks(final User user, final QueryBooksCallback callback) {
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.OWNER, user);
        query.include(Api.SHARES);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(final List<AccountBook> list, AVException e) {
                if (e == null) {
                    // 查询每个帐薄的总支出、收入
                    final List<Integer> items = new ArrayList<>();
                    for (final AccountBook book : list) {
                        queryBookTotalMoney(book.getBid(), new QueryBookTotalMoneyCallback() {
                            @Override
                            public void querySuccess(double totalCost, double totalIncome) {
                                synchronized (items){
                                    items.add(0);
                                    book.totalCost = totalCost;
                                    book.totalIncome = totalIncome;
                                    if(list.size() == items.size()){
                                        callback.querySuccess(list);
                                    }
                                }
                            }

                            @Override
                            public void queryFail(Error e) {}
                        });
                    }
                } else {
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
                if (e == null) {
                    if (accountBook == null) {
                        Error error = new Error();
                        error.message = UiUtils.getString(R.string.toast_query_book_empty);
                        callback.requestFail(error);
                    } else {
                        if (accountBook.getShares().contains(user)) {
                            Error error = new Error();
                            error.message = UiUtils.getString(R.string.toast_has_add_share_book);
                            callback.requestFail(error);
                        } else {
                            // 判断是否申请过
                            AVQuery<Msg> queryMsgOwner = AVQuery.getQuery(Msg.class);
                            queryMsgOwner.whereEqualTo(Api.OWNER, accountBook.getOwner());
                            AVQuery<Msg> queryMsgBook = AVQuery.getQuery(Msg.class);
                            queryMsgBook.whereEqualTo(Api.APPLY_BOOK, accountBook);
                            AVQuery<Msg> queryMsg = AVQuery.and(Arrays.asList(queryMsgOwner, queryMsgBook));
                            queryMsg.findInBackground(new FindCallback<Msg>() {
                                @Override
                                public void done(List<Msg> list, AVException e) {
                                    if (e == null) {
                                        if (list != null && list.size() > 0) {
                                            Error error = new Error();
                                            error.message = UiUtils.getString(R.string.toast_has_apply_book);
                                            callback.requestFail(error);
                                        } else {
                                            // 添加一条消息
                                            Msg msg = new Msg();
                                            msg.setApplyUser(user);// 设置申请人为当前用户
                                            msg.setApplyBook(accountBook);// 设置申请帐薄为帐薄 id 对应帐薄
                                            msg.setOwner(accountBook.getOwner());// 设置消息所属人为帐薄所属人
                                            msg.setType(AppConfig.TYPE_MSG_APPLY_BOOK);
                                            msg.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    if (e == null) {
                                                        callback.requestSuccess();
                                                    } else {
                                                        callback.requestFail(new Error(e));
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        callback.requestFail(new Error(e));
                                    }
                                }
                            });
                        }
                    }
                } else {
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
                if (e == null) {
                    callback.requestSuccess();
                } else {
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
                if (e == null) {
                    // 设置帐薄 ID 相同帐薄为当前帐薄
                    for (AccountBook book : list) {
                        if(book.getBid() == bid){
                            long bid = book.getBid();
                            SPUtils.setDefBookBid(bid);
                            book.setCurrent(true);
                        }else{
                            book.setCurrent(false);
                        }
                    }
                    AVObject.saveAllInBackground(list, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                callback.requestSuccess();
                            } else {
                                callback.requestFail(new Error(e));
                            }
                        }
                    });
                } else {
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
                if (e == null) {
                    // 统一修改
                    for (AccountBook accountBook : list) {
                        accountBook.setName(book.getName());
                        accountBook.setScene(book.getScene());
                        accountBook.setCover(book.getCover());
                    }

                    AVObject.saveAllInBackground(list, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                callback.requestSuccess();
                            } else {
                                callback.requestFail(new Error(e));
                            }
                        }
                    });
                } else {
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
                if (e == null) {
                    // 统一删除
                    AVObject.deleteAllInBackground(list, new DeleteCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                callback.requestSuccess();
                            } else {
                                callback.requestFail(new Error(e));
                            }
                        }
                    });
                } else {
                    callback.requestFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void exitBook(final User user, final AccountBook book, final Callback callback) {
        // 查找要退出帐薄 id 所有对应帐薄数据
        AVQuery<AccountBook> query = AVQuery.getQuery(AccountBook.class);
        query.whereEqualTo(Api.BID, book.getBid());
        query.include(Api.SHARES);
        query.findInBackground(new FindCallback<AccountBook>() {
            @Override
            public void done(List<AccountBook> list, AVException e) {
                if (e == null) {
                    // 在所有帐薄的共享用户中移除当前用户
                    for (AccountBook book : list) {
                        List<User> shares = book.getShares();
                        shares.remove(user);
                        book.setShare(shares);
                    }
                    AVObject.saveAllInBackground(list, new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                book.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            callback.requestSuccess();
                                        } else {
                                            callback.requestFail(new Error(e));
                                        }
                                    }
                                });
                            } else {
                                callback.requestFail(new Error(e));
                            }
                        }
                    });
                } else {
                    callback.requestFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void queryBookTotalMoney(long bid, final QueryBookTotalMoneyCallback callback) {
        AVQuery<Account> query = new AVQuery<>(Api.TAB_ACCOUNT);
        query.whereEqualTo(Api.BID, bid);
        query.findInBackground(new FindCallback<Account>() {
            @Override
            public void done(List<Account> list, AVException e) {
                if (e == null) {
                    double costTotalMoney = 0;
                    double incomeTotalMoney = 0;
                    for (Account account : list) {
                        double money = Double.parseDouble(account.getMoney());
                        if (AppConfig.TYPE_COST == account.getType()) {
                            costTotalMoney = ArithUtils.add(costTotalMoney, money);
                        } else {
                            incomeTotalMoney = ArithUtils.add(incomeTotalMoney, money);
                        }
                    }
                    callback.querySuccess(costTotalMoney, incomeTotalMoney);
                } else {
                    callback.queryFail(new Error(e));
                }
            }
        });
    }

}

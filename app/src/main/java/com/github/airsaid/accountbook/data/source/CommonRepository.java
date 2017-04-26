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
import com.github.airsaid.accountbook.data.AboutApp;
import com.github.airsaid.accountbook.data.AccountBook;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Msg;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;
import com.github.airsaid.accountbook.util.UiUtils;

import java.util.List;

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
                if (e == null) {
                    callback.getSuccess(aboutApp);
                } else {
                    callback.getFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void queryMsgList(User user, final QueryMsgListCallback callback) {
        AVQuery<Msg> query = AVQuery.getQuery(Msg.class);
        query.whereEqualTo(Api.OWNER, user);
        query.include(Api.APPLY_USER);
        query.include(Api.APPLY_BOOK);
        query.orderByDescending(Api.CREATED_AT);// 按创建时间，降序排列
        query.findInBackground(new FindCallback<Msg>() {
            @Override
            public void done(List<Msg> list, AVException e) {
                if (e == null) {
                    // 设置消息为已读
                    for (Msg msg : list) {
                        msg.setRead(true);
                    }
                    AVObject.saveAllInBackground(list);
                    callback.querySuccess(list);
                } else {
                    callback.queryFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void agreeAddBook(final Msg msg, final Callback callback) {
        final AccountBook applyBook = msg.getApplyBook();
        final User applyUser = msg.getApplyUser();

        // 健壮性判断
        if (applyBook.getShares().contains(applyUser)) {
            Error error = new Error();
            error.message = UiUtils.getString(R.string.toast_has_agree);
            callback.requestFail(error);
            return;
        }

        // 共享用户中加入该用户
        applyBook.addShare(applyUser);
        applyBook.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 在该用户中创建一个帐薄，其中除了所属人为该用户外，其他都同要加入的帐薄数据。
                    AccountBook book = new AccountBook();
                    book.setOwner(applyUser);
                    book.setBid(applyBook.getBid());
                    book.setName(applyBook.getName());
                    book.setScene(applyBook.getScene());
                    book.setShare(applyBook.getShares());
                    book.setCover(applyBook.getCover());
                    AccountRepository repository = new AccountRepository();
                    repository.addBook(book, new Callback() {
                        @Override
                        public void requestSuccess() {
                            // 删除该消息
                            deleteMessage(msg, new Callback() {
                                @Override
                                public void requestSuccess() {
                                    // 创建通知消息
                                    Msg message = new Msg();
                                    message.setOwner(applyUser);
                                    message.setType(AppConfig.TYPE_MSG_SYSTEM);
                                    String content = UiUtils.getString(R.string.msg_agree_book_apply);
                                    content = String.format(content, applyBook.getName());
                                    message.setContent(content);
                                    saveMessage(message, new Callback() {
                                        @Override
                                        public void requestSuccess() {
                                            callback.requestSuccess();
                                        }

                                        @Override
                                        public void requestFail(Error e) {
                                            callback.requestFail(e);
                                        }
                                    });
                                }

                                @Override
                                public void requestFail(Error e) {
                                    callback.requestFail(e);
                                }
                            });
                        }

                        @Override
                        public void requestFail(Error e) {
                            callback.requestFail(e);
                        }
                    });
                } else {
                    callback.requestFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void refuseAddBook(final Msg msg, final Callback callback) {
        AccountBook applyBook = msg.getApplyBook();
        User applyUser = msg.getApplyUser();
        // 创建拒绝消息
        Msg message = new Msg();
        message.setOwner(applyUser);
        message.setType(AppConfig.TYPE_MSG_SYSTEM);
        String content = UiUtils.getString(R.string.msg_refuse_book_apply);
        content = String.format(content, applyBook != null ? applyBook.getBid() : UiUtils.getString(R.string.booK_has_delete));
        message.setContent(content);
        saveMessage(message, new Callback() {
            @Override
            public void requestSuccess() {
                // 删除该申请消息
                deleteMessage(msg, new Callback() {
                    @Override
                    public void requestSuccess() {
                        callback.requestSuccess();
                    }

                    @Override
                    public void requestFail(Error e) {
                        callback.requestFail(e);
                    }
                });
            }

            @Override
            public void requestFail(Error e) {
                callback.requestFail(e);
            }
        });
    }

    @Override
    public void queryUnReadMsg(User user, final QueryUnReadMsgCallback callback) {
        AVQuery<Msg> query = AVQuery.getQuery(Msg.class);
        query.whereEqualTo(Api.OWNER, user);
        query.findInBackground(new FindCallback<Msg>() {
            @Override
            public void done(List<Msg> list, AVException e) {
                if (e == null) {
                    int count = 0;
                    for (Msg msg : list) {
                        if (!msg.isRead()) {
                            count++;
                        }
                    }
                    callback.querySuccess(count);
                } else {
                    callback.queryFail(new Error(e));
                }
            }
        });
    }

    @Override
    public void saveMessage(Msg msg, final Callback callback) {
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

    @Override
    public void deleteMessage(Msg msg, final Callback callback) {
        msg.deleteInBackground(new DeleteCallback() {
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
}

package com.github.airsaid.accountbook.data.source;

import com.github.airsaid.accountbook.data.AboutApp;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Msg;
import com.github.airsaid.accountbook.data.User;
import com.github.airsaid.accountbook.data.i.Callback;

import java.util.List;

/**
 * @author Airsaid
 * @Date 2017/4/24 21:43
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public interface CommonDataSource {

    /** 关于 APP */
    void aboutApp(GetAboutAppInfoCallback callback);

    /** 查询指定用户的消息列表 */
    void queryMsgList(User user, QueryMsgListCallback callback);

    /** 同意加入帐薄 */
    void agreeAddBook(Msg msg, Callback callback);

    /** 拒绝申请 */
    void refuseAddBook(Msg msg, Callback callback);

    /** 查询是否有未读消息 */
    void queryUnReadMsg(User user, QueryUnReadMsgCallback callback);

    /** 保存消息 */
    void saveMessage(Msg msg, Callback callback);

    /** 删除消息 */
    void deleteMessage(Msg msg, Callback callback);

    interface GetAboutAppInfoCallback{
        void getSuccess(AboutApp about);
        void getFail(Error e);
    }

    interface QueryMsgListCallback{
        void querySuccess(List<Msg> list);
        void queryFail(Error e);
    }

    interface QueryUnReadMsgCallback{
        void querySuccess(int count);
        void queryFail(Error e);
    }

}

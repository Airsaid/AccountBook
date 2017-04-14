package com.github.airsaid.accountbook.mvp.books;

import com.github.airsaid.accountbook.base.BasePresenter;
import com.github.airsaid.accountbook.base.BaseView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/14
 * @desc 帐薄契约类
 */
public interface AccountBooksContract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter{

    }

}

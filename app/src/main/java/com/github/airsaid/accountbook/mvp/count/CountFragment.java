package com.github.airsaid.accountbook.mvp.count;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.LogUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UserUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/12
 * @desc 统计 Fragment
 */
public class CountFragment extends BaseFragment implements CountContract.View {

    @BindView(R.id.ibt_left)
    ImageButton mIbtLeft;
    @BindView(R.id.txt_month)
    TextView mTxtMonth;
    @BindView(R.id.ibt_right)
    ImageButton mIbtRight;
    @BindView(R.id.chartView)
    PieChartView mChartView;
    @BindView(R.id.llt_empty)
    LinearLayout mLltEmpty;

    private HashMap<String, Double> mCostMoneyMap = new HashMap<>();
    private HashMap<String, String> mCostIconMap = new HashMap<>();
    private CountContract.Presenter mPresenter;
    private String mStartDate;
    private String mEndDate;

    @Override
    public void setPresenter(CountContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    public static CountFragment newInstance(Bundle args) {
        CountFragment fragment = new CountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_count, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        initChart();
        initData();
        setChartData();
    }

    private void initChart() {
        mChartView.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, SliceValue sliceValue) {
                Log.e("test", "onValueSelected  i: " + i);
            }

            @Override
            public void onValueDeselected() {
                Log.e("test", "onValueDeselected");
            }
        });
    }

    private void setChartData() {
        if (mCostMoneyMap.size() > 0) {
            mLltEmpty.setVisibility(View.GONE);
            mChartView.setVisibility(View.VISIBLE);
            List<SliceValue> values = new ArrayList<>();
            for (Map.Entry<String, Double> entry : mCostMoneyMap.entrySet()) {
                String value = String.valueOf(entry.getValue());
                SliceValue sliceValue = new SliceValue(Float.valueOf(value), ChartUtils.pickColor());
                values.add(sliceValue);
            }

            PieChartData data = new PieChartData(values);
            data.setHasLabels(true);// 是否显示标签
            data.setHasLabelsOnlyForSelected(false);// 是否当选中后才显示标签
            data.setHasLabelsOutside(false);// 是否显示外标签
            data.setHasCenterCircle(false);// 是否有中心圆
            data.setSlicesSpacing(1);// 设置间距
//        data.setCenterText1("Hello!");// 设置中心文字
//        data.setCenterText2("Charts (Roboto Italic)");// 是否有第二个中心文字
            mChartView.setCircleFillRatio(0.9f);// 设置填充率（大小）
            mChartView.setValueSelectionEnabled(true);// 设置值选择模式
            mChartView.setPieChartData(data);
            for (SliceValue value : data.getValues()) {
                value.setTarget(value.getValue());
            }
            mChartView.startDataAnimation();
        }else{
            // 设置空布局
            mLltEmpty.setVisibility(View.VISIBLE);
            mChartView.setVisibility(View.GONE);
        }
    }

    private void initData() {
        mStartDate = DateUtils.getCurrentDate(DateUtils.FORMAT_MAIN_TAB);
        mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
        queryAccounts(mStartDate, mEndDate);
    }

    @OnClick({R.id.ibt_left, R.id.ibt_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibt_left:
                mStartDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, -1);
                mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
                queryAccounts(mStartDate, mEndDate);
                break;
            case R.id.ibt_right:
                mStartDate = mEndDate;
                mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
                queryAccounts(mStartDate, mEndDate);
                break;
        }
    }


    @Override
    public void queryAccounts(String startDate, String endDate) {
        LogUtils.e("test", "startDate: " + startDate);
        LogUtils.e("test", "endDate: " + endDate);

        SimpleDateFormat f = new SimpleDateFormat(DateUtils.FORMAT_MAIN_TAB, Locale.CHINA);
        try {
            Date date = f.parse(startDate);
            String text = f.format(date);
            mTxtMonth.setText(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ProgressUtils.show(mContext);
        mPresenter.queryAccounts(UserUtils.getUser(), mStartDate, mEndDate);
    }

    @Override
    public void queryAccountsSuccess(List<Account> accounts) {
        ProgressUtils.dismiss();
        mCostMoneyMap.clear();
        mCostIconMap.clear();
        LogUtils.e("test", "queryAccountsSuccess accounts: " + accounts);

        if (accounts != null && accounts.size() > 0) {
            for (Account account : accounts) {
                // 判断收入/支出类型
                if (account.getType() == AppConfig.TYPE_COST) {
                    String type = account.getCType();
                    String icon = account.getTypeIcon();
                    double money = Double.parseDouble(account.getMoney());

                    if(TextUtils.isEmpty(type)){

                    }


                    // 如果已经存在该分类，累加金额
                    LogUtils.e("test", "type: " + type);
                    LogUtils.e("test", "money: " + money);
                    if (mCostMoneyMap.containsKey(type)) {
                        double resultMoney = ArithUtils.add(mCostMoneyMap.get(type), money);
                        LogUtils.e("test", "resultMoney: " + resultMoney);
                        mCostMoneyMap.put(type, resultMoney);
                    } else {
                        mCostMoneyMap.put(type, money);
                    }
                } else {

                }
            }

            LogUtils.e("test", "=========================================");

            for (Map.Entry<String, Double> entry : mCostMoneyMap.entrySet()) {
                LogUtils.e("test", "key: " + entry.getKey());
                LogUtils.e("test", "value: " + entry.getValue());
            }
        }

        setChartData();
    }

    @Override
    public void queryAccountsFail(Error e) {
        ProgressUtils.dismiss();
        mCostMoneyMap.clear();
        mCostIconMap.clear();
        setChartData();
        ToastUtils.show(mContext, e.getMessage());
    }
}

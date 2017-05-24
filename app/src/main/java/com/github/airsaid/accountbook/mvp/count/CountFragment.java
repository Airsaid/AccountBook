package com.github.airsaid.accountbook.mvp.count;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.CountListAdapter;
import com.github.airsaid.accountbook.base.BaseApplication;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.CountList;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.data.TypeDao;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.ChartUtils;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.PaletteUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.ChartFormatter;
import com.github.airsaid.accountbook.widget.recycler.HorizontalDividerItemDecoration;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/12
 * @desc 统计 Fragment
 */
public class CountFragment extends BaseFragment implements CountContract.View
        , SwipeRefreshLayout.OnRefreshListener, ChartFormatter.OnFormattedFinishListener {

    /** 统计当前用户所有账目 */
    public static final int QUERY_TYPE_ALL_ME = 1;
    /** 统计当前用户的当前帐薄中所有账目 */
    public static final int QUERY_TYPE_BOOK_ALL = 2;
    /** 统计当前用户的当前帐薄中当前用户所有账目 */
    public static final int QUERY_TYPE_BOOK_ME = 3;

    @BindView(R.id.ibt_left)
    ImageButton mIbtLeft;
    @BindView(R.id.txt_month)
    TextView mTxtMonth;
    @BindView(R.id.ibt_right)
    ImageButton mIbtRight;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.pieChartView)
    PieChart mChartView;

    private CountListAdapter mAdapter;
    private CountContract.Presenter mPresenter;

    // 分类对应总金额集合
    private HashMap<String, Double> mMoneyMap = new HashMap<>();
    // 分类对应图标集合
    private HashMap<String, String> mIconMap = new HashMap<>();
    // 分类对应百分比集合
    private HashMap<String, Float> mPercentMap = new HashMap<>();
    // 分类对应颜色值
    private HashMap<String, Integer> mColorMap = new HashMap<>();
    // 记账类型数据
    private List<Type> mTypes;
    // 统计类型（支出/收入），默认支出
    private int mCountType = AppConfig.TYPE_COST;
    // 查询类型，默认查询当前用户所有账目
    private int mQueryType = QUERY_TYPE_ALL_ME;

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
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(this);
        initTypeData();
        initAdapter();
        initChart();
        initData();
    }

    /**
     * 初始化分类数据
     */
    private void initTypeData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCountType = bundle.getInt(AppConstants.EXTRA_TYPE, AppConfig.TYPE_COST);
        }

        TypeDao dao = BaseApplication.getInstance().getSession().getTypeDao();
        mTypes = new ArrayList<>();
        if (mCountType == AppConfig.TYPE_COST) {
            mTypes = dao.queryBuilder()
                    .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(AppConfig.TYPE_COST))
                    .orderAsc(TypeDao.Properties.Index)
                    .list();
        } else {
            mTypes = dao.queryBuilder()
                    .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(AppConfig.TYPE_INCOME))
                    .orderAsc(TypeDao.Properties.Index)
                    .list();
        }
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(R.color.colorDivide)
                .size(2)
                .build());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CountListAdapter(R.layout.item_count_list, new ArrayList<CountList>());
        mAdapter.setEmptyView(UiUtils.getEmptyView(mContext, mRecyclerView
                , UiUtils.getString(R.string.empty_count_data), R.mipmap.ic_pie_empty));
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化图表
     */
    private void initChart() {
        ChartUtils.initPieChart(mChartView);
        mChartView.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // 单独显示每个分类的统计情况
                PieEntry entry = (PieEntry) e;
                float money = entry.getValue();
                String type = entry.getLabel();
                mChartView.setCenterText(generateCenterSpannableText(
                        String.valueOf(money), "(".concat(type).concat(")")));
            }

            @Override
            public void onNothingSelected() {
                setChartData();
            }
        });
    }

    /**
     * 初始化数据，默认为系统时间当前月
     */
    private void initData() {
        mStartDate = DateUtils.getCurrentDate(DateUtils.FORMAT_MAIN_TAB);
        mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if(mRefreshLayout == null) return;

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                clearData();
                mRefreshLayout.setRefreshing(true);
                queryAccounts(mStartDate, mEndDate, mQueryType, mCountType);
            }
        });
    }

    /**
     * 清空数据
     */
    private void clearData() {
        mMoneyMap.clear();
        mIconMap.clear();
        mPercentMap.clear();
        mColorMap.clear();
        setChartData();
        setCountListData();
    }

    @Override
    public void queryAccounts(String startDate, String endDate, int queryType, int type) {
        SimpleDateFormat f = new SimpleDateFormat(DateUtils.FORMAT_MAIN_TAB, Locale.CHINA);
        try {
            Date date = f.parse(startDate);
            String text = f.format(date);
            mTxtMonth.setText(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mPresenter.queryAccounts(UserUtils.getUser(), mStartDate, mEndDate, queryType, type);
    }

    @Override
    public void queryAccountsSuccess(List<Account> accounts) {
        mRefreshLayout.setRefreshing(false);
        mMoneyMap.clear();
        mIconMap.clear();
        mColorMap.clear();
        if (accounts != null && accounts.size() > 0) {
            for (Account account : accounts) {
                String type = account.getCType();
                String icon = account.getTypeIcon();
                double money = Double.parseDouble(account.getMoney());

                // 判断 typeIcon 字段是否为空
                if (!TextUtils.isEmpty(icon)) {
                    // 保存图片名称
                    mIconMap.put(type, icon);
                } else {
                    // 兼容旧版本
                    for (Type costType : mTypes) {
                        if (costType.getName().equals(type)) {
                            mIconMap.put(type, costType.getIcon());
                            break;
                        }
                    }
                }

                // 如果已经存在该分类，累加金额
                if (mMoneyMap.containsKey(type)) {
                    double resultMoney = ArithUtils.add(mMoneyMap.get(type), money);
                    mMoneyMap.put(type, resultMoney);
                } else {
                    mMoneyMap.put(type, money);
                }
            }
        }

        setChartData();
    }

    @Override
    public void queryAccountsFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
        mRefreshLayout.setRefreshing(false);
    }

    /**
     * 设置图表数据
     */
    @Override
    public void setChartData() {
        if (mMoneyMap.size() > 0) {
            mChartView.setVisibility(View.VISIBLE);
            // 设置总支出/收入
            String typeStr = mCountType == AppConfig.TYPE_COST
                    ? UiUtils.getString(R.string.total_cost_chart)
                    : UiUtils.getString(R.string.total_income_chart);
            mChartView.setCenterText(generateCenterSpannableText(
                    String.valueOf(getTotalMoney()), typeStr));
            // 设置图表数据与颜色
            ArrayList<PieEntry> entries = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();
            for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
                // 添加分类、金额数据
                Float money = Float.valueOf(String.valueOf(entry.getValue()));
                String type = entry.getKey();
                PieEntry pieEntry = new PieEntry(money, type);
                entries.add(pieEntry);
                // 添加从分类图片中取色的颜色
                String iconName = mIconMap.get(entry.getKey());
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), UiUtils.getImageResIdByName(iconName));
                int colorRgb = PaletteUtils.getColorRgb(bitmap);
                mColorMap.put(type,colorRgb);
                colors.add(colorRgb);
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            ChartFormatter formatter = new ChartFormatter(mPercentMap, mMoneyMap.size());
            formatter.setOnFormattedValueListener(this);
            data.setValueFormatter(formatter);
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            mChartView.setData(data);
            mChartView.animateX(800);
        }else{
            mChartView.setVisibility(View.GONE);
        }
    }

    /**
     * 生成图标中心文字的 SpannableString
     *
     * @return SpannableString
     */
    private SpannableString generateCenterSpannableText(String money, String descText) {
        SpannableString s = new SpannableString(UiUtils.getString(R.string.rmb)
                .concat(money).concat("\n").concat(descText));
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length() - descText.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length() - descText.length(), 0);
        s.setSpan(new ForegroundColorSpan(UiUtils.getColor(R.color.textRed))
                , 0, s.length() - descText.length(), 0);
        s.setSpan(new RelativeSizeSpan(.9f), s.length() - descText.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - descText.length(), s.length(), 0);
        s.setSpan(new ForegroundColorSpan(UiUtils.getColor(R.color.textGrayish))
                , s.length() - descText.length(), s.length(), 0);
        return s;
    }

    @Override
    public void onFormattedValue(HashMap<String, Float> percentMap) {
        setCountListData();
    }

    /**
     * 设置统计列表数据
     */
    private void setCountListData() {
        List<CountList> list = new ArrayList<>();
        for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
            String type = entry.getKey();
            Double money = entry.getValue();
            String icon = mIconMap.get(type);
            Float percent = mPercentMap.get(type);
            Integer color = mColorMap.get(type);

            CountList count = new CountList();
            count.setTypeName(type)
                    .setTotalMoney(money)
                    .setIconName(icon)
                    .setColor(color)
                    .setPercent(percent);

            list.add(count);
        }
        mAdapter.setNewData(sortCountList(list));
    }

    /**
     * 对统计列表按照金额大小进行倒序排序
     *
     * @param list 统计列表数据
     * @return 排序后的统计列表数据
     */
    private List<CountList> sortCountList(List<CountList> list) {
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<CountList>() {
                @Override
                public int compare(CountList t2, CountList t1) {
                    double money1 = t1.getTotalMoney();
                    double money2 = t2.getTotalMoney();
                    if (money1 > money2) {
                        return 1;
                    } else if (money1 == money2) {
                        return 0;
                    }
                    return -1;
                }
            });
        }
        return list;
    }

    @Override
    public double getTotalMoney() {
        Double totalMoney = 0.0;
        for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
            totalMoney = ArithUtils.add(totalMoney, entry.getValue());
        }
        return totalMoney;
    }

    @Override
    public void setQueryType(int type) {
        mQueryType = type;
        if(isVisible())
            onRefresh();
    }

    @OnClick({R.id.ibt_left, R.id.ibt_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibt_left: // 请求上月数据
                mStartDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, -1);
                mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
                onRefresh();
                break;
            case R.id.ibt_right:// 请求下月数据
                mStartDate = mEndDate;
                mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
                onRefresh();
                break;
        }
    }
}

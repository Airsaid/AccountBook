package com.github.airsaid.accountbook.mvp.count;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.LogUtils;
import com.github.airsaid.accountbook.util.PaletteUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.github.airsaid.accountbook.widget.recycler.HorizontalDividerItemDecoration;

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
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
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
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private View mHeadView;
    private PieChartView mChartView;

    // 记账类型数据
    private List<Type> mCostTypes;
    private List<Type> mIncomeTypes;

    // 分类对应总金额集合
    private HashMap<String, Double> mMoneyMap = new HashMap<>();
    // 分类对应图标集合
    private HashMap<String, String> mIconMap = new HashMap<>();

    private CountContract.Presenter mPresenter;
    private String mStartDate;
    private String mEndDate;

    // 统计类型（支出/收入），默认支出
    private int mCountType = AppConfig.TYPE_COST;


    private CountListAdapter mAdapter;

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
        initTypeData();
        initHeadView();
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
        if (mCountType == AppConfig.TYPE_COST) {
            mCostTypes = new ArrayList<>();
            mCostTypes = dao.queryBuilder()
                    .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(AppConfig.TYPE_COST))
                    .orderAsc(TypeDao.Properties.Index)
                    .list();
        } else {
            mIncomeTypes = new ArrayList<>();
            mIncomeTypes = dao.queryBuilder()
                    .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(AppConfig.TYPE_INCOME))
                    .orderAsc(TypeDao.Properties.Index)
                    .list();
        }
    }

    /**
     * 初始化头布局
     */
    private void initHeadView() {
        mHeadView = mLayoutInflater.inflate(R.layout.rlv_header_count
                , (ViewGroup) mRecyclerView.getParent(), false);
        mChartView = (PieChartView) mHeadView.findViewById(R.id.pieChartView);
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(R.color.colorDivide)
                .showLastDivider()
                .build());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CountListAdapter(R.layout.item_count_list, new ArrayList<CountList>());
        mAdapter.addHeaderView(mHeadView);
        mAdapter.setEmptyView(UiUtils.getEmptyView(mContext, mRecyclerView
                , UiUtils.getString(R.string.empty_count_data), R.mipmap.ic_pie_empty));
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 初始化图表
     */
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

    /** 初始化数据，默认为系统时间当前月 */
    private void initData() {
        mStartDate = DateUtils.getCurrentDate(DateUtils.FORMAT_MAIN_TAB);
        mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
        queryAccounts(mStartDate, mEndDate, mCountType);
    }

    @Override
    public void queryAccounts(String startDate, String endDate, int type) {
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
        mPresenter.queryAccounts(UserUtils.getUser(), mStartDate, mEndDate, type);
    }

    @Override
    public void queryAccountsSuccess(List<Account> accounts) {
        LogUtils.e("test", "queryAccountsSuccess accounts: " + accounts);
        ProgressUtils.dismiss();
        mMoneyMap.clear();
        mIconMap.clear();
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
                    for (Type costType : mCostTypes) {
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

        LogUtils.e("test", "=========================================");

        for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
            LogUtils.e("test", "key: " + entry.getKey());
            LogUtils.e("test", "value: " + entry.getValue());
        }

        LogUtils.e("test", "=========================================");

        for (Map.Entry<String, String> entry : mIconMap.entrySet()) {
            LogUtils.e("test", "key: " + entry.getKey());
            LogUtils.e("test", "value: " + entry.getValue());
        }

        setChartData();
        setCountListData();
    }

    @Override
    public void queryAccountsFail(Error e) {
        ProgressUtils.dismiss();
        mMoneyMap.clear();
        mIconMap.clear();
        setChartData();
        ToastUtils.show(mContext, e.getMessage());
    }

    /**
     * 设置图表数据
     */
    private void setChartData() {
        if (mMoneyMap.size() > 0) {
            List<SliceValue> values = new ArrayList<>();
            for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
                String value = String.valueOf(entry.getValue());
                String iconName = mIconMap.get(entry.getKey());
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), UiUtils.getImageResIdByName(iconName));
                int colorRgb = PaletteUtils.getColorRgb(bitmap);
                SliceValue sliceValue = new SliceValue(Float.valueOf(value), colorRgb);
                values.add(sliceValue);
            }

            PieChartData data = new PieChartData(values);
            data.setHasLabels(true);// 是否显示标签
            data.setHasLabelsOnlyForSelected(false);// 是否当选中后才显示标签
            data.setHasLabelsOutside(true);// 是否显示外标签
            data.setHasCenterCircle(true);// 是否有中心圆
            data.setSlicesSpacing(1);// 设置间距
            // 设置中心文字
            Typeface typeface = Typeface.defaultFromStyle(Typeface.BOLD);
            data.setCenterText1(UiUtils.getString(R.string.rmb)
                    .concat(String.valueOf(getTotalCostMoney())));
            data.setCenterText1Color(UiUtils.getColor(R.color.textRed));
            data.setCenterText1Typeface(typeface);
            data.setCenterText1FontSize(24);

            data.setCenterText2Color(UiUtils.getColor(R.color.textGrayish));
            data.setCenterText2("(总支出)");
            data.setCenterText2FontSize(14);

            mChartView.setCircleFillRatio(0.8f);// 设置填充率（大小）
            mChartView.setValueSelectionEnabled(true);// 设置值选择模式
            mChartView.setPieChartData(data);
            for (SliceValue value : data.getValues()) {
                value.setTarget(value.getValue());
            }
            mChartView.startDataAnimation();
        }
    }

    /**
     * 设置统计列表数据
     */
    private void setCountListData(){
        List<CountList> list = new ArrayList<>();
        for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
            String type = entry.getKey();
            Double money = entry.getValue();
            String icon = mIconMap.get(type);

            CountList count = new CountList();
            count.setTypeName(type)
                    .setTotalMoney(money)
                    .setIconName(icon)
                    .setPercent(10f);

            list.add(count);
        }
        mAdapter.setNewData(sortCountList(list));
    }

    /**
     * 对统计列表按照金额大小进行倒序排序
     * @param list 统计列表数据
     * @return 排序后的统计列表数据
     */
    private List<CountList> sortCountList(List<CountList> list){
        if(list.size() > 0){
            Collections.sort(list, new Comparator<CountList>() {
                @Override
                public int compare(CountList t2, CountList t1) {
                    double money1 = t1.getTotalMoney();
                    double money2 = t2.getTotalMoney();
                    if(money1 > money2){
                        return 1;
                    }else if(money1 == money2){
                        return 0;
                    }
                    return -1;
                }
            });
        }
        return list;
    }

    @Override
    public double getTotalCostMoney() {
        Double totalMoney = 0.0;
        for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
            totalMoney = ArithUtils.add(totalMoney, entry.getValue());
        }
        return totalMoney;
    }

    @Override
    public double getTotalIncomeMoney() {
        Double totalMoney = 0.0;
        for (Map.Entry<String, Double> entry : mMoneyMap.entrySet()) {
            totalMoney = ArithUtils.add(totalMoney, entry.getValue());
        }
        return totalMoney;
    }

    @OnClick({R.id.ibt_left, R.id.ibt_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibt_left: // 请求上月数据
                mStartDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, -1);
                mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
                queryAccounts(mStartDate, mEndDate, mCountType);
                break;
            case R.id.ibt_right:// 请求下月数据
                mStartDate = mEndDate;
                mEndDate = DateUtils.getDateNxtMonth(mStartDate, DateUtils.FORMAT_MAIN_TAB, 1);
                queryAccounts(mStartDate, mEndDate, mCountType);
                break;
        }
    }
}

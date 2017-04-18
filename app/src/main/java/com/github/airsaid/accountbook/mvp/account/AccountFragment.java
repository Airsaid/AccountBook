package com.github.airsaid.accountbook.mvp.account;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.adapter.AccountTypeAdapter;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.MsgConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.AccountType;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.util.AnimUtils;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.RegexUtils;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;
import com.github.airsaid.accountbook.util.UserUtils;
import com.sbgapps.simplenumberpicker.decimal.DecimalPickerDialog;
import com.sbgapps.simplenumberpicker.decimal.DecimalPickerHandler;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/1
 * @desc
 */
public class AccountFragment extends BaseFragment implements AccountContract.View , DecimalPickerHandler {

    @BindView(R.id.txt_type)
    TextView mTxtType;
    @BindView(R.id.txt_money)
    TextView mTxtMoney;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.txt_date)
    TextView mTxtDate;
    @BindView(R.id.edt_note)
    EditText mEdtNote;

    private AccountContract.Presenter mPresenter;

    // 记账类型数据
    private List<AccountType> mTypes = new ArrayList<>();
    private AccountTypeAdapter mTypeAdapter;
    private Account mAccount;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        initAdapter();
        initData();
    }

    /**
     * 初始化 Adapter
     */
    private void initAdapter() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
        mTypeAdapter = new AccountTypeAdapter(R.layout.item_account_type, mTypes);
        mRecyclerView.setAdapter(mTypeAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                setAccountType(position);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化账目对象
        mAccount = new Account();
        mAccount.setType(AppConfig.TYPE_COST); // 默认支出分类
        // 初始化分类数据
        initTypeData();
        // 设置默认日期
        Calendar calendar = Calendar.getInstance();
        setSelectData(calendar);
    }

    /**
     * 初始化分类数据
     */
    private void initTypeData() {
        mTypes.clear();
        String[] types;
        if(mAccount.getType() == AppConfig.TYPE_COST){
            types = getResources().getStringArray(R.array.account_cost_type);
        }else{
            types = getResources().getStringArray(R.array.account_income_type);
        }
        for (int i = 0; i < types.length; i++) {
            AccountType type = new AccountType();
            int resId;
            if(mAccount.getType() == AppConfig.TYPE_COST){
                resId = getResources().getIdentifier("ic_cost_type_".concat(String.valueOf(i)), "mipmap"
                        , getActivity().getPackageName());
            }else{
                resId = getResources().getIdentifier("ic_income_type_".concat(String.valueOf(i)), "mipmap"
                        , getActivity().getPackageName());
            }
            type.drawable = UiUtils.getDrawable(resId);
            type.type = types[i];
            mTypes.add(type);
        }
        mTypeAdapter.setNewData(mTypes);
        // 设置默认分类选中
        setAccountType(0);
    }

    /**
     * 设置消费或收入分类
     * @param position 选中的位置
     */
    private void setAccountType(int position) {
        AccountType type = mTypeAdapter.getData().get(position);
        UiUtils.setCompoundDrawables(mTxtType, type.drawable, null, null, null);
        mTxtType.setText(type.type);
        mAccount.setCType(type.type);
    }

    @Override
    public void setPresenter(AccountContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 保存账目数据
     */
    @Override
    public void save() {
        // 获取输入金额
        String money = mTxtMoney.getText().toString();
        // 获取输入备注
        String note = mEdtNote.getText().toString();
        if(!RegexUtils.checkMoney(money)){
            AnimUtils.startVibrateAnim(mTxtMoney, -1);
        }else{
            mAccount.setMoney(money);
            mAccount.setNote(note);
            mPresenter.saveAccount(UserUtils.getUser(), mAccount);
        }
    }

    @Override
    public void saveSuccess() {
        Message msg = new Message();
        msg.what = MsgConstants.MSG_SAVE_ACCOUNT_SUCCESS;
        EventBus.getDefault().post(msg);

        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_save_success));
        finish();
    }

    @Override
    public void saveFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    /**
     * 选择了支出
     */
    @Override
    public void selectCost() {
        mAccount.setType(AppConfig.TYPE_COST);
        initTypeData();
        mTxtMoney.setTextColor(UiUtils.getColor(R.color.textPink));
    }

    /**
     * 选择了收入
     */
    @Override
    public void selectIncome() {
        mAccount.setType(AppConfig.TYPE_INCOME);
        initTypeData();
        mTxtMoney.setTextColor(UiUtils.getColor(R.color.textLightBlue));
    }

    /**
     * 显示选择日期弹框
     */
    @Override
    public void showSelectDateDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                setSelectData(calendar);
            }
            // 设置初始日期
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 显示输入金额弹框
     */
    @Override
    public void showInputMoneyDialog() {
        new DecimalPickerDialog.Builder()
                .setReference(1)
                .setNatural(false)
                .setRelative(false)
                .setTheme(R.style.DecimalPickerTheme)
                .create()
                .show(getChildFragmentManager(), "TAG_DEC_DIALOG");
    }

    @Override
    public void onDecimalNumberPicked(int i, float v) {
        Double money = Double.valueOf(Float.toString(v));
        String moneyStr = ArithUtils.formatMoney(money);
        if(RegexUtils.checkMoney(moneyStr)){
            mTxtMoney.setText(moneyStr);
        }else{
            AnimUtils.startVibrateAnim(mTxtMoney, -1);
        }
    }

    /**
     * 设置选择的日期
     */
    private void setSelectData(Calendar calendar) {
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        String time = format.format(date);
        mTxtDate.setText(time);
        mAccount.setDate(date);
    }

    @OnClick({R.id.txt_money, R.id.txt_date})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_money:
                showInputMoneyDialog();
                break;
            case R.id.txt_date:
                showSelectDateDialog();
                break;
        }
    }

}

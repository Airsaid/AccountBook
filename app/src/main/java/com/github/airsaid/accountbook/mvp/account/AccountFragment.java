package com.github.airsaid.accountbook.mvp.account;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.github.airsaid.accountbook.base.BaseApplication;
import com.github.airsaid.accountbook.base.BaseFragment;
import com.github.airsaid.accountbook.constants.AppConfig;
import com.github.airsaid.accountbook.constants.AppConstants;
import com.github.airsaid.accountbook.constants.MsgConstants;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.data.Error;
import com.github.airsaid.accountbook.data.Type;
import com.github.airsaid.accountbook.data.TypeDao;
import com.github.airsaid.accountbook.ui.activity.TypeEditActivity;
import com.github.airsaid.accountbook.util.AnimUtils;
import com.github.airsaid.accountbook.util.ArithUtils;
import com.github.airsaid.accountbook.util.DateUtils;
import com.github.airsaid.accountbook.util.ProgressUtils;
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

    public static final int REQUEST_CODE_TYPE = 1;

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
    private List<Type> mCostTypes = new ArrayList<>();
    private List<Type> mIncomeTypes = new ArrayList<>();
    private AccountTypeAdapter mTypeAdapter;
    private Account mAccount;

    private boolean mIsEcho = false; // 是否是回显数据
    private boolean mIsEdit = false; // 是否是编辑账目

    public static AccountFragment newInstance(Bundle args) {
        AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        initAdapter();
        initData();

        Bundle bundle = getArguments();
        if(bundle != null){
            mAccount = bundle.getParcelable(AppConstants.EXTRA_DATA);
            if(mAccount != null){
                mIsEdit = true;
                // 回显数据
                echoData();
            }
        }
    }

    /**
     * 初始化 Adapter
     */
    private void initAdapter() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
        mTypeAdapter = new AccountTypeAdapter(R.layout.item_account_type, mCostTypes);
        mRecyclerView.setAdapter(mTypeAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(position == adapter.getData().size() - 1){// 自定义
                    Intent intent = new Intent(mContext, TypeEditActivity.class);
                    intent.putExtra(AppConstants.EXTRA_ACCOUNT_TYPE, mAccount.getType());
                    startActivityForResult(intent, REQUEST_CODE_TYPE);
                }else{
                    setAccountType(position);
                }
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
        // 默认设置支出分类
        setTypeData();
    }

    /**
     * 初始化分类数据
     */
    private void initTypeData() {
        mCostTypes.clear();
        mIncomeTypes.clear();
        TypeDao dao = BaseApplication.getInstance().getSession().getTypeDao();
        mCostTypes = dao.queryBuilder()
                .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(AppConfig.TYPE_COST))
                .orderAsc(TypeDao.Properties.Index)
                .list();

        mIncomeTypes = dao.queryBuilder()
                .where(TypeDao.Properties.Uid.eq(UserUtils.getUid()), TypeDao.Properties.Type.eq(AppConfig.TYPE_INCOME))
                .orderAsc(TypeDao.Properties.Index)
                .list();
    }

    /**
     * 回显数据
     */
    private void echoData() {
        if(mAccount == null) return;

        // 回显支出、收入分类
        mIsEcho = true;
        AccountActivity act = ((AccountActivity)getActivity());
        act.mType = mAccount.getType();
        act.setCostType();
        // 回显小分类
        String cType = mAccount.getCType();
        mTxtType.setText(cType);
        // 回显分类图标
        String typeIcon = mAccount.getTypeIcon();
        if(!TextUtils.isEmpty(typeIcon)){
            Drawable image = UiUtils.getDrawable(UiUtils.getImageResIdByName(typeIcon));
            UiUtils.setCompoundDrawables(mTxtType, image, null, null, null);
        }else{
            // 由于旧版本账目信息中并无 typeIcon 字段，所以这里要兼容旧版本
            for (Type type : mTypeAdapter.getData()) {
                if(type.getName().equals(cType)){
                    Drawable image = UiUtils.getDrawable(UiUtils.getImageResIdByName(type.getIcon()));
                    UiUtils.setCompoundDrawables(mTxtType, image, null, null, null);
                    break;
                }
            }
        }
        // 回显金额
        mTxtMoney.setText(mAccount.getMoney());
        // 回显日期
        mTxtDate.setText(mAccount.getDateFormat(DateUtils.FORMAT_MONTH_DAY));
        // 回显备注
        mEdtNote.setText(mAccount.getNote());
        mIsEcho = false;
    }

    /**
     * 设置分类数据
     */
    private void setTypeData(){
        if(mAccount == null) return;

        if(mAccount.getType() == AppConfig.TYPE_COST){
            mTypeAdapter.setNewData(mCostTypes);
        }else{
            mTypeAdapter.setNewData(mIncomeTypes);
        }
        // 判断不回显时才去设置默认分类选中
        if(!mIsEcho)
            setAccountType(0);
    }

    /**
     * 设置消费或收入分类
     * @param position 选中的位置
     */
    private void setAccountType(int position) {
        Type type = mTypeAdapter.getData().get(position);
        Drawable image = UiUtils.getDrawable(UiUtils.getImageResIdByName(type.getIcon()));
        UiUtils.setCompoundDrawables(mTxtType, image, null, null, null);
        mTxtType.setText(type.getName());
        mAccount.setCType(type.getName());
        mAccount.setTypeIcon(type.getIcon());
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
            ProgressUtils.show(mContext);
            if(!mIsEdit){
                mPresenter.saveAccount(UserUtils.getUser(), mAccount);
            }else{
                mPresenter.saveAccount(mAccount.getOwner(), mAccount);
            }
        }
    }

    @Override
    public void saveSuccess() {
        ProgressUtils.dismiss();
        Message msg = new Message();
        msg.what = MsgConstants.MSG_SAVE_ACCOUNT_SUCCESS;
        EventBus.getDefault().post(msg);

        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_save_success));
        finish();
    }

    @Override
    public void saveFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    /**
     * 选择了支出
     */
    @Override
    public void selectCost() {
        mTxtMoney.setTextColor(UiUtils.getColor(R.color.textPink));
        mAccount.setType(AppConfig.TYPE_COST);
        setTypeData();
    }

    /**
     * 选择了收入
     */
    @Override
    public void selectIncome() {
        mTxtMoney.setTextColor(UiUtils.getColor(R.color.textLightBlue));
        mAccount.setType(AppConfig.TYPE_INCOME);
        setTypeData();
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
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.FORMAT_MONTH_DAY, Locale.CHINA);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_TYPE:
                    // 重新获取分类数据并设置
                    initTypeData();
                    setTypeData();
                    break;
            }
        }
    }
}

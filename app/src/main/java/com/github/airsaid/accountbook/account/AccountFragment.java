package com.github.airsaid.accountbook.account;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.github.airsaid.accountbook.data.AccountType;
import com.github.airsaid.accountbook.util.ToastUtils;
import com.github.airsaid.accountbook.util.UiUtils;

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
public class AccountFragment extends BaseFragment implements AccountContract.View {

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
    private void initData(){
        // 初始化分类数据
        mTypes.clear();
        String[] types = getResources().getStringArray(R.array.account_type);
        for (int i = 0; i < types.length; i++) {
            AccountType type = new AccountType();
            int resId = getResources().getIdentifier("ic_cost_type_".concat(String.valueOf(i)), "mipmap"
                            , getActivity().getPackageName());
            type.drawable = UiUtils.getDrawable(resId);
            type.type = types[i];
            mTypes.add(type);
        }
        mTypeAdapter.setNewData(mTypes);

        // 设置默认分类选中
        setAccountType(0);
        // 设置默认日期
        Calendar calendar = Calendar.getInstance();
        setSelectData(calendar);
    }

    private void setAccountType(int position){
        AccountType type = mTypeAdapter.getData().get(position);
        UiUtils.setCompoundDrawables(mTxtType, type.drawable, null, null, null);
        mTxtType.setText(type.type);
    }

    @Override
    public void setPresenter(AccountContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void save() {
        ToastUtils.show(mContext, "保存");
    }

    /**
     * 选择了支出
     */
    @Override
    public void selectCost() {
        ToastUtils.show(mContext, "选择了支出");
    }

    /**
     * 选择了收入
     */
    @Override
    public void selectIncome() {
        ToastUtils.show(mContext, "选择了收入");
    }

    /**
     * 显示选择日期弹框
     */
    @Override
    public void showSelectDate() {
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
     * 设置选择的日期
     */
    private void setSelectData(Calendar calendar){
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日", Locale.CHINA);
        String time = format.format(date);
        mTxtDate.setText(time);
    }


    @OnClick(R.id.txt_date)
    public void onClick() {
        showSelectDate();
    }
}

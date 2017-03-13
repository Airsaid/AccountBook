package com.github.airsaid.accountbook.account;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.base.BaseActivity;
import com.github.airsaid.accountbook.data.Account;
import com.github.airsaid.accountbook.util.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Airsaid
 * @Date 2017/2/27 21:28
 * @Blog http://blog.csdn.net/airsaid
 * @Desc 记账 Activity
 */
public class AccountActivity extends BaseActivity {

    @BindView(R.id.txt_type)
    TextView mTxtType;
    @BindView(R.id.txt_consume_type)
    TextView mTxtConsumeType;
    @BindView(R.id.edt_price)
    EditText mEdtPrice;
    @BindView(R.id.edt_remarks)
    EditText mEdtRemarks;
    @BindView(R.id.txt_date)
    TextView mTxtDate;

    // 类型 1:支出 2：收入
    private int mType = -1;

    // 消费类型
    private String mCType = null;

    private Date mDate;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_account;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {

    }


    @OnClick({R.id.txt_type, R.id.txt_consume_type, R.id.txt_date})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_type:
                final String[] types = {"支出", "收入"};
                new AlertDialog.Builder(this)
                        .setTitle("选择类别")
                        .setItems(types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTxtType.setText(types[which]);
                                mType = which + 1;
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.txt_consume_type:
                final String[] ctypes = {"一般", "学习", "餐饮", "交通", "购物", "医疗"};
                new AlertDialog.Builder(this)
                        .setTitle("选择类别")
                        .setItems(ctypes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTxtConsumeType.setText(ctypes[which]);
                                mCType = ctypes[which];
                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.txt_date:
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        Date date = calendar.getTime();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String time = format.format(date);
//                        mTxtDate.setText(year + "-" + (monthOfYear + 1) + "-" +  dayOfMonth);
                        mTxtDate.setText(time);

                        mDate = date;
                    }
                    // 设置初始日期
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    public void save(View v){
        if(mType == -1){
            ToastUtils.show(this, "请选择类型");
            return;
        }

        if(mCType == null){
            ToastUtils.show(this, "请选择消费类型");
            return;
        }

        String price = mEdtPrice.getText().toString();
        if(TextUtils.isEmpty(price)){
            ToastUtils.show(this, "请输入金额");
            return;
        }

        String remarks = mEdtRemarks.getText().toString();
        if(TextUtils.isEmpty(remarks)){
            ToastUtils.show(this, "请输入备注");
            return;
        }

        Account account = new Account();
        account.type = mType;
        account.ctype = mCType;
        account.money = price;
        account.note = remarks;
        account.date = mDate;
        saveAccount(account);
    }

    private void saveAccount(Account account){
        AVObject a = new AVObject("Account");
        a.put("type", account.type);
        a.put("ctype", account.ctype);
        a.put("money", account.money);
        a.put("date", account.date);
        a.put("note", account.note);
        a.put("uid", AVUser.getCurrentUser());
        a.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    ToastUtils.show(mContext, "保存成功！");
                }else{
                    ToastUtils.show(mContext, e.getMessage());
                }
            }
        });
    }
}

package com.github.airsaid.accountbook.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.airsaid.accountbook.R;
import com.github.airsaid.accountbook.util.RegexUtils;
import com.github.airsaid.accountbook.util.UiUtils;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/3/17
 * @desc 验证手机号的 Dialog
 */
@SuppressLint("ValidFragment")
public class VerifyPhoneDialog extends AppCompatDialogFragment{

    private View mContentView;
    private EditText mEdtCode;

    public VerifyPhoneDialog(Context context) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.dialog_verify_phone, null);
        TextInputLayout tilCode = (TextInputLayout) mContentView.findViewById(R.id.til_code);
        if (tilCode.getEditText() == null) return;
        mEdtCode = tilCode.getEditText();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(UiUtils.getString(R.string.verify_phone))
                .setCancelable(false)
                .setView(mContentView, 0, 50, 0, 0)
                .setNegativeButton(UiUtils.getString(R.string.dialog_cancel), null)
                .setPositiveButton(UiUtils.getString(R.string.dialog_finish), null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mCallback == null) return;

                        String code = mEdtCode.getText().toString();
                        if (!RegexUtils.checkCode(code)) {
                            mCallback.onVerifyFail(UiUtils.getString(R.string.hint_right_code));
                        } else {
                            mCallback.onVerifySuccess(code);
                        }
                    }
                });
            }
        });
        return alertDialog;
    }

    public OnVerifyPhoneCallback mCallback;

    public interface OnVerifyPhoneCallback{
        void onVerifySuccess(String code);
        void onVerifyFail(String msg);
    }

    public void setOnVerifyPhoneCallback(OnVerifyPhoneCallback callback){
        this.mCallback = callback;
    }
}

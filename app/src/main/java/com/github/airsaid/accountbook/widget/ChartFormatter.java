package com.github.airsaid.accountbook.widget;

import com.github.airsaid.accountbook.util.LogUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author Airsaid
 * @Date 2017/5/21 15:40
 * @Blog http://blog.csdn.net/airsaid
 * @Desc
 */
public class ChartFormatter implements IValueFormatter, IAxisValueFormatter {

    private int mSize = 0;
    protected DecimalFormat mFormat;
    private HashMap<String, Float> mPercentMap;
    private boolean mIsCallback;// is callback listener

    public ChartFormatter(HashMap<String, Float> map, int size) {
        this.mFormat = new DecimalFormat("##0.0");
        this.mIsCallback = false;
        this.mPercentMap = map;
        this.mSize = size;
    }

    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        String formatValue = this.mFormat.format((double) value);
        PieEntry e = (PieEntry) entry;
        String type = e.getLabel();
        mPercentMap.put(type, Float.valueOf(formatValue));
        if(mSize == mPercentMap.size() && mListener != null && !mIsCallback){
            mListener.onFormattedValue(mPercentMap);
            mIsCallback = true;
        }
        return formatValue + " %";
    }

    public String getFormattedValue(float value, AxisBase axis) {
        return this.mFormat.format((double)value) + " %";
    }

    public int getDecimalDigits() {
        return 1;
    }

    public OnFormattedFinishListener mListener;

    public interface OnFormattedFinishListener {
        void onFormattedValue(HashMap<String, Float> percentMap);
    }

    public void setOnFormattedValueListener(OnFormattedFinishListener listener){
        this.mListener = listener;
    }
}

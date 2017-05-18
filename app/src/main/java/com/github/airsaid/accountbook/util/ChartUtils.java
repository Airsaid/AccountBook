package com.github.airsaid.accountbook.util;

import android.graphics.Color;

import com.github.airsaid.accountbook.R;
import com.github.mikephil.charting.charts.PieChart;

/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/5/18
 * @desc 图表相关操作工具类，主要将一些基本操作封装在该类。
 */
public class ChartUtils {

    /**
     * 初始化饼状图
     * @param chart pieChart
     */
    public static void initPieChart(PieChart chart){
        chart.setNoDataText(UiUtils.getString(R.string.empty_data));
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setDrawHoleEnabled(true);
        chart.setDrawEntryLabels(false);// 设置条目标签
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.getLegend().setEnabled(false);
    }

}

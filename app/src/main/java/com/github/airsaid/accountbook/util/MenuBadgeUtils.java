package com.github.airsaid.accountbook.util;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.airsaid.accountbook.R;


/**
 * @author Airsaid
 * @github https://github.com/airsaid
 * @date 2017/4/25
 * @desc 菜单提示红点相关操作工具类.
 */
public class MenuBadgeUtils {

    /**
     * 更新消息数量,当 0 时隐藏.
     * @param act       菜单对应 Activity
     * @param menu      菜单 item
     * @param resId     菜单图片
     * @param count     消息数量
     */
    public static void update(final Activity act, final MenuItem menu, int resId, int count){
        if(menu == null) return;

        View badge = menu.getActionView();
        if(badge == null){
            menu.setActionView(R.layout.menu_action_item_badge);
            badge = menu.getActionView();
        }

        ImageView imgBadge = (ImageView) badge.findViewById(R.id.menu_badge_icon);
        TextView txtBadge = (TextView) badge.findViewById(R.id.menu_badge);

        if(resId != -1){
            imgBadge.setImageResource(resId);
        }

        if(count > 0){
            txtBadge.setVisibility(View.VISIBLE);
            if(count < 10){
                txtBadge.setTextSize(12f);
                txtBadge.setText(String.valueOf(count));
            }else{
                txtBadge.setTextSize(10f);
                txtBadge.setText("9+");
            }

        }else{
            txtBadge.setVisibility(View.GONE);
        }

        badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, menu);
            }
        });
    }
}

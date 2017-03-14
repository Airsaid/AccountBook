package com.github.airsaid.accountbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.github.airsaid.accountbook.account.AccountActivity;
import com.github.airsaid.accountbook.login.LoginActivity;
import com.github.airsaid.accountbook.register.RegisterActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View v){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void register(View v){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void account(View v){
        startActivity(new Intent(this, AccountActivity.class));
    }

    public void query(View v){
        final TextView txtQuery = (TextView) findViewById(R.id.txt_query);

        final AVQuery<AVObject> startDateQuery = new AVQuery<>("Account");
        startDateQuery.whereGreaterThanOrEqualTo("date", getDateWithDateString("2017-03"));

        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Account");
        endDateQuery.whereLessThan("date", getDateWithDateString("2017-04"));

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(startDateQuery, endDateQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null){
                    StringBuilder sb = new StringBuilder();
                    for (AVObject avObject : list) {
                        String money = (String) avObject.get("money");
                        String note = (String) avObject.get("note");
                        Date date = (Date) avObject.get("date");
                        sb.append("\r\n金额: " + money);
                        sb.append("\r\n备注: " + note);
//                        sb.append("\r\n日期: " + date.toString());
//                        Calendar c = Calendar.getInstance(Locale.CHINA);
//                        c.setTime(date);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String time = format.format(date);
                        sb.append("\r\n日期: " + time);
                        Log.d("test", "money: " + money);
                        Log.d("test", "note: " + note);
                        Log.d("test", "date: " + date.toString());
                    }
                    txtQuery.setText(sb.toString());
                }
            }
        });
    }

    private Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

package com.github.airsaid.accountbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.airsaid.accountbook.login.LoginActivity;
import com.github.airsaid.accountbook.register.RegisterActivity;
import com.github.airsaid.accountbook.util.LogUtils;

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
}

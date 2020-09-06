package com.deco.magnus.ActivityScreens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

public class Chat extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        support.setSupportBarActive(getSupportActionBar(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        User user = MainActivity.getLoggedUser();
    }
}

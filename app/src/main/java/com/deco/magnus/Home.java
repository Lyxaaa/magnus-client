package com.deco.magnus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        final Button gameBtn = findViewById(R.id.game_btn);
        final Button chatBtn = findViewById(R.id.chat_btn);
        final Button friendsBtn = findViewById(R.id.friends_btn);

        friendsBtn.setBackgroundResource(R.drawable.game_icon);

        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
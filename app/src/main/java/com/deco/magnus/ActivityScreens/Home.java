package com.deco.magnus.ActivityScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        User user = MainActivity.getLoggedUser();

        final Button gameBtn = findViewById(R.id.game_btn);
        final Button chatBtn = findViewById(R.id.chat_btn);
        final Button friendsBtn = findViewById(R.id.friends_btn);

        friendsBtn.setBackgroundResource(R.drawable.game_icon);

        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(v);
            }
        });
    }

    public void createGame(View view) {
        Intent gameScreen = new Intent(this, GameScreen.class);
        startActivity(gameScreen);
    }
}
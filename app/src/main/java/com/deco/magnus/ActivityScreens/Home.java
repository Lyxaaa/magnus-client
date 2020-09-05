package com.deco.magnus.ActivityScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        User user = MainActivity.getLoggedUser();

        final Button gameBtn = findViewById(R.id.home_game_btn);
        final Button chatBtn = findViewById(R.id.home_chat_btn);
        final Button friendsBtn = findViewById(R.id.home_friends_btn);
        final TextView gameTxt = findViewById(R.id.home_game_txt);
        final TextView chatTxt = findViewById(R.id.home_chat_txt);
        final TextView friendsTxt = findViewById(R.id.home_friends_txt);

        friendsBtn.setMaxWidth(20);
        friendsBtn.setMaxHeight(20);

        gameBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    gameBtn.getContext().getResources().getColorStateList(R.color.colorAccent);
                    gameTxt.getContext().getResources().getColorStateList(R.color.colorAccent);
                            //.setTextColor(getResources().getColor(R.color.credentialsDefault));
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });

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
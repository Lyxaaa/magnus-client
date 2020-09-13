package com.deco.magnus.ActivityScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

public class GameScreen extends AppCompatActivity {
    User user = MainActivity.getLoggedUser();

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Button chessBtn = findViewById(R.id.chess_start_btn);

        chessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChess(v);
            }
        });
    }

    private void createChess(View v) {
        Intent chessScreen = new Intent(this, Chess.class);
        startActivity(chessScreen);
    }
}

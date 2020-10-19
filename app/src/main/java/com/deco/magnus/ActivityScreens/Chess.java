package com.deco.magnus.ActivityScreens;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.Games.Chess.Board;
import com.deco.magnus.R;

public class Chess extends AppCompatActivity {

    Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chess_game);
        View v = findViewById(R.layout.chess_game);


        board = new Board(this, findViewById(R.id.chess_board));

        Button b = (Button)findViewById(R.id.chess_reset);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }


}

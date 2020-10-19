package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.Games.Chess.Board;
import com.deco.magnus.R;

public class Chess extends AppCompatActivity {
    Activity activity = this;
    Board board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chess_game);
        View root = findViewById(R.id.root_chess);

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (board == null) {
                TableLayout tableLayout = findViewById(R.id.chess_board);
                View rt = tableLayout.getRootView();
                ViewGroup.LayoutParams params = tableLayout.getLayoutParams();
                int rootHeight = Math.min(rt.getHeight(), root.getHeight());

                params.width = rootHeight;
                params.height = rootHeight;
                tableLayout.setLayoutParams(params);
                board = new Board(activity, tableLayout, rootHeight / 75);
            }
        });

        Button b = findViewById(R.id.chess_reset);
        b.setOnClickListener(view -> {
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
    }
}

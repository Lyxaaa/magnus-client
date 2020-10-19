package com.deco.magnus.ActivityScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.Netbase.JsonMsg;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.EnterMatchQueue;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;

import static com.deco.magnus.ActivityScreens.MainActivity.loggedUser;
import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class GameScreen extends AppCompatActivity {
    boolean isSearching = false;

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

        final Button chessBtn = findViewById(R.id.btn_start_queue);

        final com.deco.magnus.Netbase.Client.OnReceiveListener onMatchFound = (socketType, dataType, data) -> {
            EnterMatchQueue mf =  TryCast(dataType, data, Type.EnterMatchQueue.getValue(), EnterMatchQueue.class);
            if(mf == null) return false;

            // start of a game,
            // show accept/decline


            return true;
        };

        chessBtn.setOnClickListener(v -> {
            if(isSearching) {
                isSearching = false;
                chessBtn.setText("FIND MATCH");
                chessBtn.setBackground(getResources().getDrawable(R.drawable.round_green));
                Client.getInstance().removeOnReceiveListener(onMatchFound);
                JsonMsg msg = new JsonMsg();
                msg.type = Type.ExitMatchQueue.getValue();
                Client.getInstance().threadSafeSend(msg);
            } else {
                isSearching = true;
                chessBtn.setText("CANCEL QUEUE");
                chessBtn.setBackground(getResources().getDrawable(R.drawable.round_red));
                Client.getInstance().addOnReceiveListener(onMatchFound);
                Client.getInstance().threadSafeSend(new EnterMatchQueue(loggedUser.getEmail()));
            }
        });
    }

    private void createChess(View v) {
        Intent chessScreen = new Intent(this, Chess.class);
        startActivity(chessScreen);
    }
}

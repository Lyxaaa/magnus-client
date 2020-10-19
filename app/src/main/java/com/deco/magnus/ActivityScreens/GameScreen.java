package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.Games.Chess.GameState;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.AcceptMatch;
import com.deco.magnus.ProjectNet.Messages.EnterMatchQueue;
import com.deco.magnus.ProjectNet.Messages.ExitMatchQueue;
import com.deco.magnus.ProjectNet.Messages.MatchFound;
import com.deco.magnus.ProjectNet.Messages.MatchStart;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class GameScreen extends AppCompatActivity {
    boolean isSearching = false;

    final Activity activity = this;

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

        isSearching = false;

        final Button btnQ = findViewById(R.id.btn_start_queue);
        final LinearLayout layout = findViewById(R.id.game_main);

        final com.deco.magnus.Netbase.Client.OnReceiveListener onMatchStart = (socketType, dataType, data) -> {
            MatchStart ms = TryCast(dataType, data, Type.MatchStart.getValue(), MatchStart.class);
            if (ms == null) return false;
            GameState.getInstance().setMatchStart(ms);
            if (GameState.getInstance().isReady()) {
                Intent chessScreen = new Intent(this, Chess.class);
                startActivity(chessScreen);
            }
            return true;
        };

        final com.deco.magnus.Netbase.Client.OnReceiveListener onMatchFound = (socketType, dataType, data) -> {
            MatchFound mf = TryCast(dataType, data, Type.MatchFound.getValue(), MatchFound.class);
            if (mf == null) return false;

            Client.getInstance().addOnReceiveListener(onMatchStart);

            GameState.getInstance().setMatchFound(mf);

            MediaPlayer mp = MediaPlayer.create(activity, R.raw.bong);
            mp.setOnCompletionListener(mediaPlayer -> mp.release());
            mp.start();

            Vibrator v;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(250);
                }
            }

            runOnUiThread(() -> {
                LayoutInflater loginInflater = getLayoutInflater();
                PopupWindow acceptMatch = new PopupWindow(loginInflater.inflate(R.layout.match_found, null, false), layout.getWidth(), layout.getHeight(), true);
                acceptMatch.showAtLocation(layout, Gravity.CENTER, 0, getSupportActionBar().getHeight());
                View content = acceptMatch.getContentView();

                TextView status = content.findViewById(R.id.txt_game_status);
                ProgressBar progressBar = content.findViewById(R.id.pb_awaiting);
                Button accept = content.findViewById(R.id.btn_accept);
                Button decline = content.findViewById(R.id.btn_decline);

                status.setText("Match Found!");
                final com.deco.magnus.Netbase.Client.OnReceiveListener onResult = (socketType1, dataType1, data1) -> {
                    MessageResult result = TryCast(dataType1, data1, Type.MessageResult.getValue(), MessageResult.class);
                    if (result == null || result.callingType != Type.AcceptMatch) return false;
                    switch (result.result) {
                        case Success:
                            runOnUiThread(() -> {
                                status.setText("Match starting!");
                                accept.setEnabled(false);
                                decline.setEnabled(false);
                                decline.setBackground(getResources().getDrawable(R.drawable.round_darker));
                                decline.setTextColor(getResources().getColor(R.color.dark));
                                accept.setBackground(getResources().getDrawable(R.drawable.round_darker));
                                accept.setTextColor(getResources().getColor(R.color.dark));

                                isSearching = false;
                                btnQ.setText("FIND MATCH");
                                btnQ.setBackground(getResources().getDrawable(R.drawable.round_green));
                            });
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(() -> {
                                        acceptMatch.dismiss();
                                        Intent chessScreen = new Intent(activity, Chess.class);
                                        startActivity(chessScreen);
                                    });
                                }
                            }, 2000);
                            return true;
                        case Pending:
                            return false;
                        case Invalid:
                        case Failure:
                            runOnUiThread(() -> {
                                status.setText("Match has been Declined");
                                accept.setEnabled(false);
                                decline.setEnabled(false);
                                decline.setBackground(getResources().getDrawable(R.drawable.round_darker));
                                decline.setTextColor(getResources().getColor(R.color.dark));
                                accept.setBackground(getResources().getDrawable(R.drawable.round_darker));
                                accept.setTextColor(getResources().getColor(R.color.dark));
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(() -> {
                                            acceptMatch.dismiss();
                                            isSearching = true;
                                            btnQ.callOnClick();
                                        });
                                    }
                                }, 2500);
                            });
                            return true;
                    }
                    return false;
                };
                Client.getInstance().addOnReceiveListener(onResult);

                accept.setOnClickListener(view -> {
                    status.setText("Waiting for opponent");
                    progressBar.setIndeterminate(true);
                    accept.setBackground(getResources().getDrawable(R.drawable.round_darker));
                    accept.setTextColor(getResources().getColor(R.color.dark));
                    accept.setEnabled(false);
                    Client.getInstance().threadSafeSend(new AcceptMatch(
                            true,
                            GameState.getInstance().getEmail(),
                            GameState.getInstance().getOpponent(),
                            GameState.getInstance().getMatchId()
                    ));
                });

                decline.setOnClickListener(view -> {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> {
                                acceptMatch.dismiss();
                                isSearching = true;
                                btnQ.callOnClick();
                                Client.getInstance().removeOnReceiveListener(onResult);
                            });
                        }
                    }, 2500);
                    status.setText("Match has been Declined");
                    accept.setEnabled(false);
                    decline.setEnabled(false);
                    decline.setBackground(getResources().getDrawable(R.drawable.round_darker));
                    decline.setTextColor(getResources().getColor(R.color.dark));
                    accept.setBackground(getResources().getDrawable(R.drawable.round_darker));
                    accept.setTextColor(getResources().getColor(R.color.dark));

                    Client.getInstance().threadSafeSend(new AcceptMatch(false,
                            GameState.getInstance().getEmail(),
                            GameState.getInstance().getOpponent(),
                            GameState.getInstance().getMatchId()));
                });
            });
            return true;
        };

        btnQ.setOnClickListener(v -> {
            if (isSearching) {
                isSearching = false;
                btnQ.setText("FIND MATCH");
                btnQ.setBackground(getResources().getDrawable(R.drawable.round_green));
                Client.getInstance().removeOnReceiveListener(onMatchFound);
                Client.getInstance().threadSafeSend(new ExitMatchQueue(GameState.getInstance().getEmail()));
                GameState.getInstance().clear();
            } else {
                isSearching = true;
                btnQ.setText("CANCEL QUEUE");
                btnQ.setBackground(getResources().getDrawable(R.drawable.round_red));
                Client.getInstance().addOnReceiveListener(onMatchFound);
                Client.getInstance().threadSafeSend(new EnterMatchQueue(GameState.getInstance().getEmail()));
            }
        });
    }
}

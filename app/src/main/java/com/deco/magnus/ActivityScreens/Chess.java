package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.Games.Chess.Board;
import com.deco.magnus.Games.Chess.GameState;
import com.deco.magnus.Netbase.UDPSocket;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.BoardResult;
import com.deco.magnus.ProjectNet.Messages.EndMatch;
import com.deco.magnus.ProjectNet.Messages.GetBoardState;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.ProjectNet.Messages.UpdateBoard;
import com.deco.magnus.R;
import com.ncorti.slidetoact.SlideToActView;

import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class Chess extends AppCompatActivity {
    Activity activity = this;
    Board board;
    boolean iWon = false;
    boolean myTurn = false;
    boolean firstTurn = true;
    Button endTurn;
    UDPSocket chessBoard;

    com.deco.magnus.Netbase.Client.OnReceiveListener listener = (socketType, dataType, data) -> {
        BoardResult br = TryCast(dataType, data, Type.BoardResult.getValue(), BoardResult.class);
        if (br != null) {
            Log.d("Board", "First Turn = " + firstTurn);
            activity.runOnUiThread(() -> {
                board.setStateFromNetworkMessage(br);
                if (firstTurn) {
                    firstTurn = false;
                } else {
                    myTurn = !myTurn;
                }
                Log.d("Board", "My Turn = " + myTurn);

//            myTurn = !myTurn;
////            if (!firstTurn)
////                myTurn = true;
////            else
////                firstTurn = false;
                updateEndTurnButtonStyle(endTurn);
                board.setStateFromNetworkMessage(br);
            });
            return false;
        }

        EndMatch em = TryCast(dataType, data, Type.EndMatch.getValue(), EndMatch.class);
        if (em != null) {

            return false;
        }

        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        new Thread(() -> {
//            chessBoard = new UDPSocket("192.168.1.23", 42069);
//            chessBoard.begin();
//        }).start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chess_game);
        View root = findViewById(R.id.root_chess);

        Client.getInstance().addOnReceiveListener(listener);

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (board == null) {
                TableLayout tableLayout = findViewById(R.id.chess_board);
                final TextView name = findViewById(R.id.txt_opponent);
                name.setText(GameState.getInstance().getEmail());
                View rt = tableLayout.getRootView();
                ViewGroup.LayoutParams params = tableLayout.getLayoutParams();
                int rootHeight = Math.min(rt.getHeight(), root.getHeight());

                params.width = rootHeight;
                params.height = rootHeight;
                tableLayout.setLayoutParams(params);
                board = new Board(activity, tableLayout, rootHeight / 75);
                Log.d("Board", "Match ID = " + GameState.getInstance().getMatchId());
                Client.getInstance().threadSafeSend(new GetBoardState(GameState.getInstance().getMatchId()));
            }
        });

        if (GameState.getInstance().isWhite()) {
            myTurn = true;
        }

        SlideToActView sldConcede = findViewById(R.id.slider_concede);
        sldConcede.setOnSlideCompleteListener(slideToActView -> {
            Client.getInstance().threadSafeSend(new EndMatch(GameState.getInstance().getMatchId()));
        });

        endTurn = findViewById(R.id.btn_next_turn);
        endTurn.setOnClickListener(view -> {
            updateEndTurnButtonStyle(endTurn);
            Client.getInstance().threadSafeSend(
                    new UpdateBoard(
                            GameState.getInstance().getMatchId(),
                            board.getStateForSending(),
                            GameState.getInstance().isWhite()));
            if (GameState.getInstance().isWhite()) {
                Log.d("Board", "Bytes: " + board.getStateForSending().getBytes());
//                new Thread(() -> chessBoard.send(board.getStateForSending().getBytes())).start();
            }
        });
        updateEndTurnButtonStyle(endTurn);
    }

    void updateEndTurnButtonStyle(Button endTurn) {
        runOnUiThread(() -> {
            if (myTurn) {
                endTurn.setBackground(activity.getResources().getDrawable(R.drawable.round_blue));
                endTurn.setTextColor(activity.getResources().getColor(R.color.white));
                endTurn.setText("End Turn");
                endTurn.setEnabled(true);
            } else {
                endTurn.setBackground(activity.getResources().getDrawable(R.drawable.round_darker));
                endTurn.setTextColor(activity.getResources().getColor(R.color.dark));
                endTurn.setText("Opponent's Turn");
                endTurn.setEnabled(false);
            }
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

    @Override
    protected void onStop() {
        super.onStop();
        if (!iWon)
            Client.getInstance().threadSafeSend(new EndMatch(GameState.getInstance().getMatchId()));

        Client.getInstance().removeOnReceiveListener(listener);
    }
}

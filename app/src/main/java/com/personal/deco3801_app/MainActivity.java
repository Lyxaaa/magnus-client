package com.personal.deco3801_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    Button btn_new_act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final MessageFragment messageFragment = new MessageFragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout home_sidebar = findViewById(R.id.home_sidebar);
        final Button btn_friends = findViewById(R.id.btn_friends);
        final Button btn_games = findViewById(R.id.btn_games);
        final Button btn_chat = findViewById(R.id.btn_chat);
        final Button btn_profile = findViewById(R.id.btn_profile);

        home_sidebar.setMinimumWidth(btn_friends.getHeight());
        btn_friends.setWidth(btn_friends.getHeight());
        btn_games.setWidth(btn_games.getHeight() );
        btn_chat.setWidth(btn_chat.getHeight());
        btn_profile.setWidth(btn_profile.getHeight());

        final LinearLayoutCompat second_layout = findViewById(R.id.linear_layout);

        final Fragment frag_chat = getSupportFragmentManager().findFragmentById(R.id.fragment_chat);

        Socket socket = new Socket();

        btn_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_friends.setHighlightColor(Color.rgb(100, 200, 0));

//                if (btn_new_act == null) {
//                    btn_new_act = new Button(v.getContext());
//                    btn_new_act.setText("Confirm");
//                    second_layout.addView(btn_new_act);
//                    btn_new_act.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(v.getContext(), SecondActivity.class);
//                            v.getContext().startActivity(intent);
//                        }
//                    });
//                }
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams frag_chat_params = messageFragment.getView().getLayoutParams();
                frag_chat_params.width = 150;
                frag_chat.getView().setLayoutParams(frag_chat_params);
            }
        });

        ViewTreeObserver obsv_friends = btn_friends.getViewTreeObserver();
        obsv_friends.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            }
        });
    }


}
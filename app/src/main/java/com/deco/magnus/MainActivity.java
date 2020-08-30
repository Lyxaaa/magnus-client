package com.deco.magnus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.deco.magnus.UserData.User;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MainActivity extends AppCompatActivity {
    String lastFragTag = "chatFrag";
    Activity activity = this;
    String logTag = "socketLogger";
    String socketData = null;

    EditText username, pword;

    User loggedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Created instance");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        final RelativeLayout rootLayout = findViewById(R.id.root_layout);
        final Button registerBtn = findViewById(R.id.register_btn);
        final Button loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater loginInflater = getLayoutInflater();

                PopupWindow window = new PopupWindow(loginInflater.inflate(R.layout.login_main, null, false), rootLayout.getWidth(), rootLayout.getHeight(), true);
                //View view = loginInflater.inflate(R.layout.login_main, null);
                window.showAtLocation(activity.findViewById(R.id.root_layout), Gravity.CENTER, 0, 0);

                /*
                username = window.getContentView().findViewById(R.id.username_box);
                pword = window.getContentView().findViewById(R.id.password_box);

                final Button submitLogin = findViewById(R.id.submit_login_btn);

                submitLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            loggedUser = new User(username.getText().toString(), pword.getText().toString());
                        } catch (User.IncorrectCredentialsException ice) {
                            loggedUser = null;
                        }
                    }
                });*/
            }
        });

//        final TextView socket_test = findViewById(R.id.socket_test);
        DataTransmission dataTransmission = new DataTransmission();

//        final LinearLayout home_sidebar = findViewById(R.id.home_sidebar);
//        final Button btn_friends = findViewById(R.id.btn_friends);
//        final Button btn_games = findViewById(R.id.btn_games);
//        final Button btn_chat = findViewById(R.id.btn_chat);
//        final Button btn_profile = findViewById(R.id.btn_profile);

//        home_sidebar.setMinimumWidth(btn_friends.getHeight());
//        btn_friends.setWidth(btn_friends.getHeight());
//        btn_games.setWidth(btn_games.getHeight() );
//        btn_chat.setWidth(btn_chat.getHeight());
//        btn_profile.setWidth(btn_profile.getHeight());


        getSupportFragmentManager().beginTransaction().add(MessageFragment.newInstance(), lastFragTag).commitAllowingStateLoss();

//        btn_friends.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_friends.setTextColor(Color.rgb(100, 200, 0));
//
//            }
//        });

//        btn_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction ft = fragmentManager.beginTransaction();
//                Fragment previous = fragmentManager.findFragmentByTag(lastFragTag);
//                if (previous != null) {
//                    ft.remove(previous);
//                }
//                lastFragTag = "chatFrag";
//                ft.add(MessageFragment.newInstance(), lastFragTag).commit();
//                Fragment current = fragmentManager.findFragmentByTag(lastFragTag);
//                ViewGroup.LayoutParams frag_chat_params = current.getView().getLayoutParams();
////                frag_chat_params.width = 150;
////                current.getView().setLayoutParams(frag_chat_params);
//            }
//        });

//        ViewTreeObserver obsv_friends = btn_friends.getViewTreeObserver();
//        obsv_friends.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//            }
//        });
//        getSocketData();
//        socket_test.setText(socketData);
    }

    private void getSocketData() {
        try {
            Log.d(logTag, "Getting Socket Data");

            final DatagramSocket socket = new DatagramSocket(25566);
            Log.d(logTag, "Created Socket");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatagramPacket packet = new DatagramPacket(new byte[65000], 0, 65000);
                    while (true) {
                        Log.d(logTag, "Received Packet");
                        try {
                            socket.receive(packet);
                        } catch (Exception e) { }

                        byte[] chars = new byte[packet.getLength()];
                        System.arraycopy(packet.getData(), 0, chars, 0, chars.length);
                        final String s = new String(chars);
                        Log.d(logTag, "Received Packet with text: " + s);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                socketData = s;
                            }
                        });
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.deco.magnus.ActivityScreens;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deco.magnus.DataTransmission;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MainActivity extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();

    String lastFragTag = "chatFrag";
    Activity activity = this;
    String logTag = "socketLogger";
    String socketData = null;

    EditText email, pword, cfrmPword;
    Button submitLogin, submitRegister;

    private static User loggedUser;

    public static void setLoggedUser(User user) {
        if (user != null) {
            loggedUser = user;
        }
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void largeButtonPress(int frameId, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker)));
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lighter)));
        }
    }

//TODO For some reason the support bar back button only goes away on pressing it a 2nd time, fix this l8r
    @Override
    public boolean onSupportNavigateUp() {
//        support.setSupportBarActive(getSupportActionBar(), false);
        if (support.active) {
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        support.setSupportBarActive(getSupportActionBar(), true);
        System.out.println("Created instance");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(findViewById(R.id.my_toolbar));

        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        final RelativeLayout rootLayout = findViewById(R.id.root_layout);
        final FrameLayout registerBtn = findViewById(R.id.main_register_frame);
        final FrameLayout loginBtn = findViewById(R.id.main_login_frame);
//        final Button registerBtn = findViewById(R.id.main_register_btn);
//        final Button loginBtn = findViewById(R.id.main_login_btn);

        loginBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.main_login_onclick_frame, event);
                return false;
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater loginInflater = getLayoutInflater();
                PopupWindow loginWindow = new PopupWindow(loginInflater.inflate(R.layout.login_main, null, false), rootLayout.getWidth(), rootLayout.getHeight(), true);
                loginWindow.showAtLocation(activity.findViewById(R.id.root_layout), Gravity.CENTER, 0, getSupportActionBar().getHeight());
                support.setSupportBarActive(getSupportActionBar(), true);
                email = loginWindow.getContentView().findViewById(R.id.login_email_edit_text);
                pword = loginWindow.getContentView().findViewById(R.id.login_password_edit_text);
                email.setInputType(InputType.TYPE_CLASS_TEXT);
                pword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                submitLogin = loginWindow.getContentView().findViewById(R.id.submit_login_btn);
                final TextView credentialInfo = loginWindow.getContentView().findViewById(R.id.login_view);

                email.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            credentialInfo.setText(R.string.login);
                            credentialInfo.setTextColor(getResources().getColor(R.color.credentialsDefault));
                        }
                        return false;
                    }
                });

                submitLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            loggedUser = new User(email.getText().toString(), pword.getText().toString());
                        } catch (User.IncorrectCredentialsException ice) {
                            loggedUser = null;
                        }

                        if (loggedUser != null) {
                            createHome(v);
                        } else {
                            email.clearComposingText();
                            pword.clearComposingText();
                            credentialInfo.setText(R.string.bad_login);
                            credentialInfo.setTextColor(getResources().getColor(R.color.credentialsError));
                        }
                    }
                });
            }
        });

        registerBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.main_register_onclick_frame, event);
                return false;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater registerInflater = getLayoutInflater();
                PopupWindow registerWindow = new PopupWindow(registerInflater.inflate(R.layout.register_main, null, false), rootLayout.getWidth(), rootLayout.getHeight(), true);
                registerWindow.showAtLocation(activity.findViewById(R.id.root_layout), Gravity.CENTER, 0, getSupportActionBar().getHeight());

                email = registerWindow.getContentView().findViewById(R.id.register_email_edit_text);
                pword = registerWindow.getContentView().findViewById(R.id.register_password_edit_text);
                cfrmPword = registerWindow.getContentView().findViewById(R.id.register_confirm_password_edit_text);

                email.setInputType(InputType.TYPE_CLASS_TEXT);
                pword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                cfrmPword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                submitRegister = registerWindow.getContentView().findViewById(R.id.submit_register_btn);
                final TextView credentialInfo = registerWindow.getContentView().findViewById(R.id.register_view);

                email.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            credentialInfo.setText(R.string.register);
                            credentialInfo.setTextColor(getResources().getColor(R.color.credentialsDefault));
                        }
                        return false;
                    }
                });

                pword.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            credentialInfo.setText(R.string.register);
                            credentialInfo.setTextColor(getResources().getColor(R.color.credentialsDefault));
                        }
                        return false;
                    }
                });

                submitRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!pword.getText().toString().equals("") &&
                                !cfrmPword.getText().toString().equals("") &&
                                pword.getText().toString().equals(cfrmPword.getText().toString())) {
                            try {
                                loggedUser = new User(email.getText().toString(), pword.getText().toString());
                            } catch (User.IncorrectCredentialsException ice) {
                                loggedUser = null;
                            }

                            if (loggedUser != null) {
                                createHome(v);
                            } else {
                                email.setText("");
                                pword.setText("");
                                cfrmPword.setText("");
                                credentialInfo.setText(R.string.bad_register_email);
                                credentialInfo.setTextColor(getResources().getColor(R.color.credentialsError));
                            }
                        } else {
                            pword.setText("");
                            cfrmPword.setText("");
                            credentialInfo.setText(R.string.bad_register_pword);
                            credentialInfo.setTextColor(getResources().getColor(R.color.credentialsError));
                        }
                    }
                });
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

    public void createHome(View view) {
        Intent homeScreen = new Intent(this, Home.class);
        startActivity(homeScreen);
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
package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.deco.magnus.DataTransmission;
import com.deco.magnus.Games.Chess.GameState;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;
import com.deco.magnus.UserData.UserInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    GlobalSupport support = new GlobalSupport();

    String lastFragTag = "chatFrag";
    Activity activity = this;
    String logTag = "socketLogger";
    String socketData = null;

    EditText email, pword, cfrmPword;
    Button submitLogin, submitRegister;

    //changed to public so i can access
    public static User loggedUser;

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
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark)));
        }
    }

    //TODO For some reason the support bar back button only goes away on pressing it a 2nd time, fix this l8r
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        support.setSupportBarActive(getSupportActionBar(), true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        System.out.println("Created instance");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(findViewById(R.id.my_toolbar));

        final FragmentManager fragmentManager = this.getSupportFragmentManager();

        final RelativeLayout rootLayout = findViewById(R.id.root_layout);
        final FrameLayout registerBtn = findViewById(R.id.main_register_frame);
        final FrameLayout loginBtn = findViewById(R.id.main_login_frame);

        //Changes the colour of the Login button to indicate being pressed
        loginBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.main_login_onclick_frame, event);
                return false;
            }
        });

        //Sets Login button action, allowing the User to begin a login request
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater loginInflater = getLayoutInflater();
                PopupWindow loginWindow = new PopupWindow(loginInflater.inflate(R.layout.login_main, null, false), rootLayout.getWidth(), rootLayout.getHeight(), true);
                loginWindow.showAtLocation(activity.findViewById(R.id.root_layout), Gravity.CENTER, 0, getSupportActionBar().getHeight());

                support.setSupportBarActive(getSupportActionBar(), true);
                email = loginWindow.getContentView().findViewById(R.id.login_email_edit_text);
                pword = loginWindow.getContentView().findViewById(R.id.login_password_edit_text);
                final CheckBox loginRemember = loginWindow.getContentView().findViewById(R.id.cb_remember);

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
                if(pref.getBoolean("login_remember", false))  {
                    loginRemember.setChecked(true);
                    email.setText(pref.getString("login_email", ""));
                    pword.setText(pref.getString("login_password", ""));
                }

                email.setInputType(InputType.TYPE_CLASS_TEXT);
                pword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                submitLogin = loginWindow.getContentView().findViewById(R.id.submit_login_btn);
                final TextView credentialInfo = loginWindow.getContentView().findViewById(R.id.login_view);

                //Resets the colour of the email text field upon touch, which is originally changed
                //to the Error colour upon entering an invalid email/password combination
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

                //Sets Submit button action, allowing the User to send a login request
                submitLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Login Info", "Beginning login sequence");
                        PopupWindow loading = new PopupWindow(loginInflater.inflate(R.layout.loading, null, false), rootLayout.getWidth(), rootLayout.getHeight(), true);
                        loading.showAtLocation(activity.findViewById(R.id.root_layout), Gravity.CENTER, 0, getSupportActionBar().getHeight());
                        Log.d("Login Info", "Loading content");
                        User.authenticateUser(email.getText().toString(), pword.getText().toString(), info -> runOnUiThread(() -> {
                            Log.d("Login Info", String.valueOf(info.result.getValue()) + " : " + String.valueOf(MessageResult.Result.Success.getValue()));
                            loading.dismiss();
                            if (info.result == MessageResult.Result.Success) {
                                if(loginRemember.isChecked()) {
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
                                    SharedPreferences.Editor edit = pref.edit();
                                    edit.putString("login_email", info.email);
                                    edit.putString("login_password", pword.getText().toString());
                                    edit.putBoolean("login_remember", true);
                                    edit.apply();
                                }
                                loggedUser = new User(info.uniqueId, info.userName, info.email, info.bio, null, activity);
                                GameState.getInstance().setUser(loggedUser);
                                loginWindow.dismiss();
                                createHome(v);
                            } else {
                                email.clearComposingText();
                                pword.clearComposingText();
                                credentialInfo.setText(R.string.bad_login);
                                credentialInfo.setTextColor(getResources().getColor(R.color.credentialsError));
                            }
                        }));

                    }
                });
            }
        });

        //Changes the colour of the Register button to indicate being pressed
        registerBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.main_register_onclick_frame, event);
                return false;
            }
        });

        //Sets Register button action, allowing the User to begin a login request
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

                //Resets the colour of the email text field upon touch, which is originally changed
                //to the Error colour upon entering an invalid email/password combination
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

                //Resets the colour of the password text field upon touch, which is originally changed
                //to the Error colour upon entering an invalid email/password combination
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
                //Sets Submit button action, allowing the User to send a register request
                submitRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Login Info", "Beginning register sequence");
                        PopupWindow loading = new PopupWindow(registerInflater.inflate(R.layout.loading, null, false), rootLayout.getWidth(), rootLayout.getHeight(), true);
                        loading.showAtLocation(activity.findViewById(R.id.root_layout), Gravity.CENTER, 0, getSupportActionBar().getHeight());
                        if (!pword.getText().toString().equals("") &&
                                !cfrmPword.getText().toString().equals("") &&
                                pword.getText().toString().equals(cfrmPword.getText().toString())) {
                            User.registerUser(email.getText().toString(), pword.getText().toString(), info -> runOnUiThread(() -> {
                                loading.dismiss();
                                if (info.result == MessageResult.Result.Success) {
                                    loggedUser = new User(info.uniqueId, info.userName, info.email, info.bio, null, activity);
                                    registerWindow.dismiss();
                                    createHome(v);
                                } else {
                                    email.getText().clear();
                                    pword.getText().clear();
                                    cfrmPword.getText().clear();
                                    credentialInfo.setText(R.string.bad_register_email);
                                    credentialInfo.setTextColor(getResources().getColor(R.color.credentialsError));
                                }
                            }));
                        } else {
                            loading.dismiss();
                            pword.getText().clear();
                            cfrmPword.getText().clear();
                            credentialInfo.setText(R.string.bad_register_pword);
                            credentialInfo.setTextColor(getResources().getColor(R.color.credentialsError));
                        }
                    }
                });
            }
        });
        DataTransmission dataTransmission = new DataTransmission();

        getSupportFragmentManager().beginTransaction().add(MessageFragment.newInstance(), lastFragTag).commitAllowingStateLoss();
    }

    /**
     * Creates a new view of the {@link GameScreen}
     * @param view View object passed throughout different activities
     */
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
                        } catch (Exception e) {
                        }

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

    /**
     * We don't want the hash to be calculated differently depending on which user is interacting
     * with the conversation, so a simple hash like this will prevent that. No need for anything
     * more complex
     * @param thisUserId
     * @param otherUserId
     * @return A hashed String to be used as the conversation ID
     */
    public static String nameHash(String thisUserId, String otherUserId) {
        return String.valueOf(31 * Integer.parseInt(thisUserId) * Integer.parseInt(otherUserId));
    }
}
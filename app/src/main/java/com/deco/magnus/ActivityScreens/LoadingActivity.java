package com.deco.magnus.ActivityScreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class LoadingActivity extends AppCompatActivity {
    public static String logTag = "loading";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            System.exit(0);
            return;
        }

//        support.setSupportBarActive(getSupportActionBar(), true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_loading);

        String oscarsPCExt = "49.3.201.65";
        String oscarsPCInt = "192.168.0.6";
        String cysPCInt = "192.168.1.42";
        String markPC = "192.168.171.34";

        String target = cysPCInt;

        final Timer timeout = new Timer();
        timeout.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Check server status 😢", Toast.LENGTH_LONG).show());
                finish();
            }
        }, 8000); // wait 5 seconds before giving up

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    MessageResult result = TryCast(dataType, data, Type.MessageResult.getValue(), MessageResult.class);
                    if (result != null && result.result == MessageResult.Result.Success) {
                        runOnUiThread(() -> {
                            timeout.cancel();
                            Toast.makeText(getBaseContext(), "Welcome to Magnus!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            finishAffinity();
                            finish();
                            startActivity(intent);
                        });
                        return true;
                    }
                    return false;
                });
                Client.getInstance().connect(target, 2457);
            }
        }, 2000); // wait 2 second to make it look like it's doing something

        Client.getInstance().addOnDisconnectListener(() -> {
            runOnUiThread(() -> {
                Toast.makeText(getBaseContext(), "Connection lost with server, closing.", Toast.LENGTH_SHORT).show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    }
                }, 2000);
            });
        });
    }
}
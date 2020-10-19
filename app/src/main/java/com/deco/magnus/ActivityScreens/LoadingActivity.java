package com.deco.magnus.ActivityScreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.GenericResult;
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
//        support.setSupportBarActive(getSupportActionBar(), true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        System.out.println("Created instance");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        String oscarsPCExt = "49.3.201.65";
        String oscarsPCInt = "192.168.0.6";
        String cysPCInt = "192.168.1.42";
        String markPC = "192.168.171.34";

        String target = oscarsPCInt;

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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                    return true;
                }
                return false;
            });
                Client.getInstance().connect(target, 2457);
            }
        }, 2000); // wait 1 second to make it look like it's doing something

        Client.getInstance().addOnDisconnectListener(() -> {
            runOnUiThread(() -> {
                Toast.makeText(getBaseContext(), "Connection lost with server, attempting reconnection", Toast.LENGTH_SHORT).show();
                Intent loadingIntent = new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loadingIntent);
            });
        });


    }
}
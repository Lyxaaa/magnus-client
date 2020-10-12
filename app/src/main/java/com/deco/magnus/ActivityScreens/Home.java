package com.deco.magnus.ActivityScreens;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.ProjectNet.Messages.GetFriendsResult;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    //Used for Gallery Image Picker
    private Uri outputFileUri;
    final int GALLERY_CODE = 27;
    final int DISPLAY_PICTURE_RESOLUTION = 256;
    final String PROFILE_IMAGE = "profile.jpg";

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setSupportActionBar(findViewById(R.id.my_toolbar));
//        support.setSupportBarActive(getSupportActionBar(), true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        user.updateFriends(friends -> {
            if (friends.result == GetFriendsResult.Result.Success) {
                GetFriendsResult friendsResult = friends;
                //TODO can't see contents of friends lambda, even though code is the exact same
//                user.unstableAddFriend(friends.);
            }
        });

        final FrameLayout gameBtn = findViewById(R.id.home_game_frame);
        final FrameLayout chatBtn = findViewById(R.id.home_chat_frame);
        final FrameLayout friendsBtn = findViewById(R.id.home_friends_frame);
        final CardView profileBtn = findViewById(R.id.profile_button);

//        final Button gameBtn = findViewById(R.id.home_game_btn);
//        final Button chatBtn = findViewById(R.id.home_chat_btn);
//        final Button friendsBtn = findViewById(R.id.home_friends_btn);
        final TextView gameTxt = findViewById(R.id.home_game_txt);
        final TextView chatTxt = findViewById(R.id.home_chat_txt);
        final TextView friendsTxt = findViewById(R.id.home_friends_txt);
        final ImageView profileImage = findViewById(R.id.profile_image);

        gameBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.home_game_onclick_frame, event);
                return false;
            }
        });

        chatBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.home_chat_onclick_frame, event);
                return false;
            }
        });

        friendsBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.home_friends_onclick_frame, event);
                return false;
            }
        });

        profileBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.profile_button, event);
                return false;
            }
        });

        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(v);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat(v);
            }
        });

        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFriends(v);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfile(v);
                profileImage.setImageURI(null);
                profileImage.setImageURI(outputFileUri);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void largeButtonPress(int frameId, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker)));
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lighter)));
        }
    }

    public void createGame(View view) {
        Intent gameScreen = new Intent(this, GameScreen.class);
        startActivity(gameScreen);
    }

    public void createChat(View view) {
        Intent chatScreen = new Intent(this, ChatScreen.class);
        startActivity(chatScreen);
    }

    public void createFriends(View view) {
        Intent friendsScreen = new Intent(this, FriendsScreen.class);
        startActivity(friendsScreen);
    }

    public void createProfile(View view) {
        openImageIntent();
    }

    private String getUniqueImageFilename() {
        return "img_"+ System.currentTimeMillis() + ".jpg";
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = getUniqueImageFilename();
//        final File sdImageMainDirectory = new File(root, fname);
//        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final Intent cameraIntent = new Intent();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final ResolveInfo cam = packageManager.resolveActivity(captureIntent, 0);
//        captureIntent.setComponent(new ComponentName(cam.activityInfo.packageName, cam.activityInfo.name));
//        captureIntent.setPackage(cam.activityInfo.packageName);
//        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, captureIntent);

        startActivityForResult(chooserIntent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                final boolean isCamera;
                if (data == null || data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }
                if (isCamera) {
                    outputFileUri = null;
                } else {
                    outputFileUri = data == null ? null : data.getData();
                }
                try {
                    user.bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                    int dim = user.bitmapImage.getHeight() > user.bitmapImage.getWidth() ? user.bitmapImage.getWidth() : user.bitmapImage.getHeight();
                    Bitmap crop = Bitmap.createBitmap(user.bitmapImage, 0, 0, dim, dim);
                    user.bitmapImage = Bitmap.createScaledBitmap(crop, DISPLAY_PICTURE_RESOLUTION, DISPLAY_PICTURE_RESOLUTION, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (user.bitmapImage != null) {
                    try {
                        FileOutputStream out = openFileOutput(PROFILE_IMAGE, MODE_PRIVATE);
                        user.bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(this, fetchProfileImage() ? "Display picture set" : "Failed to set display picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean fetchProfileImage() {
        if (user.bitmapImage == null) {
            final File imageFile = new File(getFilesDir(), PROFILE_IMAGE);
            user.bitmapImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            return true;
        }
        return false;
    }

    private void deleteProfileImage() {
        File file = new File(getFilesDir(), PROFILE_IMAGE);
        if (file.isFile()) {
            file.delete();
        }
        Toast.makeText(this, fetchProfileImage() ? "Failed to remove Profile Image" : "Profile Image removed", Toast.LENGTH_SHORT).show();
    }

    private boolean refreshProfileImage() {
        if (user.bitmapImage != null) {
            final File imageFile = new File(getFilesDir(), PROFILE_IMAGE);
        }
        return false;
    }
}
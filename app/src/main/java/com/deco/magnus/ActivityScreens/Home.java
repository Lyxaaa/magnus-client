package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
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

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.GetFriendsResult;
import com.deco.magnus.ProjectNet.Messages.Login;
import com.deco.magnus.ProjectNet.Messages.LoginResult;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.RetrieveUserProfile;
import com.deco.magnus.ProjectNet.Messages.RetrieveUserProfileResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class Home extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    //Used for Gallery Image Picker
    private Uri outputFileUri;
    final int GALLERY_CODE = 27;
    final int DISPLAY_PICTURE_RESOLUTION = 256;
    final String PROFILE_IMAGE = "profile.jpg";
    Activity activity = this;

    ImageView profileImage;

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
        /*
        user.updateFriends(friends -> {
            if (friends.result == GetFriendsResult.Result.Success) {
                GetFriendsResult friendsResult = friends;
                //TODO can't see contents of friends lambda, even though code is the exact same
//                user.unstableAddFriend(friends.);
            }
        });
         */

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
        profileImage = findViewById(R.id.profile_image);
        refreshProfileImage();

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
        return "img_" + System.currentTimeMillis() + ".jpg";
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = getUniqueImageFilename();

        // Camera.
        final Intent cameraIntent = new Intent();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final ResolveInfo cam = packageManager.resolveActivity(captureIntent, 0);
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    // Required method for startActivityForResult, grabs an image from the phones local storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) {
            if (data != null) {
                profileImage.setImageURI(data.getData());
                profileImage.refreshDrawableState();
                try {
                    Log.d("Profile", "Updating user.bitmapImage");
                    user.bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    int dim = user.bitmapImage.getHeight() > user.bitmapImage.getWidth() ? user.bitmapImage.getWidth() : user.bitmapImage.getHeight();
                    Bitmap crop = Bitmap.createBitmap(user.bitmapImage, 0, 0, dim, dim);
                    user.bitmapImage = Bitmap.createScaledBitmap(crop, DISPLAY_PICTURE_RESOLUTION, DISPLAY_PICTURE_RESOLUTION, false);
                    Log.d("Profile", "Cropping");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (user.bitmapImage != null) {
                    try {
                        FileOutputStream out = new FileOutputStream(new File(getFilesDir(), PROFILE_IMAGE));
                        user.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, out);
                        user.bitmapToBytes();
                        user.updateProfileImage(messageResult -> runOnUiThread(() -> {
                            Toast.makeText(activity, messageResult.result == MessageResult.Result.Success ? "Successfully Updated Profile Picture" : "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show();
                        }));
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                Toast.makeText(activity, updateProfileImage() ? "Changed Profile Picture" : "Failed to Change Profile Picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean updateProfileImage() {
        try {
            FileOutputStream out = openFileOutput(PROFILE_IMAGE, MODE_PRIVATE);
            user.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deleteProfileImage() {
        File file = new File(getFilesDir(), PROFILE_IMAGE);
        if (file.isFile()) {
            file.delete();
        }
        Toast.makeText(this, updateProfileImage() ? "Failed to remove Profile Image" : "Profile Image removed", Toast.LENGTH_SHORT).show();
    }

    public interface fetchProfileDataListener {
        void OnProfileDataReceive(RetrieveUserProfileResult retrieveUserProfileResult);
    }

    public static void fetchProfileData(String email, final fetchProfileDataListener listener) {
        long start = System.currentTimeMillis();
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Refresh Image", "Made it into onReceive");
                RetrieveUserProfileResult result = TryCast(dataType, data, Type.RetrieveUserProfileResult.getValue(), RetrieveUserProfileResult.class);
                if (result != null || start < System.currentTimeMillis() + 1000) {
                    listener.OnProfileDataReceive(result);
                    Log.d("Refresh Image", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new RetrieveUserProfile(email));
    }

    private void refreshProfileImage() {
        fetchProfileData(user.getEmail(), profileResult -> runOnUiThread(() -> {
            if (profileResult != null) {
                user.bitmapImage = user.bytesToBitmap(profileResult.profile);
                try {
                    FileOutputStream out = openFileOutput(PROFILE_IMAGE, MODE_PRIVATE);
                    user.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, out);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                File image = new File(getFilesDir(), PROFILE_IMAGE);
                user.bitmapImage = BitmapFactory.decodeFile(image.getAbsolutePath());
            }

            Log.d("Profile", "Init Profile Picture");
            profileImage.setImageURI(Uri.fromFile(new File(getFilesDir(), PROFILE_IMAGE)));
            profileImage.refreshDrawableState();
        }));
    }
}
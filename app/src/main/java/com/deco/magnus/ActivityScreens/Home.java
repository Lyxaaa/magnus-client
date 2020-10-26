package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.Games.Chess.GameState;
import com.deco.magnus.Netbase.ByteMsg;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.RetrieveUserProfile;
import com.deco.magnus.ProjectNet.Messages.RetrieveUserProfileResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class Home extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    //Used for Gallery Image Picker
    private Uri outputFileUri;
    final int GALLERY_CODE = 27;
    public static final int DISPLAY_PICTURE_RESOLUTION = 100;
    final String PROFILE_IMAGE = "profile.jpg";
    final Activity activity = this;
    ImageView profileImage;

    public final static String TAG = "HOME";

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
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark)));
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
                        user.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, out);
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
            user.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 70, out);
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

    public interface fetchProfileImageListener {
        void OnProfileImageReceive(byte[] profileImageResult);
    }

    public static void fetchProfileData(Activity activity, String email, final fetchProfileDataListener dataListener, final fetchProfileImageListener imageListener) {
        int imageRequestId = (int) (Math.random() * Integer.MAX_VALUE);
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    Log.d("Refresh Image", "Made it into onReceive");
                    RetrieveUserProfileResult result = TryCast(dataType, data, Type.RetrieveUserProfileResult.getValue(), RetrieveUserProfileResult.class);
                    if (result != null) {
                        dataListener.OnProfileDataReceive(result);
                        Log.d("Refresh Image", "Result: " + result);
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Successfully fetched profile data", Toast.LENGTH_SHORT).show());

                        return true;
                    }
                    return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to fetch profile data", Toast.LENGTH_SHORT).show();
                    dataListener.OnProfileDataReceive(null);
                }));
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    Log.d("Refresh Image", "Made it into onReceive");
                    byte[] result = ByteMsg.TryCast(dataType, data, imageRequestId);
                    if (result != null) {
                        imageListener.OnProfileImageReceive(result);
                        Log.d("Refresh Image", "Result: " + result.length);
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Successfully fetched profile data", Toast.LENGTH_SHORT).show());

                        return true;
                    }
                    return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to fetch profile image", Toast.LENGTH_SHORT).show();
                    imageListener.OnProfileImageReceive(null);
                }));
        Client.getInstance().threadSafeSend(new RetrieveUserProfile(email, imageRequestId));
    }

    private void refreshProfileImage() {
        fetchProfileData(activity, GameState.getInstance().getEmail(), dataResult -> {}, profileResult -> runOnUiThread(() -> {
            if (profileResult != null) {
                user.bitmapImage = user.bytesToBitmap(profileResult);
                try {
                    FileOutputStream out = openFileOutput(PROFILE_IMAGE, MODE_PRIVATE);
                    user.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 75, out);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                File image = new File(getFilesDir(), PROFILE_IMAGE);
                if( user == null) return;
                user.bitmapImage = BitmapFactory.decodeFile(image.getAbsolutePath());
            }

            Log.d("Profile", "Init Profile Picture");
            profileImage.setImageURI(Uri.fromFile(new File(getFilesDir(), PROFILE_IMAGE)));
            profileImage.refreshDrawableState();
        }));
    }
}
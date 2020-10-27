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

        final FrameLayout gameBtn = findViewById(R.id.home_game_frame);
        final FrameLayout chatBtn = findViewById(R.id.home_chat_frame);
        final FrameLayout friendsBtn = findViewById(R.id.home_friends_frame);
        final CardView profileBtn = findViewById(R.id.profile_button);

        final TextView gameTxt = findViewById(R.id.home_game_txt);
        final TextView chatTxt = findViewById(R.id.home_chat_txt);
        final TextView friendsTxt = findViewById(R.id.home_friends_txt);

        profileImage = findViewById(R.id.profile_image);

        refreshProfileImage();

        //Changes the colour of the Game button to indicate being pressed
        gameBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.home_game_onclick_frame, event);
                return false;
            }
        });

        //Changes the colour of the Chat button to indicate being pressed
        chatBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.home_chat_onclick_frame, event);
                return false;
            }
        });

        //Changes the colour of the Friends button to indicate being pressed
        friendsBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.home_friends_onclick_frame, event);
                return false;
            }
        });

        //Changes the colour of the Profile button to indicate being pressed
        profileBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.profile_button, event);
                return false;
            }
        });

        //Sets the game button action, starting the GameScreen
        gameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGame(v);
            }
        });

        //Sets the chat button action, starting the ChatScreen
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat(v);
            }
        });

        //Sets the friends button action, starting the FriendsScreen
        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFriends(v);
            }
        });

        //Sets the profile image button action, allowing the User to access and change their profile
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

    /**
     * Creates a new view of the {@link GameScreen}
     * @param view View object passed throughout different activities
     */
    public void createGame(View view) {
        Intent gameScreen = new Intent(this, GameScreen.class);
        startActivity(gameScreen);
    }

    /**
     * Creates a new view of the {@link ChatScreen}
     * @param view View object passed throughout different activities
     */
    public void createChat(View view) {
        Intent chatScreen = new Intent(this, ChatScreen.class);
        startActivity(chatScreen);
    }

    /**
     * Creates a new view of the {@link FriendsScreen}
     * @param view View object passed throughout different activities
     */
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

    /**
     * Starts a gallery process, allowing the user to select any image available on their device
     * This image data is placed into a request accessible by the rest of the application.
     * Primarily used to set/update the {@link User#bitmapImage}
     */
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

    /**
     * Updates the locally stored profile image to contain the {@link User#bitmapImage}
     * @return false if the image could not be saved or the {@link User#bitmapImage} is null,
     * returns true otherwise
     */
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

    /**
     * Deletes the local file of a profile image
     */
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

    /**
     * Fetches all profile data related to a {@link User}.
     * @param activity The activity the profile data is being requested from, allowing Toasts to be
     *                 displayed inside the correct activity.
     * @param email The email of the {@link User} whose data we are requesting
     * @param dataListener A {@link fetchProfileDataListener} waits for basic information about
     *                     the {@link User}, including email, name and bio.
     * @param imageListener A {@link fetchProfileImageListener} waits for a {@link Byte} Array
     *                      containing the requested {@link User} profile image
     */
    public static void fetchProfileData(Activity activity, String email, final fetchProfileDataListener dataListener, final fetchProfileImageListener imageListener) {
        int imageRequestId = (int) (Math.random() * Integer.MAX_VALUE);
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    Log.d("Refresh Image", "Made it into onReceive");
                    RetrieveUserProfileResult result = TryCast(dataType, data, Type.RetrieveUserProfileResult.getValue(), RetrieveUserProfileResult.class);
                    if (result != null) {
                        dataListener.OnProfileDataReceive(result);
                        Log.d("Refresh Image", "Result: " + result);
//                        activity.runOnUiThread(() -> Toast.makeText(activity, "Successfully fetched profile data", Toast.LENGTH_SHORT).show());

                        return true;
                    }
                    return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
//                    Toast.makeText(activity, "Failed to fetch profile data", Toast.LENGTH_SHORT).show();
                    dataListener.OnProfileDataReceive(null);
                }));
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    Log.d("Refresh Image", "Made it into onReceive");
                    byte[] result = ByteMsg.TryCast(dataType, data, imageRequestId);
                    if (result != null) {
                        imageListener.OnProfileImageReceive(result);
                        Log.d("Refresh Image", "Result: " + result.length);
//                        activity.runOnUiThread(() -> Toast.makeText(activity, "Successfully fetched profile data", Toast.LENGTH_SHORT).show());

                        return true;
                    }
                    return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
//                    Toast.makeText(activity, "Failed to fetch profile image", Toast.LENGTH_SHORT).show();
                    imageListener.OnProfileImageReceive(null);
                }));
        Client.getInstance().threadSafeSend(new RetrieveUserProfile(email, imageRequestId));
    }

    /**
     * Grabs the currently logged {@link User} profile image from the database and displays it on
     * their profile icon.
     * If nothing is received from the server, grabs the most recent profile image file found on
     * the device.
     */
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
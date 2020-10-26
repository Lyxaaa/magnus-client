package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.JsonMsg;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.RetrieveOtherUsers;
import com.deco.magnus.ProjectNet.Messages.RetrieveOtherUsersResult;
import com.deco.magnus.ProjectNet.Messages.SendFriendRequest;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.deco.magnus.ActivityScreens.Home.fetchProfileData;
import static com.deco.magnus.ActivityScreens.MainActivity.loggedUser;
import static com.deco.magnus.Netbase.JsonMsg.TryCast;

public class FriendsScreen extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    Activity activity = this;
    private float density;
    List<User> searchResult = new ArrayList<>();

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_main);
        user.activity = activity;

        final EditText searchFriendTxt = findViewById(R.id.search_friend_edit_text);
        final ImageView searchFriendBtn = findViewById(R.id.search_friends_asset_clickable);
        final LinearLayout friendsLayout = findViewById(R.id.search_friends_linear_layout_scroller);

        searchFriendBtn.setOnClickListener(v -> {
            String search = searchFriendTxt.getText().toString();
            searchFriends(search, (friends) -> runOnUiThread(() -> {
                searchResult = new ArrayList<>();
                friendsLayout.removeAllViews();
                if (friends.userId.length == 0) {
                    Toast.makeText(v.getContext(), "no matches found", Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < friends.userId.length; i++) {
                    User searchFriend = new User(friends.userId[i], friends.name[i], friends.email[i], friends.bio[i], null, activity);
                    fetchProfileData(activity, searchFriend.getEmail(), data -> {}, image -> {
                        if (image != null) {
                            Log.d("Friends", "Image File Length = " + image.length);
                            searchFriend.bitmapImage = searchFriend.bytesToBitmap(image);
                        } else {
                            File imageFile = new File(getFilesDir(), "profile.jpg");
                            if (imageFile.exists()) {
                                searchFriend.bitmapImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            }
                        }
                        drawSearch();
                    });
                    searchResult.add(searchFriend);

                    // get profile picture
                    // save to disk
                    // set Bitmap displayPicture to that image

                   /* ImageView friendImage = box.findViewById(R.id.img_friend_box);

                    Bitmap displayPicture = BitmapFactory.decodeFile(file.getAbsolutePath());

                    Bitmap result = Bitmap.createBitmap(Home.DISPLAY_PICTURE_RESOLUTION, Home.DISPLAY_PICTURE_RESOLUTION, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(result);
                    Paint paint = new Paint();

                    // this is to get the damned display picture in a button_photo
                    paint.setAntiAlias(true);
                    paint.setColor(Color.parseColor("#FFFFFF"));
                    canvas.drawCircle(Home.DISPLAY_PICTURE_RESOLUTION / 2, Home.DISPLAY_PICTURE_RESOLUTION / 2, Home.DISPLAY_PICTURE_RESOLUTION / 2, paint);
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(displayPicture, 0, 0, paint);

                    friendImage.setImageBitmap(result);*/
                }

            }));
        });
    }

    //TODO Change this to listen for all users when implemented
    public interface searchListener {
        void OnFriendsResult(RetrieveOtherUsersResult retrieveOtherUsersResult);
    }

    /**
     * Sends User's name and password to database to verify login information
     *
     * @return Users ID if correct, 0 otherwise
     */
    public static void searchFriends(String search, final searchListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Message", "Made it into onReceive " + ((JsonMsg) data).type);
                RetrieveOtherUsersResult result = TryCast(dataType, data, Type.RetrieveOtherUsersResult.getValue(), RetrieveOtherUsersResult.class);
                if (result != null) {
                    listener.OnFriendsResult(result);
                    Log.d("Friends Data", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new RetrieveOtherUsers(loggedUser.getEmail(), search, 10, 0));
    }

    public interface sendFriendRequestListener {
        void OnSendFriendRequestResult(MessageResult friendRequestResult);
    }

    public void sendFriendRequest(String from, String to, sendFriendRequestListener listener) {
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                Log.d("Message", "Made it into onReceive " + ((JsonMsg) data).type);
                MessageResult result = TryCast(dataType, data, Type.MessageResult.getValue(), MessageResult.class);
                if (result != null) {
                    listener.OnSendFriendRequestResult(result);
                    Log.d("Search", "Result: " + result.result);
                    return true;
                }
                return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to send friend request", Toast.LENGTH_SHORT).show();
                    listener.OnSendFriendRequestResult(null);
                }));
        Client.getInstance().threadSafeSend(new SendFriendRequest(from, to));
    }

    //region Draws each friends image
    private void drawSearch() {
        runOnUiThread(() -> {
            LinearLayout friendsLayout = findViewById(R.id.search_friends_linear_layout_scroller);
            friendsLayout.removeAllViews();
            for (User friend : searchResult) {
                View box = getLayoutInflater().inflate(R.layout.friend_box, null);
                TextView friendName = box.findViewById(R.id.txt_name);
                friendName.setText(friend.username);
                TextView friendBio = box.findViewById(R.id.txt_bio);
                friendBio.setText(friend.bio);
                ImageView profilePic = box.findViewById(R.id.img_friend_box);

                Bitmap result = Bitmap.createBitmap(Home.DISPLAY_PICTURE_RESOLUTION, Home.DISPLAY_PICTURE_RESOLUTION, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                Paint paint = new Paint();

                // this is to get the damned display picture in a button_photo
                paint.setAntiAlias(true);
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawCircle(Home.DISPLAY_PICTURE_RESOLUTION / 2, Home.DISPLAY_PICTURE_RESOLUTION / 2, Home.DISPLAY_PICTURE_RESOLUTION / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                if (friend.bitmapImage != null) {
                    canvas.drawBitmap(friend.bitmapImage, 0, 0, paint);
                } else {
                    canvas.drawBitmap(user.bitmapImage, 0, 0, paint);
                }
                profilePic.setImageBitmap(result);

                profilePic.refreshDrawableState();
                friendsLayout.addView(box);

                profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Put getConversation listener here. Open chat Id is determined by database
                        Log.d("Search", "Sending a friend request to " + friend.getEmail());
                        sendFriendRequest(user.getEmail(), friend.getEmail(), listener -> {});
                    }
                });
            }

        });
    }
    //endregion
}

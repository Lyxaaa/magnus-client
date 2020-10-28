package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.Games.Chess.GameState;
import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.JsonMsg;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.AcceptFriend;
import com.deco.magnus.ProjectNet.Messages.GetFriendsRequestingMe;
import com.deco.magnus.ProjectNet.Messages.GetFriendsRequestingMeResult;
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
        final FrameLayout searchFriendBtn = findViewById(R.id.search_frame);
        final LinearLayout friendsLayout = findViewById(R.id.search_friends_linear_layout_scroller);
        final FrameLayout acceptFriendBtn = findViewById(R.id.requests_frame);
        final LinearLayout friendsMain = findViewById(R.id.friends_main);

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
                    fetchProfileData(activity, searchFriend.getEmail(), data -> {
                    }, image -> {
                        if (image != null) {
                            Log.d("Friends", "Image File Length = " + image.length);
                            searchFriend.bitmapImage = searchFriend.bytesToBitmap(image);
                        } else {
                            File imageFile = new File(getFilesDir(), "profile.jpg");
                            if (imageFile.exists()) {
                                searchFriend.bitmapImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            }
                        }
                        drawSearch(true, friendsMain);
                    });
                    searchResult.add(searchFriend);
                }

            }));
        });

        acceptFriendBtn.setOnClickListener(v -> {
            getRequests(requests -> runOnUiThread(() -> {
                searchResult = new ArrayList<>();
                friendsLayout.removeAllViews();
                if (requests.userId.length == 0) {
                    Toast.makeText(v.getContext(), "no matches found", Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < requests.userId.length; i++) {
                    User searchFriend = new User(requests.userId[i], requests.name[i], requests.email[i], "", null, activity);
                    fetchProfileData(activity, searchFriend.getEmail(), data -> {
                        searchFriend.bio = data.bio;
                    }, image -> {
                        if (image != null) {
                            Log.d("Friends", "Image File Length = " + image.length);
                            searchFriend.bitmapImage = searchFriend.bytesToBitmap(image);
                        } else {
                            File imageFile = new File(getFilesDir(), "profile.jpg");
                            if (imageFile.exists()) {
                                searchFriend.bitmapImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                            }
                        }
                        drawSearch(false, friendsMain);
                    });
                    searchResult.add(searchFriend);
                }

            }));
        });

        //Changes the colour of the Login button to indicate being pressed
        searchFriendBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.requests_frame, event);
                return false;
            }
        });

        //Changes the colour of the Login button to indicate being pressed
        searchFriendBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                largeButtonPress(R.id.search_frame, event);
                return false;
            }
        });
    }

    public interface searchListener {
        void OnFriendsResult(RetrieveOtherUsersResult retrieveOtherUsersResult);
    }

    /**
     * Requests a set of users from the database and waits for a {@link RetrieveOtherUsersResult}
     *
     * @param search   The name to search the database with
     * @param listener Waits for a {@link RetrieveOtherUsersResult} to display the result of a
     *                 friend search. Holds basic user information, profile images must be retrieved
     *                 later
     */
    public static void searchFriends(String search, final searchListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Message", "Made it into onReceive " + ((JsonMsg) data).type);
                RetrieveOtherUsersResult result = TryCast(dataType, data,
                        Type.RetrieveOtherUsersResult.getValue(), RetrieveOtherUsersResult.class);
                if (result != null) {
                    listener.OnFriendsResult(result);
                    Log.d("Friends Data", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new RetrieveOtherUsers(
                loggedUser.getEmail(), search, 10, 0));
    }

    public interface requestsListener {
        void OnRequestsResult(GetFriendsRequestingMeResult getRequestsResult);
    }

    /**
     * Requests a set of users from the database and waits for a {@link RetrieveOtherUsersResult}
     *
     * @param listener Waits for a {@link RetrieveOtherUsersResult} to display the result of a
     *                 friend search. Holds basic user information, profile images must be retrieved
     *                 later
     */
    public static void getRequests(final requestsListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Message", "Made it into onReceive " + ((JsonMsg) data).type);
                GetFriendsRequestingMeResult result = TryCast(dataType, data,
                        Type.GetFriendsRequestingMeResult.getValue(),
                        GetFriendsRequestingMeResult.class);
                if (result != null) {
                    listener.OnRequestsResult(result);
                    Log.d("Friends Data", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new GetFriendsRequestingMe(loggedUser.getEmail()));
    }

    public interface sendFriendRequestListener {
        void OnSendFriendRequestResult(MessageResult friendRequestResult);
    }

    /**
     * Sends a friend request from the currently signed in user to the requested user
     * This doesn't immediately add the user as a friend, instead it sends them a notification
     * indicating they have a pending friend request
     *
     * @param from     The {@link User#getEmail()} of the User currently signed in
     * @param to       The {@link User#getEmail()} of the add friend button that was selected
     * @param listener Waits for a MessageResult to confirm the output of sending a friend request
     *                 If this is a {@link MessageResult.Result#Failure}, display a toast indicating
     *                 this result
     */
    public void sendFriendRequest(String from, String to, sendFriendRequestListener listener) {
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    Log.d("Message", "Made it into onReceive " + ((JsonMsg) data).type);
                    MessageResult result = TryCast(dataType, data, Type.MessageResult.getValue(),
                            MessageResult.class);
                    if (result != null) {
                        listener.OnSendFriendRequestResult(result);
                        Log.d("Search", "Result: " + result.result);
                        return true;
                    }
                    return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to send friend request",
                            Toast.LENGTH_SHORT).show();
                    listener.OnSendFriendRequestResult(null);
                }));
        Client.getInstance().threadSafeSend(new SendFriendRequest(from, to));
    }

    public interface acceptFriendRequestListener {
        void OnAcceptFriendRequestResult(MessageResult acceptRequestResult);
    }

    /**
     * Sends a friend request from the currently signed in user to the requested user
     * This doesn't immediately add the user as a friend, instead it sends them a notification
     * indicating they have a pending friend request
     *
     * @param from     The {@link User#getEmail()} of the User currently signed in
     * @param to       The {@link User#getEmail()} of the add friend button that was selected
     * @param listener Waits for a MessageResult to confirm the output of sending a friend request
     *                 If this is a {@link MessageResult.Result#Failure}, display a toast indicating
     *                 this result
     */
    public void acceptFriendRequest(String from, String to, sendFriendRequestListener listener) {
        Client.getInstance().addOnReceiveListener((socketType, dataType, data) -> {
                    Log.d("Message", "Made it into onReceive " + ((JsonMsg) data).type);
                    MessageResult result = TryCast(dataType, data, Type.MessageResult.getValue(),
                            MessageResult.class);
                    if (result != null) {
                        listener.OnSendFriendRequestResult(result);
                        Log.d("Search", "Result: " + result.result);
                        return true;
                    }
                    return false;
                },
                1000,
                () -> activity.runOnUiThread(() -> {
                    Toast.makeText(activity, "Failed to send friend request",
                            Toast.LENGTH_SHORT).show();
                    listener.OnSendFriendRequestResult(null);
                }));
        Client.getInstance().threadSafeSend(new AcceptFriend(from, to));
    }

    //region Draws each matching friends image upon searching for friends with a certain name
    private void drawSearch(Boolean findFriends, LinearLayout mainLayout) {
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

                Bitmap result = Bitmap.createBitmap(Home.DISPLAY_PICTURE_RESOLUTION,
                        Home.DISPLAY_PICTURE_RESOLUTION, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                Paint paint = new Paint();

                // this is to get the damned display picture in a button_photo
                paint.setAntiAlias(true);
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawCircle(Home.DISPLAY_PICTURE_RESOLUTION / 2,
                        Home.DISPLAY_PICTURE_RESOLUTION / 2,
                        Home.DISPLAY_PICTURE_RESOLUTION / 2, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                if (friend.bitmapImage != null) {
                    canvas.drawBitmap(friend.bitmapImage, 0, 0, paint);
                } else {
                    canvas.drawBitmap(user.bitmapImage, 0, 0, paint);
                }
                profilePic.setImageBitmap(result);

                profilePic.refreshDrawableState();
                friendsLayout.addView(box);

                if (findFriends) {
                    profilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            runOnUiThread(() -> {
                                LayoutInflater loginInflater = getLayoutInflater();
                                PopupWindow sendRequest = new PopupWindow(loginInflater.inflate(R.layout.send_request, null,
                                        false), mainLayout.getWidth(), mainLayout.getHeight(), true);
                                sendRequest.showAtLocation(mainLayout, Gravity.CENTER, 0,
                                        getSupportActionBar().getHeight());
                                View content = sendRequest.getContentView();

                                TextView status = content.findViewById(R.id.request_pop_text);
                                status.setText("Send Friend Request to " + friend.username + "?");
                                Button accept = content.findViewById(R.id.request_pop_yes);
                                accept.setText("Send");
                                Button decline = content.findViewById(R.id.request_pop_no);
                                decline.setText("Cancel");

                                accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("Search", "Sending friend request to " + friend.getEmail());
                                        sendFriendRequest(user.getEmail(), friend.getEmail(),
                                                listener -> runOnUiThread(() -> {
                                                    Toast.makeText(activity, listener.result == MessageResult.Result.Success
                                                            ? "Send Friend Request to " + friend.username
                                                            : "Failed to send friend request", Toast.LENGTH_SHORT).show();
                                                }));
                                        sendRequest.dismiss();
                                    }
                                });
                                decline.setOnClickListener(view -> {
                                    sendRequest.dismiss();
                                });
                            });
                        }
                    });
                } else {
                    profilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            runOnUiThread(() -> {
                                LayoutInflater loginInflater = getLayoutInflater();
                                PopupWindow sendRequest = new PopupWindow(loginInflater.inflate(R.layout.send_request, null,
                                        false), mainLayout.getWidth(), mainLayout.getHeight(), true);
                                sendRequest.showAtLocation(mainLayout, Gravity.CENTER, 0,
                                        getSupportActionBar().getHeight());
                                View content = sendRequest.getContentView();

                                TextView status = content.findViewById(R.id.request_pop_text);
                                status.setText("Accept Friend Request from " + friend.username + "?");
                                Button accept = content.findViewById(R.id.request_pop_yes);
                                accept.setText("Accept");
                                Button decline = content.findViewById(R.id.request_pop_no);
                                decline.setText("Decline");

                                accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("Search", "Accepting friend request from " + friend.getEmail());
                                        acceptFriendRequest(user.getEmail(), friend.getEmail(),
                                                listener -> runOnUiThread(() -> {
                                                    Toast.makeText(activity, listener.result == MessageResult.Result.Success
                                                            ? "Accepted friend request from " + friend.username
                                                            : "Failed to accept friend request", Toast.LENGTH_SHORT).show();
                                                }));
                                        sendRequest.dismiss();
                                    }
                                });
                                decline.setOnClickListener(view -> {
                                    sendRequest.dismiss();
                                });
                            });
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void largeButtonPress(int frameId, MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darker)));
        } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            findViewById(frameId).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark)));
        }
    }
}

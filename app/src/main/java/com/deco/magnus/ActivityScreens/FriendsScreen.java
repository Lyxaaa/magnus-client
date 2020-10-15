package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.deco.magnus.ProjectNet.Messages.GetFriends;
import com.deco.magnus.ProjectNet.Messages.GetFriendsResult;
import com.deco.magnus.ProjectNet.Messages.RetrieveOtherUsers;
import com.deco.magnus.ProjectNet.Messages.RetrieveOtherUsersResult;
import com.deco.magnus.ProjectNet.Messages.Login;
import com.deco.magnus.ProjectNet.Messages.LoginResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.util.ArrayList;
import java.util.List;

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

        searchFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchFriendTxt.getText().toString();
                //String newsearch = loggedUser.getEmail();
                searchFriends(search, (friends) -> runOnUiThread(() -> {
                    searchResult = new ArrayList<>();
                    friendsLayout.removeAllViews();
                    if (friends.userId.length == 0) {
                        Toast.makeText(v.getContext(), "no matches found", Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 0; i < friends.userId.length; i++) {
                        searchResult.add(new User(friends.userId[i], friends.name[i], friends.email[i], friends.bio[i], null, activity));
                        View box = getLayoutInflater().inflate(R.layout.friend_box, null);
                        TextView friendname = box.findViewById(R.id.txt_name);
                        friendname.setText(friends.name[i]);
                        TextView friendbio = box.findViewById(R.id.txt_bio);
                        friendbio.setText(friends.bio[i]);
                        friendsLayout.addView(box);
                    }
                }));
            }
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

    private void drawFriends() {
        LinearLayout layout = findViewById(R.id.search_friends_linear_layout_scroller);
        layout.removeAllViews();
        for (User friend : searchResult) {

            /*// Values related to the current displays dimensions
            int layoutSize = (int) (80 * density);

            // Initialise layout for entire clickable profile
            LinearLayout friendContainer = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            friendContainer.setOrientation(LinearLayout.VERTICAL);
            friendContainer.setPadding(0, (int) (5 * density), 0, (int) (5 * density));
            friendContainer.setLayoutParams(params);
            friendContainer.setGravity(Gravity.CENTER);
            friendContainer.setClickable(true);

            // Initialise holder for profile picture
            CardView imageContainer = new CardView(this);
            imageContainer.setLayoutParams(new CardView.LayoutParams(layoutSize, layoutSize));
            imageContainer.setRadius(250 * density);
            imageContainer.setCardBackgroundColor(getResources().getColor(R.color.yewwo));

            // Initialise profile picture
            ImageView profilePic = new ImageView(this);
            profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            profilePic.setLayoutParams(new LinearLayout.LayoutParams(layoutSize, layoutSize));
            profilePic.setImageResource(friend.profilePicDrawable);

            // Initialise profile name
            TextView profileName = new TextView(this);
            profileName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            profileName.setText(friend.username);
            profileName.setGravity(Gravity.CENTER);

            // Add views to their containers
            imageContainer.addView(profilePic);
            friendContainer.addView(imageContainer);
            friendContainer.addView(profileName);
            layout.addView(friendContainer);

            friendContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Buttons can be created here to do a certain thing for every friend we create
                }
            });*/
        }
    }
}

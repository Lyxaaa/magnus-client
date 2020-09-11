package com.deco.magnus.ActivityScreens;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Chat extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    Activity activity = this;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        support.setSupportBarActive(getSupportActionBar(), true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        user.activity = activity;
        drawFriends();
    }

    private void drawFriends() {
        user.friends.add(new User("test1", activity));
        user.friends.add(new User("test2", activity));
        user.friends.add(new User("test3", activity));
        user.friends.add(new User("test4", activity));
        LinearLayout layout = findViewById(R.id.chat_friends_linear_layout_scroller);
        Stack<Integer> testPicList = new Stack<>();
        testPicList.add(R.drawable.test);
        testPicList.add(R.drawable.chat);
        testPicList.add(R.drawable.pawn);
        testPicList.add(R.drawable.search);

        for (User friend : user.friends) {
            friend.profilePicDrawable = testPicList.pop();

            // Values related to the current displays dimensions
            float density = getResources().getDisplayMetrics().density;
            int layoutSize = (int) (80 * density);

            // Initialise layout for entire clickable profile
            LinearLayout friendContainer = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            friendContainer.setOrientation(LinearLayout.VERTICAL);
            friendContainer.setPadding(0, (int) (5 * density), 0, (int) (5 * density));
            friendContainer.setLayoutParams(params);
            friendContainer.setGravity(Gravity.CENTER);

            // Initialise holder for profile picture
            CardView imageContainer = new CardView(this);
            imageContainer.setLayoutParams(new CardView.LayoutParams(layoutSize, layoutSize));
            imageContainer.setRadius(250 * density);

            // Initialise profile picture
            ImageView profilePic = new ImageView(this);
            profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            profilePic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            profilePic.setImageResource(friend.profilePicDrawable);

            // Initialise profile name
            TextView profileName = new TextView(this);
            profileName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            profileName.setText(user.getName());
            profileName.setGravity(Gravity.CENTER);

            // Add views to their containers
            imageContainer.addView(profilePic);
            friendContainer.addView(imageContainer);
//            friendContainer.addView(profilePic);
            friendContainer.addView(profileName);
            layout.addView(friendContainer);

            friendContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
//            ScrollView scrollView = findViewById(R.id.chat_friends_scroll_view);
//            Drawable testPic = getResources().getDrawable(R.drawable.test);
//            ImageView image = new ImageView(this);
//            image.setImageDrawable(testPic);
            TextView text = new TextView(this);
            text.setLayoutParams(params);
            text.setText(friend.getName());
            layout.addView(text);
            Log.d("Friend name", friend.getName());
        }
    }


}

package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.R;
import com.deco.magnus.UserData.User;

import java.util.Stack;

public class Chat extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    Activity activity = this;
    String openChatId = " ";
    private float density;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        support.setSupportBarActive(getSupportActionBar(), true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        user.activity = activity;
        density = getResources().getDisplayMetrics().density;
        drawFriends();
        if (openChatId.equals(" ") && user.friends.size() > 0) {
            openChatId = user.friends.get(0).id;
        }
        drawChat();
        drawChat();
    }

    //region Test function for chat messages
    private void testDrawChat() {
        for (User friend : this.user.friends) {
            com.deco.magnus.UserData.Chat friendMessage = new com.deco.magnus.UserData.Chat();
            friendMessage.userId = friend.id;
            friendMessage.contents = "Hey, my email is " + friend.getEmail() + " under the ID " + friend.id;
            user.addChatMessage(friend.id, friendMessage);

            com.deco.magnus.UserData.Chat userMessage = new com.deco.magnus.UserData.Chat();
            userMessage.userId = user.id;
            userMessage.contents = "Hey, my email is " + this.user.getEmail() + " under the ID " + this.user.id;
            user.addChatMessage(friend.id, userMessage);
            Log.d("Friends id", String.valueOf(friend.id));
            Log.d("Open Chat ID", String.valueOf(openChatId));
        }
    }
    //endregion

    //region Draws the currently accessed chat region
    private void drawChat() {
        if (user.friends.size() > 0 && user.getChat(openChatId) == null) {
            testDrawChat();
        }
        if (user.getChat(openChatId) == null) {
            return;
        }
        LinearLayout layout = findViewById(R.id.chat_dialogue_linear_layout_scroller);
        layout.removeAllViews();
        for (com.deco.magnus.UserData.Chat message : user.getChat(openChatId)) {
            Log.d("Message user ID", String.valueOf(message.userId));
            // Create a new TextView to display a message
            LinearLayout messageContainer = new LinearLayout(this);
            TextView displayMessage = new TextView(this);
            int gravity;
            if (message.userId == openChatId) {
                gravity = Gravity.START;
            } else {
                gravity = Gravity.END;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            messageContainer.setOrientation(LinearLayout.VERTICAL);
            messageContainer.setPadding(0, (int) (5 * density), 0, (int) (5 * density));
            messageContainer.setLayoutParams(params);
            messageContainer.setGravity(gravity);

            FrameLayout messageBubble = new FrameLayout(this);
            messageBubble.setPadding((int) (5 * density), (int) (5 * density), (int) (5 * density), (int) (5 * density));
            messageBubble.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            messageBubble.setBackground(getResources().getDrawable(R.drawable.large_button_internal));
            messageBubble.setBackgroundColor(getResources().getColor(R.color.dark));

            displayMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            displayMessage.setText(message.contents);
            displayMessage.setTextSize(getResources().getDimension(R.dimen.chatMessageTextSize));
            displayMessage.setGravity(gravity);

            messageBubble.addView(displayMessage);
            messageContainer.addView(messageBubble);
            layout.addView(messageContainer);
        }
        findViewById(R.id.chat_dialogue_scroll_view).scrollTo(0, findViewById(R.id.chat_dialogue_scroll_view).getBottom());
    }
    //endregion

    //region Test function for friend images
    private void testDrawFriends() {
        user.friends.add(new User("test1","test1","test1","test1",null, activity));
        user.friends.add(new User("test2","test2","test2","test2", null, activity));
        user.friends.add(new User("test3","test3","test3","test3",null, activity));
        user.friends.add(new User("test4","test4","test4","test4",null, activity));
        Stack<Integer> testPicList = new Stack<>();
        testPicList.add(R.drawable.test);
        testPicList.add(R.drawable.chat);
        testPicList.add(R.drawable.pawn);
        testPicList.add(R.drawable.search);
        for (User friend : user.friends) {
            friend.profilePicDrawable = testPicList.pop();
        }
    }
    //endregion

    //region Draws each friends image
    private void drawFriends() {
        testDrawFriends();
        LinearLayout layout = findViewById(R.id.chat_friends_linear_layout_scroller);

        for (User friend : user.friends) {

            // Values related to the current displays dimensions
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
            profileName.setText(friend.getEmail());
            profileName.setGravity(Gravity.CENTER);

            // Add views to their containers
            imageContainer.addView(profilePic);
            friendContainer.addView(imageContainer);
            friendContainer.addView(profileName);
            layout.addView(friendContainer);

            friendContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openChatId = friend.id;
                    drawChat();
                }
            });
        }
    }
    //endregion


}

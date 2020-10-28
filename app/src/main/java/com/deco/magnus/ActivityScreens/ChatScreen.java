package com.deco.magnus.ActivityScreens;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.JsonMsg;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.Login;
import com.deco.magnus.ProjectNet.Messages.LoginResult;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.SendMessage;
import com.deco.magnus.ProjectNet.Messages.SendMessageResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;
import com.deco.magnus.UserData.Chat;
import com.deco.magnus.UserData.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static com.deco.magnus.ActivityScreens.Home.fetchProfileData;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ChatScreen extends AppCompatActivity {
    GlobalSupport support = new GlobalSupport();
    User user = MainActivity.getLoggedUser();
    Activity activity = this;
    String openChatId = " ";
    private float density;

    @Override
    public boolean onSupportNavigateUp() {
        LinearLayout layout = findViewById(R.id.chat_friends_linear_layout_scroller);
        layout.removeAllViews();
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
        user.updateFriends(friendList -> {
            Log.d("Friends", "Start List");
            List<User> friends = new ArrayList<>();
            for (int i = 0; i < friendList.userId.length; i++) {
                AtomicInteger j = new AtomicInteger(i);
                User friend = new User(friendList.userId[i], friendList.name[i], friendList.email[i], "", null, activity);
                fetchProfileData(activity, friend.getEmail(), data -> {}, image -> {
                    //Adds each friends profile image to their respective User Object
                    String imageName = getImageName(friend.conversationId);
                    if (image != null) {
                        Log.d("Friends", "Image File Length = " + image.length);
                        friend.bitmapImage = friend.bytesToBitmap(image);
                        try {
                            FileOutputStream out = openFileOutput(imageName, MODE_PRIVATE);
                            friend.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        File imageFile = new File(getFilesDir(), imageName);
                        if (imageFile.exists()) {
                            friend.bitmapImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        } else {
                            try {
                                friend.bitmapImage = user.bitmapImage;
                                FileOutputStream out = openFileOutput(imageName, MODE_PRIVATE);
                                friend.bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.d("Profile", "Init Profile Picture");
                    friend.bitmapImage = friend.bytesToBitmap(image); //idk why this is here
                    Log.d("Friends", "Finish List");
                    user.setFriends(friends);
                    Log.d("Friends", "Drew Friends");
                    if (user.getFriends().size() > 0) {
                        openChatId = user.getFriends().get(0).conversationId;
                    }
                    drawFriends();
                    drawChat();
                });
                friend.conversationId = friendList.conversationId[i];
                friends.add(friend);
            }

        });

        final FrameLayout sendMessageBtn = findViewById(R.id.chat_send_frame);
        final EditText sendMessageText = findViewById(R.id.chat_edit_text_input);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendMessageText.getText().toString().trim().length() > 0) {
                    sendMessage(sendMessageText.getText().toString(), messageResult -> runOnUiThread(() -> {
                        user.addChatMessage(openChatId,
                                new Chat(user.id, user.getEmail(), sendMessageText.getText().toString(),
                                        System.currentTimeMillis(), messageResult.result));
                        drawChat();
                        sendMessageText.getText().clear();
                    }));
                }
            }
        });
    }

    /**
     * Used to decide the name to give a profile image when saving it to the {@link User} device
     * storage. Ensures consistent naming to prevent human error.
     * @param conversationId The ID of the conversation which the related {@link User}'s profile
     *                       image will be saved under
     * @return A String indicating the name the profile image should be saved under
     */
    public String getImageName(String conversationId) {
        return conversationId + ".jpg";
    }

    public interface sendMessageResultListener {
        void OnSendMessageResult(SendMessageResult sendMessageResult);
    }

    /**
     * Safely sends a {@link Chat} message to the server
     * @param message The contents of the message to send to the server
     * @param listener
     */
    private void sendMessage(String message, sendMessageResultListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Send Message", "Made it into onReceive");
                //TODO There's no type for MessageResult, meaning no way to receive a generic MessageResult from the server
                SendMessageResult result = JsonMsg.TryCast(dataType, data, Type.SendMessageResult.getValue(), SendMessageResult.class);
                if (result != null) {
                    listener.OnSendMessageResult(result);
                    Log.d("SendMessage", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new SendMessage(user.getEmail(), message, openChatId));
    }

    private void getConversation(String userId) {

    }

    //region Test function for chat messages
    private void testDrawChat() {
        for (User friend : this.user.getFriends()) {
            Chat friendMessage = new Chat(friend.id, friend.getEmail(),
                    "Hey, my email is " + friend.getEmail() + " under the ID " + friend.id,
                    System.currentTimeMillis());
            user.addChatMessage(friend.id, friendMessage);

            Chat userMessage = new Chat(user.id, this.user.getEmail(),
                    "Hey, my email is " + this.user.getEmail() + " w/ ID " + this.user.id,
                    System.currentTimeMillis());
            user.addChatMessage(friend.id, userMessage);
            Log.d("Friends id", String.valueOf(friend.id));
            Log.d("Open Chat ID", String.valueOf(openChatId));
        }
    }
    //endregion

    //make sure to put drawChat inside getConversation listener to ensure operation
    //region Draws the currently accessed chat region
    private void drawChat() {
        Log.d("Chat", "Drawing chat for ID " + openChatId);
        user.getChatFromServer(openChatId, chat -> runOnUiThread(() -> {
//        if (user.getFriends().size() > 0 && user.getChat(openChatId) == null) {
//            testDrawChat();
//        }
            user.clearChat(openChatId);
            for (int pos = chat.chat.length - 1; pos >= 0; pos--) {
                chat.chat[pos].result = MessageResult.Result.Success;
                user.addChatMessage(openChatId, chat.chat[pos]);
            }
            if (user.getChat(openChatId) == null) {
                return;
            }
            LinearLayout layout = findViewById(R.id.chat_dialogue_linear_layout_scroller);
            layout.removeAllViews();
            for (Chat message : user.getChat(openChatId)) {
                Log.d("Message user ID", message.email);
                // Create a new TextView to display a message
                layout.addView(messageToLinearLayout(message));
            }
            findViewById(R.id.chat_dialogue_scroll_view).scrollTo(0, findViewById(R.id.chat_dialogue_linear_layout_scroller).getBottom());
        }));
    }
    //endregion

    private void updateChat() {
    }

    /**
     * Creates a new {@link LinearLayout}-formatted {@link Chat} that can be inserted into the {@link ChatScreen}
     * @param message Contents of the message to be displayed
     * @return {@link LinearLayout} containing a stylised message
     */
    private LinearLayout messageToLinearLayout(Chat message) {
        LinearLayout messageContainer = new LinearLayout(this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        messageContainer.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams messageBubbleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView displayMessage = new TextView(this);
        int gravity;
        int bubbleColour;
        int textColour;
        if (!message.email.equals(user.getEmail())) {
            params.setMargins((int) (2 * density), (int) (5 * density), (int) (10 * density), (int) (5 * density));
            messageBubbleParams.setMargins((int) (5 * density), (int) (3 * density), (int) (100 * density), (int) (3 * density));
            messageContainer.setLayoutParams(params);
            bubbleColour = getResources().getColor(R.color.lighter);
            textColour = getResources().getColor(R.color.black);
//            messageContainer.setPadding((int) (2 * density), (int) (5 * density), (int) (10 * density), (int) (5 * density));
            gravity = Gravity.START;
            messageContainer.setGravity(gravity);
        } else {
            params.setMargins((int) (50 * density), (int) (5 * density), (int) (2 * density), (int) (5 * density));
            messageBubbleParams.setMargins((int) (100 * density), (int) (3 * density), (int) (5 * density), (int) (3 * density));
            messageContainer.setLayoutParams(params);
            bubbleColour = getResources().getColor(R.color.blue);
            textColour = getResources().getColor(R.color.white);
//            messageContainer.setPadding((int) (50 * density), (int) (5 * density), (int) (2 * density), (int) (5 * density));
            gravity = Gravity.END;
            messageContainer.setGravity(gravity);
        }

        if (message.result != MessageResult.Result.Success) {
            bubbleColour = getResources().getColor(R.color.design_default_color_error);
        }

        messageContainer.setGravity(gravity);

        FrameLayout messageBubble = new FrameLayout(this);
        messageBubble.setPadding((int) (10 * density), (int) (2 * density), (int) (10 * density), (int) (2 * density));
        messageBubble.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        messageBubble.setBackground(getResources().getDrawable(R.drawable.small_button_internal_round));
        messageBubble.setBackgroundTintList(ColorStateList.valueOf(bubbleColour));
        messageBubble.setLayoutParams(messageBubbleParams);

//        messageBubble.setBackgroundColor(getResources().getColor(R.color.dark));

        displayMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        displayMessage.setText(message.text);
        displayMessage.setTextSize(getResources().getDimension(R.dimen.chatMessageTextSize));
        displayMessage.setTextColor(textColour);
        displayMessage.setGravity(gravity);

        if (message.result != MessageResult.Result.Success) {
            TextView errorMessage = new TextView(this);
            errorMessage.setText(getString(R.string.message_not_sent));
            errorMessage.setGravity(gravity);
            errorMessage.setTextColor(getResources().getColor(R.color.design_default_color_error));
            messageContainer.addView(errorMessage);
        }
        messageBubble.addView(displayMessage);
        messageContainer.addView(messageBubble);
        return messageContainer;
    }

    //region Test function for friend images
    private void testDrawFriends() {
        user.unstableAddFriend(new User("test1","test1","test1","test1",null, activity));
        user.unstableAddFriend(new User("test2","test2","test2","test2", null, activity));
        user.unstableAddFriend(new User("test3","test3","test3","test3",null, activity));
        user.unstableAddFriend(new User("test4","test4","test4","test4",null, activity));
        Stack<Integer> testPicList = new Stack<>();
        testPicList.add(R.drawable.test);
        testPicList.add(R.drawable.chat);
        testPicList.add(R.drawable.pawn);
        testPicList.add(R.drawable.search);
        for (User friend : user.getFriends()) {
            friend.profilePicDrawable = testPicList.pop();
        }
    }
    //endregion

    //region Draws each friends image
    private void drawFriends() {
        runOnUiThread(() -> {
    //        if (user.getFriends().size() == 0) {
    //            testDrawFriends();
    //        }
            LinearLayout layout = findViewById(R.id.chat_friends_linear_layout_scroller);
            layout.removeAllViews();
            for (User friend : user.getFriends()) {

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
    //                profilePic.setImageBitmap(user.bytesToBitmap(imageData));
                    File image = new File(getFilesDir(), getImageName(friend.conversationId));
                    Log.d("Friends", "Conversation ID for Image = " + friend.conversationId + " With image name = " + getImageName(friend.conversationId));
                    if (image.exists()) {
                        Log.d("Friends", "Image Found");
                        profilePic.setImageURI(Uri.fromFile(new File(getFilesDir(), getImageName(friend.conversationId))));
                    } else {
                        Log.d("Friends", "Image Not Found");
                        profilePic.setImageURI(Uri.fromFile(new File(getFilesDir(), "profile.jpg")));
                    }
                    profilePic.refreshDrawableState();

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
                            //TODO Put getConversation listener here. Open chat Id is determined by database
                            openChatId = friend.conversationId;
                            drawChat();
                        }
                    });
            }

        });
    }
    //endregion


}

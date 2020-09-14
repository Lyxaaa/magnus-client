package com.deco.magnus.UserData;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.ImageView;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.Login;
import com.deco.magnus.ProjectNet.Messages.LoginResult;
import com.deco.magnus.ProjectNet.Messages.Message;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.R;
import com.deco.magnus.ResourceDirectory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class User extends Message {
    public class IncorrectCredentialsException extends Exception {
        public IncorrectCredentialsException(String errorMessage) {
            super(errorMessage);
        }
    }
    // Integer should be the ID of the receiving user (not the ID of this user)
    private Map<Integer, List<ChatMessage>> chats = new ArrayMap<>();
    public Activity activity;
    public List<User> friends = new ArrayList<>();
    private final String email;
    public final int id;
    private final boolean authorised;
    private String bio;
    private byte[] byteImage;
    private Bitmap bitmapImage;
    public int profilePicDrawable;

//    private User(int id, String username, String bio, byte[] profilePic) {
//        this.id = id;
//        this.username = username;
//        this.bio = bio;
//        this.byteImage = profilePic;
//        this.bitmapImage = bytesToBitmap(profilePic);
//    }

    public User(String username, String password, Activity activity) throws IncorrectCredentialsException {
        this.activity = activity;
        UserInfo userInfo = login(username, password);
        this.authorised = true;
        this.email = userInfo.email;
        this.bio = userInfo.bio;
        this.byteImage = userInfo.profilePic;
        this.id = emailHash();
//        this.bitmapImage = bytesToBitmap(userInfo.profilePic);
    }

    public User(String username, Activity activity) {
        this.activity = activity;
        UserInfo userInfo = userExists(username);
        this.authorised = false;
        this.email = userInfo.email;
        this.bio = userInfo.bio;
        this.byteImage = userInfo.profilePic;
        this.id = emailHash();
//        this.bitmapImage = bytesToBitmap(userInfo.profilePic);
    }

    public UserInfo register(String email, String pword, File image)
            throws IncorrectCredentialsException {
        UserInfo userInfo = new UserInfo();
        userInfo.passwordCorrect = true;
        userInfo.email = email;
//        userInfo.profilePic =
        //Make request for addition to user info here, return null if user already exists
        if (userInfo == null) {
            throw new IncorrectCredentialsException("Email " + email + " is already in use. " +
                    "Please log in with this email, or register with a different email");
        }
        return userInfo;
    }

    /**
     * Used to create a User Object for the local User
     * @param email
     * @param pword
     * @throws IncorrectCredentialsException
     */
    public UserInfo login(String email, String pword) throws IncorrectCredentialsException {
        UserInfo userInfo = detailsCorrect(email, pword);
        if (userInfo == null) {
            throw new IncorrectCredentialsException("User " + email + " does not exist");
        } else if (!userInfo.passwordCorrect) {
            throw new IncorrectCredentialsException("Password for " + email + " is incorrect");
        }
        return userInfo;
    }

    public void getUser(String name) {

    }

    /**
     * Asks the database if a user exists, database sends back a {@link UserInfo} Object
     * @param username User's username
     * @return {@link UserInfo} Object
     */
    private UserInfo userExists(String username) {
        //Ask database for this object
        UserInfo userDataRequest = new UserInfo();
        userDataRequest.bio = "Some info about me";
        userDataRequest.email = username;
        userDataRequest.passwordCorrect = true;
//        userDataRequest.profilePic = imageToBytes("test.jpg");
        userDataRequest.profilePic = null;
        //Ask database for this object
        return userDataRequest;
    }

    public Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public Bitmap imageToBitmap(String imageName) {
        return bytesToBitmap(imageToBytes(imageName));
    }


    /**
     * FUNCTION DO NOT DO THE OPERATE OF PROPERLY YES
     * @param imageName
     * @return
     */
    public byte[] imageToBytes(String imageName) {
//        final File imageFile = new File(activity.getResources().getDrawable(R.drawable.test));
        final File imageFile = new File("borked");
        Log.d("UserLogger", imageFile.getAbsolutePath());
        Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 0, blob);
        return blob.toByteArray();
    }

    /**
     * Sends User's name and password to database to verify login information
     *
     * @param name User's name
     * @param pword User's password
     * @return Users ID if correct, 0 otherwise
     */
    private UserInfo detailsCorrect(String name, String pword) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public void OnReceive(SocketType socketType, DataType dataType, Object data) {
                LoginResult result = TryCast(dataType, data, Type.LoginResult.getValue(), LoginResult.class);
                if (result != null) {
                    Client.getInstance().removeOnReceiveListener(this);
                    Log.d("Login Data", result.userName);
                }
            }
        });

        new Thread(() -> {
            try {
                Client.getInstance().send(new Login(name, pword));
            } catch (Exception e) {
                Log.e("Login", e.toString());
            }
        }).start();

        UserInfo userDataRequest = userExists(name);
        if (userDataRequest != null &&
                name.toLowerCase().equals(userDataRequest.email.toLowerCase()) &&
                userDataRequest.passwordCorrect) {
            //Make a server request for all of this information
            return userDataRequest;
        }
        //Make server request here, sending name and password
        return null;
    }

    public String getEmail() {
        return this.email;
    }

    /**
     *
     * @param id The ID of the user in the conversation that is NOT this user
     * @param chat A List {@link ChatMessage} Objects to add to the chats Map
     */
    public void addChat(int id, List<ChatMessage> chat) {
        if (chat != null && !chat.isEmpty()) {
            if (chats.containsKey(id)) {
                chats.get(id).addAll(chat);
            } else {
                chats.put(id, chat);
            }
        }
    }

    /**
     *
     * @param id The ID of the user in the conversation that is NOT this user
     * @param message A {@link ChatMessage} Object to add to the chats Map
     */
    public void addChatMessage(int id, ChatMessage message) {
        if (message != null) {
            if (chats.containsKey(id)) {
                chats.get(id).add(message);
            } else {
                List<ChatMessage> msgList = new ArrayList<>();
                msgList.add(message);
                chats.put(id, msgList);
            }
        }
    }

    /**
     * Gets the chat history between this user and the user with the given ID
     * @param id The ID of the user in the conversation that is NOT this user
     * @return null if the chat does not exist
     */
    public List<ChatMessage> getChat(int id) {
        if (chats.containsKey(id)) {
            return chats.get(id);
        }
        return null;
    }

    public int emailHash() {
        int hash = 1;
        for (Character character : email.toCharArray()) {
            hash *= character;
        }
        return Math.abs(hash);
    }
}


package com.deco.magnus.UserData;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.ArrayMap;
import android.util.Log;

import com.deco.magnus.Netbase.ByteMsg;
import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.ProjectNet.Client;
import com.deco.magnus.ProjectNet.Messages.GetFriends;
import com.deco.magnus.ProjectNet.Messages.GetFriendsResult;
import com.deco.magnus.ProjectNet.Messages.Login;
import com.deco.magnus.ProjectNet.Messages.LoginResult;
import com.deco.magnus.ProjectNet.Messages.Message;
import com.deco.magnus.ProjectNet.Messages.MessageResult;
import com.deco.magnus.ProjectNet.Messages.RegisterUser;
import com.deco.magnus.ProjectNet.Messages.RetrieveMessages;
import com.deco.magnus.ProjectNet.Messages.RetrieveMessagesResult;
import com.deco.magnus.ProjectNet.Messages.Type;
import com.deco.magnus.ProjectNet.Messages.UpdateUserProfile;

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
    // String should be the ID of the receiving user (or maybe the conversation ID) (not the ID of this user)
    private Map<String, List<Chat>> chats = new ArrayMap<>();
    public Activity activity;
    private List<User> friends = new ArrayList<>();
    public final String username;
    private final String email;
    public final String id;
    private boolean authorised;
    public String bio;
    public String conversationId;
    public Bitmap bitmapImage;
    public int profilePicDrawable;

    public User(String id, String username, String email, String bio, byte[] profilePic, Activity activity) {
        this.activity = activity;
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.bitmapImage = bytesToBitmap(profilePic);
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

//    /**
//     * Used to create a User Object for the local User
//     * @param email
//     * @param pword
//     * @throws IncorrectCredentialsException
//     */
//    public UserInfo login(String email, String pword) throws IncorrectCredentialsException {
//        UserInfo userInfo;
//        detailsCorrect(email, pword, info -> {
//            userInfo = info;
//        });
//        if (userInfo == null) {
//            throw new IncorrectCredentialsException("User " + email + " does not exist");
//        } else if (!userInfo.passwordCorrect) {
//            throw new IncorrectCredentialsException("Password for " + email + " is incorrect");
//        }
//        return userInfo;
//    }

    public void getUser(String name) {

    }

    public interface getFriendsResultListener {
        void OnGetFriendsResult(GetFriendsResult getFriendsResult);
    }

    public void updateFriends(getFriendsResultListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Update Friends", "Made it into onReceive");
                GetFriendsResult result = TryCast(dataType, data, Type.GetFriendsResult.getValue(), GetFriendsResult.class);
                if (result != null) {
                    listener.OnGetFriendsResult(result);
                    Log.d("Login Data", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new GetFriends(this.email));
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friendList) {
        friends = friendList;
    }

    public void unstableAddFriend(User friend) {
        friends.add(friend);
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
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
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

    public byte[] bitmapToBytes() {
        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, blob);
        return blob.toByteArray();
    }

    public interface loginResultListener {
        void OnLoginResult(LoginResult loginResult);
    }

    /**
     * Sends User's name and password to database to verify login information
     *
     * @param name User's name
     * @param pword User's password
     * @return Users ID if correct, 0 otherwise
     */
    public static void authenticateUser(String name, String pword, final loginResultListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Login Info", "Made it into onReceive");
                LoginResult result = TryCast(dataType, data, Type.LoginResult.getValue(), LoginResult.class);
                if (result != null) {
                    listener.OnLoginResult(result);
                    Log.d("Login Data", "Result: " + result);
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new Login(name, pword));
    }

    public static void registerUser(String name, String pword, final loginResultListener listener) {
        Client.getInstance().addOnReceiveListener(new com.deco.magnus.Netbase.Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                Log.d("Registration Info", "Made it into onReceive");
                LoginResult result = TryCast(dataType, data, Type.LoginResult.getValue(), LoginResult.class);
                if (result != null) {
                    listener.OnLoginResult(result);
                    Log.d("Registration Data", "Result: " + result.result.getValue());
                    return true;
                }
                return false;
            }
        });
        Client.getInstance().threadSafeSend(new RegisterUser(name, pword, name, "Hey I am new here"));
    }

    public interface updateProfileImageListener {
        void OnUpdateProfileImageResult(MessageResult messageResult);
    }

    public void updateProfileImage(final updateProfileImageListener listener) {
        Client.getInstance().addOnReceiveListener(new Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                MessageResult result = TryCast(dataType, data, Type.MessageResult.getValue(), MessageResult.class);
                if (result != null) {
                    listener.OnUpdateProfileImageResult(result);
                    Log.d("Profile Image", "Result: " + result.result);
                    return true;
                }
                return false;
            }
        });
        Log.d("Profile", "Sending image to server");

        Client.getInstance().threadSafeSend(Type.ByteUpdateProfileImage.getValue(), bitmapToBytes());
    }

    public String getEmail() {
        return this.email;
    }

    /**
     *
     * @param id The ID of the user in the conversation that is NOT this user
     * @param chat A List {@link Chat} Objects to add to the chats Map
     */
    public void addChat(String id, List<Chat> chat) {
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
     * @param id The ID of the desired conversation
     * @param message A {@link Chat} Object to add to the chats Map
     */
    public void addChatMessage(String id, Chat message) {
        if (message != null) {
            if (chats.containsKey(id)) {
                chats.get(id).add(message);
            } else {
                List<Chat> msgList = new ArrayList<>();
                msgList.add(message);
                chats.put(id, msgList);
            }
        }
    }

    public void clearChat(String conversationId) {
        if (chats.containsKey(conversationId) && chats.get(conversationId) != null) {
            chats.put(conversationId, new ArrayList<>());
        }
    }

    /**
     * Gets the chat history between this user and the user with the given ID
     * @param id The ID of the user in the conversation that is NOT this user
     * @return null if the chat does not exist
     */
    public List<Chat> getChat(String id) {
        if (chats.containsKey(id)) {
            return chats.get(id);
        }
        return null;
    }

    public interface getChatListener {
        void OnChatResult(RetrieveMessagesResult retrieveMessagesResult);
    }

    /**
     * Gets the chat history between this user and the user with the given ID directly from server
     * @param id The ID of the user in the conversation that is NOT this user
     */
    public void getChatFromServer(String id, getChatListener listener) {
        Client.getInstance().addOnReceiveListener(new Client.OnReceiveListener() {
            @Override
            public boolean OnReceive(SocketType socketType, DataType dataType, Object data) {
                RetrieveMessagesResult result = TryCast(dataType, data, Type.RetrieveMessagesResult.getValue(), RetrieveMessagesResult.class);
                if (result != null) {
                    listener.OnChatResult(result);
                    Log.d("Friends", "Chat: " + result.chat[0].email);
                    return true;
                }
                return false;
            }
        });
        Log.d("Profile", "Sending image to server");
        Client.getInstance().threadSafeSend(new RetrieveMessages(id, 0));
    }

    public int emailHash() {
        int hash = 1;
        for (Character character : email.toCharArray()) {
            hash *= character;
        }
        return Math.abs(hash);
    }
}


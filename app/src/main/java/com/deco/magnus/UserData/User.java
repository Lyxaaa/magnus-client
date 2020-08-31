package com.deco.magnus.UserData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class User {
    public class IncorrectCredentialsException extends Exception {
        public IncorrectCredentialsException(String errorMessage) {
            super(errorMessage);
        }
    }
    private final int id;
    private final String username;
    private final boolean authorised;
    private String bio;
    private byte[] byteImage;
    private Bitmap bitmapImage;
//    private final String bio;

//    private User(int id, String username, String bio, byte[] profilePic) {
//        this.id = id;
//        this.username = username;
//        this.bio = bio;
//        this.byteImage = profilePic;
//        this.bitmapImage = bytesToBitmap(profilePic);
//    }

    public User(String username, String password) throws IncorrectCredentialsException {
        UserInfo userInfo = login(username, password);
        this.authorised = true;
        this.id = userInfo.id;
        this.username = userInfo.username;
        this.bio = userInfo.bio;
        this.byteImage = userInfo.profilePic;
//        this.bitmapImage = bytesToBitmap(userInfo.profilePic);
    }

    public User(String username) {
        UserInfo userInfo = userExists(username);
        this.authorised = false;
        this.id = userInfo.id;
        this.username = userInfo.username;
        this.bio = userInfo.bio;
        this.byteImage = userInfo.profilePic;
//        this.bitmapImage = bytesToBitmap(userInfo.profilePic);
    }

    /**
     * Used to create a User Object for the local User
     * @param username
     * @param pword
     * @throws IncorrectCredentialsException
     */
    public UserInfo login(String username, String pword) throws IncorrectCredentialsException {
        UserInfo userInfo = detailsCorrect(username, pword);
        if (userInfo == null) {
            throw new IncorrectCredentialsException("User " + username + " does not exist");
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
        userDataRequest.id = 123456;
        userDataRequest.username = "roger";
        userDataRequest.passwordCorrect = true;
//        userDataRequest.profilePic = imageToBytes("test.jpg");
        userDataRequest.profilePic = null;
        //Ask database for this object
        return userDataRequest;
    }

    private Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private Bitmap imageToBitmap(String imageName) {
        return bytesToBitmap(imageToBytes(imageName));
    }

    private byte[] imageToBytes(String imageName) {
        final File imageFile = new File("drawable/test.jpg");
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
        UserInfo userDataRequest = userExists(name);
        if (userDataRequest != null &&
                name.toLowerCase().equals(userDataRequest.username.toLowerCase()) &&
                userDataRequest.passwordCorrect) {
            //Make a server request for all of this information
            return userDataRequest;
        }
        //Make server request here, sending name and password
        return null;
    }

    public String getName() {
        return username;
    }

    public int getId() {
        return id;
    }
}


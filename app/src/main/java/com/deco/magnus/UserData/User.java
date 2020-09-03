package com.deco.magnus.UserData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.deco.magnus.ProjectNet.Messages.Message;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class User extends Message {
    public class IncorrectCredentialsException extends Exception {
        public IncorrectCredentialsException(String errorMessage) {
            super(errorMessage);
        }
    }
    private final String email;
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
        this.email = userInfo.email;
        this.bio = userInfo.bio;
        this.byteImage = userInfo.profilePic;
//        this.bitmapImage = bytesToBitmap(userInfo.profilePic);
    }

    public User(String username) {
        UserInfo userInfo = userExists(username);
        this.authorised = false;
        this.email = userInfo.email;
        this.bio = userInfo.bio;
        this.byteImage = userInfo.profilePic;
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
        userDataRequest.email = "roger";
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

    public byte[] imageToBytes(String imageName) {
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
                name.toLowerCase().equals(userDataRequest.email.toLowerCase()) &&
                userDataRequest.passwordCorrect) {
            //Make a server request for all of this information
            return userDataRequest;
        }
        //Make server request here, sending name and password
        return null;
    }

    public String getName() {
        return this.email;
    }

}


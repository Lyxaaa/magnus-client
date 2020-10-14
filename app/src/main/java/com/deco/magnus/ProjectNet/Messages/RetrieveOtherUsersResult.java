package com.deco.magnus.ProjectNet.Messages;

public class RetrieveOtherUsersResult extends MessageResult {
    String[] userId;
    String[] email;
    String[] name;
    String[] bio;


    public RetrieveOtherUsersResult(String[] name, String[] userId, String[] email, String[] bio) {
        this.type = Type.RetrieveOtherUsersResult.getValue();
        this.name = name;
        this.userId = userId;
        this.email = email;
        this.bio = bio;
    }
}
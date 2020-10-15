package com.deco.magnus.ProjectNet.Messages;

public class RetrieveOtherUsersResult extends MessageResult {
    public String[] userId;
    public String[] email;
    public String[] name;
    public String[] bio;


    public RetrieveOtherUsersResult(String[] name, String[] userId, String[] email, String[] bio) {
        this.type = Type.RetrieveOtherUsersResult.getValue();
        this.name = name;
        this.userId = userId;
        this.email = email;
        this.bio = bio;
    }
}
package com.deco.magnus.ProjectNet.Messages;

public class GetFriendsRequestingMeResult extends MessageResult {
    String[] name;
    String[] userId;
    String[] email;

    public GetFriendsRequestingMeResult(String[] name, String[] userId, String[] email) {
        this.type = Type.GetFriendsRequestingMeResult.getValue();
        this.name = name;
        this.userId = userId;
        this.email = email;
    }
}

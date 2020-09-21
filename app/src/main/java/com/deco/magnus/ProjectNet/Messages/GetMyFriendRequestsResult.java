package com.deco.magnus.ProjectNet.Messages;

public class GetMyFriendRequestsResult extends MessageResult {
    String[] name;
    String[] userId;
    String[] email;

    public GetMyFriendRequestsResult(String[] name, String[] userId, String[] email) {
        this.type = Type.GetMyFriendRequestsResult.getValue();
        this.name = name;
        this.userId = userId;
        this.email = email;
    }
}

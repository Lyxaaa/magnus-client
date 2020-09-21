package com.deco.magnus.ProjectNet.Messages;

public class GetMyFriendRequests extends Message {
    String email;

    public GetMyFriendRequests(String email) {
        this.type = Type.GetMyFriendRequests.getValue();
        this.email = email;
    }
}

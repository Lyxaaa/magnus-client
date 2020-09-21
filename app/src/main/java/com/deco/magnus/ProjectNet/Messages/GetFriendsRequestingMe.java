package com.deco.magnus.ProjectNet.Messages;

public class GetFriendsRequestingMe extends Message {
    String email;

    public GetFriendsRequestingMe(String email) {
        this.type = Type.GetFriendsRequestingMe.getValue();
        this.email = email;
    }
}

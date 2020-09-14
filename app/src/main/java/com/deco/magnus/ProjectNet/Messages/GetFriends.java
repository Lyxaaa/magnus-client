package com.deco.magnus.ProjectNet.Messages;

/**
 * Sends a User's email to request a list of their friends (as emails)
 * Should receive a FriendList in return
 */
public class GetFriends extends Message {
    public String email;

    public GetFriends(String email) {
        this.type = Type.GetFriends.getValue();
        this.email = email;
    }
}

package com.deco.magnus.ProjectNet.Messages;

/**
 * Sends a {@link com.deco.magnus.UserData.User}'s email to request a list of their friends (as
 * emails)
 * Should receive a {@link GetFriendsResult} in return
 */
public class GetFriends extends Message {
    public String email;

    public GetFriends(String email) {
        this.type = Type.GetFriends.getValue();
        this.email = email;
    }
}

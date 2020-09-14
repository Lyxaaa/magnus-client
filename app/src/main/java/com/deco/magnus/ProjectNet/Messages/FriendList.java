package com.deco.magnus.ProjectNet.Messages;

/**
 * Contains an array of user emails (Used to list a user's friends)
 */
public class FriendList extends Message {
    public String[] emails;

    public FriendList(String[] emails) {
        this.type = Type.GetFriends.getValue();
        this.emails = emails;
    }
}

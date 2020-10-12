package com.deco.magnus.ProjectNet.Messages;

public class GetFriendsResult extends MessageResult {
    public String[] name;
    public String[] userId;
    public String[] email;
    public String[] conversationId;

    public GetFriendsResult(String[] name, String[] userId, String[] email, String[] conversationId) {
        this.type = Type.GetFriendsResult.getValue();
        this.name = name;
        this.userId = userId;
        this.email = email;
        this.conversationId = conversationId;
    }
}

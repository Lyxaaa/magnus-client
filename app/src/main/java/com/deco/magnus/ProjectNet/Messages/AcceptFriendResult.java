package com.deco.magnus.ProjectNet.Messages;

public class AcceptFriendResult extends Message {
    Result success;
    String conversationId;

    public AcceptFriendResult(Result success, String conversationId) {
        this.type = Type.AcceptFriendResult.getValue();
        this.success = success;
        this.conversationId = conversationId;
    }
}

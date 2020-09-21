package com.deco.magnus.ProjectNet.Messages;

public class AcceptFriendResult extends MessageResult {
    String conversationId;

    public AcceptFriendResult(String conversationId) {
        this.type = Type.AcceptFriendResult.getValue();
        this.conversationId = conversationId;
    }
}

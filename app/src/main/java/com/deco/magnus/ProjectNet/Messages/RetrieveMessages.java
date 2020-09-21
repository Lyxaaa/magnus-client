package com.deco.magnus.ProjectNet.Messages;

public class RetrieveMessages extends Message {
    String conversationId;
    long dateTime;

    public RetrieveMessages(String conversationId, long dateTime) {
        this.type = Type.RetrieveMessages.getValue();
        this.conversationId = conversationId;
        this.dateTime = dateTime;
    }
}

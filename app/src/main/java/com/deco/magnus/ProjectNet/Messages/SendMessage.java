package com.deco.magnus.ProjectNet.Messages;

public class SendMessage extends Message {
    String email;
    String text;
    String conversationId;

    public SendMessage(String email, String text, String conversationId) {
        this.type = Type.SendMessage.getValue();
        this.email = email;
        this.text = text;
        this.conversationId = conversationId;
    }
}

package com.deco.magnus.ProjectNet.Messages;

public class SendFriendRequest extends Message {
    String fromEmail;
    String toEmail;

    public SendFriendRequest(String fromEmail, String toEmail) {
        this.type = Type.SendFriendRequest.getValue();
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
    }
}

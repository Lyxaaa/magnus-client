package com.deco.magnus.ProjectNet.Messages;

public class AcceptFriend extends Message {
    String fromEmail;
    String toEmail;

    public AcceptFriend(String fromEmail, String toEmail) {
        this.type = Type.AcceptFriend.getValue();
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
    }
}

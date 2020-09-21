package com.deco.magnus.ProjectNet.Messages;

public class CreateMatch extends Message {
    String email_1;
    String email_2;

    public CreateMatch(String email_1, String email_2) {
        this.type = Type.CreateMatch.getValue();
        this.email_1 = email_1;
        this.email_2 = email_2;
    }
}

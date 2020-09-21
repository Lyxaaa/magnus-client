package com.deco.magnus.ProjectNet.Messages;

public class GetMatchHistory extends Message {
    String email_1;

    public GetMatchHistory(String email_1) {
        this.type = Type.GetMatchHistory.getValue();
        this.email_1 = email_1;
    }
}

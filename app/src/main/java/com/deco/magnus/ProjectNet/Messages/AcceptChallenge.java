package com.deco.magnus.ProjectNet.Messages;

public class AcceptChallenge extends Message {
    String myEmail;
    String opponentEmail;
    Boolean accept;

    public AcceptChallenge(Boolean accept, String myEmail, String opponentEmail ) {
        this.type = Type.AcceptChallenge.getValue();
        this.myEmail = myEmail;
        this.opponentEmail = opponentEmail;
        this.accept = accept;
    }
}
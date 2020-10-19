package com.deco.magnus.ProjectNet.Messages;

public class EndMatch extends Message {
    public String matchId;
    public boolean youwon;

    public EndMatch () {
        type = Type.EndMatch.getValue();
        youwon = false;
    }
}

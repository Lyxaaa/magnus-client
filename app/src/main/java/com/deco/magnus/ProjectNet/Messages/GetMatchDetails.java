package com.deco.magnus.ProjectNet.Messages;

public class GetMatchDetails extends Message {
    String matchId;

    public GetMatchDetails(String matchId) {
        this.type = Type.GetMatchDetails.getValue();
        this.matchId = matchId;
    }
}

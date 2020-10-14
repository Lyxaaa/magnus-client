package com.deco.magnus.ProjectNet.Messages;

public class GetBoardState extends Message {
    String matchId;


    public GetBoardState(String matchId) {
        this.type = Type.GetBoardState.getValue();
        this.matchId = matchId;
    }
}
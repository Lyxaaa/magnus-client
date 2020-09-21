package com.deco.magnus.ProjectNet.Messages;

public class CreateMatchResult extends MessageResult {
    String matchId;
    String conversationId;

    public CreateMatchResult(String matchId, String conversationId) {
        this.type = Type.CreateMatchResult.getValue();
        this.matchId = matchId;
        this.conversationId = conversationId;
    }
}

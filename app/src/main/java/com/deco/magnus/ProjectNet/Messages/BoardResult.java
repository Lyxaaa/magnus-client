package com.deco.magnus.ProjectNet.Messages;

public class BoardResult extends MessageResult {
    String matchId;
    String board;


    public BoardResult(String matchId, String board) {
        this.type = Type.BoardResult.getValue();
        this.matchId = matchId;
        this.board = board;
    }
}
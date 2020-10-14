package com.deco.magnus.ProjectNet.Messages;

public class UpdateBoard extends Message {
    String matchId;
    String board;
    Boolean White;

    public UpdateBoard(String matchId, String board, Boolean white ) {
        this.type = Type.UpdateBoard.getValue();
        this.matchId = matchId;
        this.board = board;
        this.White = White;
    }
}
package com.deco.magnus.ProjectNet.Messages;

public class GetMatchHistoryResult extends MessageResult {
    String[] userId;
    String[] email;
    String[] matchId;
    String[] board;
    long[] startDateTime;
    boolean[] ended;

    public GetMatchHistoryResult(String[] userId, String[] email, String[] matchId,
                                 String[] board, long[] startDateTime, boolean[] ended) {
        this.type = Type.GetMatchHistoryResult.getValue();
        this.userId = userId;
        this.email = email;
        this.matchId = matchId;
        this.board = board;
        this.startDateTime = startDateTime;
        this.ended = ended;;
    }
}

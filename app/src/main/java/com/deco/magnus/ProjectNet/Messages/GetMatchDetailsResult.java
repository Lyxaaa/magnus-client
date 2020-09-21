package com.deco.magnus.ProjectNet.Messages;

public class GetMatchDetailsResult extends MessageResult {
    String matchId;
    String email_1;
    String email_2;
    String userId_1;
    String userId_2;
    String board;
    long dateTime;
    String ended;

    public GetMatchDetailsResult(String matchId, String email_1, String email_2, String userId_1,
                                 String userId_2, String board, long dateTime, String ended) {
        this.type = Type.GetMatchDetailsResult.getValue();
        this.matchId = matchId;
        this.email_1 = email_1;
        this.email_2 = email_2;
        this.userId_1 = userId_1;
        this.userId_2 = userId_2;
        this.board = board;
        this.dateTime = dateTime;
        this.ended = ended;
    }
}

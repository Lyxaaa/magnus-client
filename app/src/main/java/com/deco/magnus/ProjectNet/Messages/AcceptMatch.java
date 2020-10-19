package com.deco.magnus.ProjectNet.Messages;

public class AcceptMatch extends MessageResult {
    public String matchId;
    public String email;
    public String opponentemail;
    public boolean accept;

    public AcceptMatch(boolean accept, String email, String opponentemail, String matchId) {
        this.type = Type.AcceptMatch.getValue();
        this.accept = accept;
        this.email = email;
        this.opponentemail = opponentemail;
        this.matchId = matchId;
    }
}

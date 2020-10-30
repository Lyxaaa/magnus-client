package com.deco.magnus.Games.Chess;

import com.deco.magnus.ProjectNet.Messages.MatchFound;
import com.deco.magnus.ProjectNet.Messages.MatchStart;
import com.deco.magnus.UserData.User;

public class GameState {

    //region GetInstance
    private static GameState instance;
    final private static Object lock1 = new Object();
    final private static Object lock2 = new Object();

    public static GameState getInstance() {
        if (instance == null) {
            synchronized (lock1) {
                if (instance == null) {
                    synchronized (lock2) {
                        instance = new GameState();
                    }
                }
            }
        }
        return instance;
    }
    //endregion

    private MatchFound matchFound;
    private MatchStart matchStart;
    private String email;

    public boolean isReady() {
        return matchFound != null /*&& matchStart != null*/ && email != null;
    }

    public String getEmail () {
        if(email == null) return null;
        return email;
    }

    public String getOpponent () {
        if(matchFound == null) return null;
        return matchFound.opponentemail;
    }

    public String getMatchId() {
        return isReady() ? matchFound.matchId : null;
    }

    public boolean isWhite() {
        return isReady() ? matchStart.playeriswhite : null;
    }

    public void setMatchFound(MatchFound found) {
        matchFound = found;
    }

    public void setMatchStart(MatchStart start) {
        matchStart = start;
    }

    public void setUser(User user) {
        this.email = user.getEmail();
    }

    public void clear() {
        matchFound = null;
        matchStart = null;
    }
}

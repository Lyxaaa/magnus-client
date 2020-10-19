package com.deco.magnus.Games.Chess;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.Netbase.TCPSocket;
import com.deco.magnus.ProjectNet.Messages.Initialise;
import com.deco.magnus.ProjectNet.Messages.MatchFound;
import com.deco.magnus.ProjectNet.Messages.Message;
import com.deco.magnus.ProjectNet.Messages.Type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

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

    public void setMatchFound(MatchFound found) {

    }

    public void clear() {
        matchFound = null;
    }
}

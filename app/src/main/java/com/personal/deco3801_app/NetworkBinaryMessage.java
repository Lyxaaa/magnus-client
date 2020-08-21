package com.personal.deco3801_app;
import com.personal.deco3801_app.DataSocket;

public class NetworkBinaryMessage {
    public byte[] header;
    public DataSocket.MessageType type;
    public int length;
    public int version;
    public int sentAt;
    public long receivedAt;
    public String message;
}

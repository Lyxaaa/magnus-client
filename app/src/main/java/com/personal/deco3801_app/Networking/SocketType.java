package com.personal.deco3801_app.Networking;

public enum SocketType {
    TCP(0),
    UDP(1);

    private int socketType;

    SocketType(int socketType) {this.socketType = socketType;}

    public int getType() {
        return socketType;
    }
}

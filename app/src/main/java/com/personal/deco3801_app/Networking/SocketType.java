package com.personal.deco3801_app.Networking;

public enum SocketType {
    TCP(0),
    UDP(1);

    private int value;

    SocketType(int value) {this.value = value;}

    public int getValue() {
        return value;
    }
}

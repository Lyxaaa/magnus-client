package com.deco.magnus.Netbase;

public enum SocketType {
    TCP(0),
    UDP(1);

    private int value;

    SocketType(int value) {this.value = value;}

    public int getValue() {
        return value;
    }
}

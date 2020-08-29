package com.deco.magnus.ProjectNet.Messages;

public class Message extends com.deco.magnus.Networking.Json.Message {

    public Message( ) {
        setType(Type.Unknown);
    }

    public Message(Type type) {
        setType(type);
    }

    public Type getType() {
        return Type.fromInt(type);
    }

    public void setType(Type type) {
        this.type = type.getValue();
    }

    public byte[] GetBytes() {
        return super.getBytes();
    }
}


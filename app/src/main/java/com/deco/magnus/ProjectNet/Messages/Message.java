package com.deco.magnus.ProjectNet.Messages;

import com.deco.magnus.Netbase.JsonMsg;

public class Message extends JsonMsg {

    public Message() {
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


package com.personal.deco3801_app.ProjectNet.Messages;

import android.util.SparseArray;

import com.personal.deco3801_app.Networking.DataType;

public class Message extends com.personal.deco3801_app.Networking.Json.Message {

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


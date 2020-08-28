package com.personal.deco3801_app.ProjectNet.Messages;

import android.util.SparseArray;

public enum Type {
    Ack(0),
    Heartbeat(1),
    Initialise(2),
    Disconnect(3),
    Unknown(Integer.MAX_VALUE);

    private int value;

    private static final SparseArray<Type> intToTypeMap = new SparseArray<>();

    static {
        for (Type type : Type.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static Type fromInt(int i) {
        Type type = intToTypeMap.get(i);
        if (type == null)
            return Unknown;
        return type;
    }

    Type(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    }

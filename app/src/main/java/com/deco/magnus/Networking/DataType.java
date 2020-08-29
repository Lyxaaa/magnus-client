package com.deco.magnus.Networking;

import android.util.SparseArray;

public enum DataType {
    Error (-1),
    Undefined (0),
    Bytes (1),
    RawString (2),
    JSON (3);

    private int value;

    private static final SparseArray<DataType> intToTypeMap = new SparseArray<>();

    static {
        for (DataType type : DataType.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static DataType fromInt(int i) {
        DataType type = intToTypeMap.get(i);
        if (type == null)
            return Undefined;
        return type;
    }

    DataType(int value) {this.value = value;}

    public int getValue() {
        return value;
    }
}
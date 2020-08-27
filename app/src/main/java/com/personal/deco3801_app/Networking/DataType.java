package com.personal.deco3801_app.Networking;

public enum DataType {
    Error (-1),
    Undefined (0),
    Bytes (1),
    RawString (2),
    JSON (3);

    private int dataType;

    DataType(int dataType) {this.dataType = dataType;}

    public int getType() {
        return dataType;
    }
}
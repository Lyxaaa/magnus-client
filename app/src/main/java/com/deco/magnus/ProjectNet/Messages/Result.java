package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public enum Result {
    Success(0),
    Pending(1),
    Failure(2);

    private int value;

    private static final SparseArray<Result> intToTypeMap = new SparseArray<>();

    static {
        for (Result type : Result.values()) {
            intToTypeMap.put(type.value, type);
        }
    }

    public static Result fromInt(int i) {
       return intToTypeMap.get(i);
    }

    Result(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    }

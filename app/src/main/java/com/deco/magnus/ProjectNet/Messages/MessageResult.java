package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

public class MessageResult extends Message {
    public Result result;
    public String error;

    // do not send this back to the server or expect badness
// Result will be serialized into a string rather than int and the server may behave predictably unpredictable
    public enum Result{
        Invalid(-1), // error in code on the other side
        Success(0),   // operation was a success
        Pending(1),   // operation has not failed, stand by for result
        Failure(2);   // operation failed

        private int value;

        private static final SparseArray<Result> intToResultMap = new SparseArray<>();

        static {
            for (Result result : Result.values()) {
                intToResultMap.put(result.value, result);
            }
        }

        public static Result fromInt(int i) {
            Result result = intToResultMap.get(i);
            return result;
        }

        Result(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

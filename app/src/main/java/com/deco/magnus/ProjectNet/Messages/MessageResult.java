package com.deco.magnus.ProjectNet.Messages;

import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;

public class MessageResult extends Message {
    public Result result;
    public Type callingType;
    public String error;

    // do not send this back to the server or expect badness
// Result will be serialized into a string rather than int and the server may behave predictably unpredictable
    public enum Result{
       @SerializedName("-1") Invalid(-1), // error in code on the other side
       @SerializedName("0") Success(0),   // operation was a success
       @SerializedName("1") Pending(1),   // operation has not failed, stand by for result
       @SerializedName("2") Failure(2);   // operation failed

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

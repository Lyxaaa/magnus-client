package com.personal.deco3801_app.Networking.Json;

import com.google.gson.Gson;
import com.personal.deco3801_app.Networking.DataType;

import java.io.UnsupportedEncodingException;

public class Message {
    public int type;
    public long timestamp;
    public String message;

    public byte[] getBytes() {
        timestamp = System.currentTimeMillis();
        try {
            return new Gson().toJson(this).getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T TryCast(DataType dataType, Object data, int msgType, Class<T> type) {
        if (dataType == DataType.JSON) {
            Message cast = (Message)data;
            if (cast.type == msgType) {
                try {
                    return new Gson().fromJson(cast.message, type);
                } catch(Exception e) {
                    throw e;
                }
            }
        }
        return null;
    }
}

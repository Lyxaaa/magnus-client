package com.deco.magnus.Netbase;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

public class JsonMsg {
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
            JsonMsg cast = (JsonMsg) data;
            if (cast.type == msgType) {
                try {
                    T result = new Gson().fromJson(cast.message, type);
                    return result;
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return null;
    }

    public JsonMsg() {
    }

    public JsonMsg(int type) {
        this.type = type;
    }
}

package com.personal.deco3801_app.Networking;


import java.util.ArrayList;
import java.util.List;

public abstract class Socket {
    public static byte[] HEADER = {(byte) 0x5a, (byte) 0xfe, (byte) 0xc0, (byte) 0xde };
    public static int MAXPACKETSIZE = 65535;
    public static int INTSIZE = 4;

    private List<OnReceiveListener> OnReceiveListeners = new ArrayList<>();
    private List<OnDisconnectListener> OnDisconnectListeners = new ArrayList<>();

    public interface OnReceiveListener {
        public void onReceive(byte[] data);
    }

    public interface OnDisconnectListener {
        public void onDisconnect();
    }

    public void addOnReceiveListener(OnReceiveListener listener) {
        OnReceiveListeners.add(listener);
    }

    public void addOnDisconnectListener(OnDisconnectListener listener) {
        OnDisconnectListeners.add(listener);
    }

    public void removeOnReceiveListener(OnReceiveListener listener) {
        OnReceiveListeners.remove(listener);
    }

    public void removeOnDisconnectListener(OnDisconnectListener listener) {
        OnDisconnectListeners.remove(listener);
    }

    protected void invokeOnReceiveListeners(byte[] data){
        for(OnReceiveListener listener: OnReceiveListeners) {
            if(listener != null) listener.onReceive(data);
        }
    }

    protected void invokeOnDisconnectListeners(){
        for(OnDisconnectListener listener: OnDisconnectListeners) {
            if(listener != null) listener.onDisconnect();
        }
    }

    public abstract void begin();
    public abstract void end();

    public abstract void send(byte[]... data);
}

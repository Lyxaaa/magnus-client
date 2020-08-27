package com.personal.deco3801_app.Networking;


import java.util.ArrayList;
import java.util.List;

public abstract class Socket {
    public static byte[] HEADER = {(byte) 0x5a, (byte) 0xfe, (byte) 0xc0, (byte) 0xde };
    public static int MAXPACKETSIZE = 65535;
    public static int INTSIZE = 4;

    public List<OnReceiveListener> OnReceiveListeners = new ArrayList<>();
    public List<OnDisconnectListener> OnDisconnectListeners = new ArrayList<>();

    public interface OnReceiveListener {
        public void onReceive(byte[] data);
    }

    public interface OnDisconnectListener {
        public void onDisconnect();
    }

    protected void InvokeOnReceiveListeners(byte[] data){
        for(OnReceiveListener listener: OnReceiveListeners) {
            if(listener != null) listener.onReceive(data);
        }
    }

    protected void InvokeOnDisconnectListeners(){
        for(OnDisconnectListener listener: OnDisconnectListeners) {
            if(listener != null) listener.onDisconnect();
        }
    }

    public abstract void Begin();
    public abstract void End();

    public abstract void Send(byte[]... data);
}

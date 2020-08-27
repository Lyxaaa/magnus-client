package com.personal.deco3801_app.Networking;


import java.util.ArrayList;
import java.util.List;

public abstract class Socket {
    public static byte[] HEADER = {(byte) 0x5a, (byte) 0xfe, (byte) 0xc0, (byte) 0xde };
    public List<OnReceiveListener> OnReceiveListeners = new ArrayList<>();
    public List<OnDisconnectListener> OnDisconnectListeners = new ArrayList<>();

    public interface OnReceiveListener {
        public void onReceive(byte[] data);
    }

    public interface OnDisconnectListener {
        public void onDisconnect(byte[] data);
    }

    public abstract void Begin();
    public abstract void End();

    public abstract void Send(byte[]... data);
}

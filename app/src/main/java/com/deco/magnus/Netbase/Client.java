package com.deco.magnus.Netbase;

import android.util.Log;

import com.google.gson.Gson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Client {
    public static int VERSION = 0;

    public static String TAG = "Client";

    private List<OnReceiveListener> OnReceiveListeners = new ArrayList<>();
    private List<OnDisconnectListener> OnDisconnectListeners = new ArrayList<>();

    public interface OnReceiveListener {
        boolean OnReceive(SocketType socketType, DataType dataType, Object data);
    }

    public interface OnTimeoutListener {
        void OnTimeout();
    }

    public interface OnDisconnectListener {
        void OnDisconnect();
    }

    protected UDPSocket broadcast;
    protected UDPSocket udp;
    protected TCPSocket tcp;

    protected void OnTCP(byte[] data) {
        receive(SocketType.TCP, data);
    }

    protected void OnUDP(byte[] data) {
        receive(SocketType.UDP, data);
    }

    public void end() {
        if (udp != null) {
            udp.end();
        }
        if (tcp != null) {
            tcp.end();
        }
    }

    Object sendlock = new Object();

    public void send(SocketType socketType, DataType dataType, byte[] data) {
        synchronized (sendlock) {
            switch (socketType) {
                case TCP:
                    tcp.send(
                            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(VERSION).array(),
                            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataType.getValue()).array(),
                            data
                    );
                    break;
                case UDP:
                    udp.send(
                            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(VERSION).array(),
                            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataType.getValue()).array(),
                            data
                    );
                    break;
            }
        }
    }

    Gson gson = new Gson();

    protected void receive(SocketType socketType, byte[] data) {
        // parse version
        int val = ByteBuffer.wrap(data, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        switch (val) {
            case 0:
                val = ByteBuffer.wrap(data, 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                DataType dataType = DataType.fromInt(val);

                byte[] dataSegment = new byte[data.length - 8];
                System.arraycopy(data, 8, dataSegment, 0, dataSegment.length);

                Object processedData = null;
                switch (dataType) {
                    case Bytes:
                        processedData = dataSegment;
                        break;
                    case RawString:
                        processedData = new String(dataSegment);
                        break;
                    case JSON:
                        String str = new String(dataSegment);
                        try {
                            processedData = gson.fromJson(str, JsonMsg.class);
                            ((JsonMsg) processedData).message = str;
                        } catch (Exception e) {
                            dataType = DataType.Error;
                        }
                        break;
                    default:
                        Log.e(TAG, "Protocol " + dataType + " unsupported");
                }
                invokeOnReceiveListeners(socketType, dataType, processedData);
                break;
            default:
                Log.e(TAG, "Version " + val + " unsupported");
        }
    }

    public void addOnReceiveListener(OnReceiveListener listener) {
        OnReceiveListeners.add(listener);
    }

    public void addOnDisconnectListener(OnDisconnectListener listener) {
        OnDisconnectListeners.add(listener);
    }

    public void addOnReceiveListener(OnReceiveListener onReceive, long timeoutMillis, OnTimeoutListener onTimeout) {
        addOnReceiveListener(onReceive);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (removeOnReceiveListener(onReceive)) {
                    if (onTimeout != null)
                        onTimeout.OnTimeout();
                }
            }
        }, timeoutMillis);
    }

    public boolean removeOnReceiveListener(OnReceiveListener listener) {
        return OnReceiveListeners.remove(listener);
    }

    public void removeOnDisconnectListener(OnDisconnectListener listener) {
        OnDisconnectListeners.remove(listener);
    }

    public void invokeOnReceiveListeners(SocketType socketType, DataType dataType, Object data) {
        Iterator<OnReceiveListener> itr = OnReceiveListeners.iterator();
        while (itr.hasNext()) {
            OnReceiveListener listener = itr.next();
            if (listener.OnReceive(socketType, dataType, data)) {
                OnReceiveListeners.remove(listener);
            }
        }
    }

    public void invokeOnDisconnectListeners(SocketType socketType, DataType dataType, Object data) {
        for (OnDisconnectListener listener : OnDisconnectListeners) {
            listener.OnDisconnect();
        }
    }
}

package com.deco.magnus.Networking;

import android.util.Log;

import com.google.gson.Gson;
import com.deco.magnus.Networking.Json.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public abstract class Client {
    public static int VERSION = 0;

    public static String TAG = "Client";

    public List<OnReceiveListener> OnReceiveListeners = new ArrayList<>();

    public interface OnReceiveListener {
        void OnReceive(SocketType socketType, DataType dataType, Object data);
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

    ByteBuffer sendByteBuffer = ByteBuffer.allocate(4);
    Object sendLock1 = new Object();
    Object sendLock2 = new Object();
    boolean sending = false;

    public void send(SocketType socketType, DataType dataType, byte[] data) {
        if (!sending) {
            synchronized (sendLock1) {
                if (!sending) {
                    synchronized (sendLock2) {
                        sending = true;
                        switch (socketType) {
                            case TCP:
                                tcp.send(
                                        sendByteBuffer.putInt(VERSION).order(ByteOrder.BIG_ENDIAN).array(),
                                        sendByteBuffer.putInt(dataType.getValue()).order(ByteOrder.BIG_ENDIAN).array(),
                                        data
                                );
                                break;
                            case UDP:
                                udp.send(
                                        sendByteBuffer.putInt(VERSION).order(ByteOrder.BIG_ENDIAN).array(),
                                        sendByteBuffer.putInt(dataType.getValue()).order(ByteOrder.BIG_ENDIAN).array(),
                                        data
                                );
                                break;
                        }
                        sending = false;
                    }
                }
            }
        }
    }

    Gson gson = new Gson();
    protected void receive(SocketType socketType, byte[] data) {
        // parse version
        int val = ByteBuffer.wrap(data, 0, 4).getInt();
        switch (val) {
            case 0:
                val = ByteBuffer.wrap(data, 4, 4).getInt();
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
                            processedData = gson.fromJson(str, Message.class);
                            ((Message) processedData).message = str;
                        } catch (Exception e) {
                            dataType = DataType.Error;
                        }
                        break;
                    default:
                        Log.e(TAG, "Protocol " + dataType + " unsupported");
                }
                invokeOnReceiveListeners(socketType, dataType, data);
                break;
            default:
                Log.e(TAG, "Version " + val + " unsupported");
        }
    }

    public void invokeOnReceiveListeners(SocketType socketType, DataType dataType, Object data) {
        for(OnReceiveListener listener: OnReceiveListeners) {
            listener.OnReceive(socketType,  dataType,  data);
        }
    }
}

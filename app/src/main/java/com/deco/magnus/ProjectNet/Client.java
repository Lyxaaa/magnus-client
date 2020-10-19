package com.deco.magnus.ProjectNet;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.JsonMsg;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.Netbase.TCPSocket;
import com.deco.magnus.ProjectNet.Messages.Message;
import com.deco.magnus.ProjectNet.Messages.Initialise;
import com.deco.magnus.ProjectNet.Messages.Type;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends com.deco.magnus.Netbase.Client {

    //region GetInstance
    private static Client instance;
    final private static Object lock1 = new Object();
    final private static Object lock2 = new Object();

    public static Client getInstance() {
        if (instance == null) {
            synchronized (lock1) {
                if (instance == null) {
                    synchronized (lock2) {
                        instance = new Client();
                    }
                }
            }
        }
        return instance;
    }
    //endregion

    public String id;

    public Client() {
        id = java.util.UUID.randomUUID().toString();
    }

    public Client(String id) {
        this.id = id;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        send(new Message(Type.Disconnect));
        end();
    }

    LinkedBlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();
    Thread sendThread;

    private class MessageObject {
        Object data;
        SocketType socketType;
        DataType dataType;

        MessageObject(Object data, SocketType socketType, DataType dataType) {
            this.data = data;
            this.socketType = socketType;
            this.dataType = dataType;
        }
    }

    public void threadSafeSend(int identifier, byte[] data) {
        byte[] packet = new byte[data.length + 4];
        int pos = 0;

        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(identifier).array(),
                0, packet, pos, 4);
        pos += 4;
        System.arraycopy(data,0, packet, pos, data.length);
        threadSafeSend(packet, SocketType.TCP, DataType.Bytes);
    }

    public void threadSafeSend(Object data) {
        threadSafeSend(data, SocketType.TCP, DataType.JSON);
    }

    private final Object sendLock = new Object();
    private boolean sending = false;

    public void threadSafeSend(Object data, SocketType socketType, DataType dataType) {
        synchronized (sendLock) {
            messageQueue.add(new MessageObject(data, socketType, dataType));
            startSendThread();
        }
    }

    private final Object sendThreadLock1 = new Object();
    private final Object sendThreadLock2 = new Object();

    private void startSendThread() {
        if (sendThread == null) {
            synchronized (sendThreadLock1) {
                if (sendThread == null) {
                    synchronized (sendThreadLock2) {
                        sendThread = new Thread(() -> {
                            sending = true;
                            while (sending) {
                                try {
                                    while(messageQueue.size() > 0) {
                                        MessageObject obj = messageQueue.take();
                                        send(obj.data, obj.socketType, obj.dataType);
                                    }
                                } catch (Exception e) {
                                    sending = false;
                                }
                            }
                            sending = false;
                        });
                        sendThread.start();
                    }
                }
            }
        }
    }

    public void send(Object data) {
        send(data, SocketType.TCP, DataType.JSON);
    }

    public void send(Object data, SocketType socketType, DataType dataType) {
        byte[] datata;

        switch (dataType) {
            case Bytes:
                datata = (byte[]) data;
                break;
            case RawString:
                datata = ((String) data).getBytes(StandardCharsets.US_ASCII);
                break;
            case JSON:
                datata = ((Message) data).getBytes();
                break;
            default:
                datata = new byte[0];
                break;
        }
        super.send(socketType, dataType, datata);
    }

    Timer heartBeat;
    public boolean connect(String address, int port) {
        tcp = new TCPSocket(address, port);
        tcp.addOnReceiveListener(this::OnTCP);
        tcp.addOnDisconnectListener(this::invokeOnDisconnectListeners);
        tcp.begin();
        send(new Initialise(id));

        heartBeat = new Timer();
        heartBeat.schedule(new TimerTask() {
            @Override
            public void run() {
                send(new Message(Type.Heartbeat));
            }
        }, 1000, 1000);

        return tcp.isConnected;
    }
}

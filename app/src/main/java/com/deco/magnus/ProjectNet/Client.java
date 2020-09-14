package com.deco.magnus.ProjectNet;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.Netbase.TCPSocket;
import com.deco.magnus.ProjectNet.Messages.Message;
import com.deco.magnus.ProjectNet.Messages.MsgInitialise;
import com.deco.magnus.ProjectNet.Messages.Type;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    BlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<>();
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
                                        MessageObject obj = messageQueue.poll(1000, TimeUnit.MILLISECONDS);
                                        if(obj != null) send(obj.data, obj.socketType, obj.dataType);
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

    public boolean connect(String address, int port) {
        tcp = new TCPSocket(address, port);
        tcp.addOnReceiveListener(this::OnTCP);
        tcp.begin();
        send(new MsgInitialise(id));
        return tcp.isConnected;
    }
}

package com.deco.magnus.ProjectNet;

import com.deco.magnus.Netbase.DataType;
import com.deco.magnus.Netbase.Socket;
import com.deco.magnus.Netbase.SocketType;
import com.deco.magnus.Netbase.TCPSocket;
import com.deco.magnus.ProjectNet.Messages.Message;
import com.deco.magnus.ProjectNet.Messages.MsgInitialise;
import com.deco.magnus.ProjectNet.Messages.Type;

import java.nio.charset.StandardCharsets;

public class Client extends com.deco.magnus.Netbase.Client {

    //region GetInstance
    private static Client instance;
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();

    public static Client getInstance() {
        if(instance == null) {
            synchronized (lock1) {
                if(instance == null) {
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

    public void connect(String address, int port) {
        tcp = new TCPSocket(address, port);
        tcp.addOnReceiveListener(this::OnTCP);
        tcp.begin();


        send(new MsgInitialise(id));
    }
}

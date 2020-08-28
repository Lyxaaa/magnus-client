package com.personal.deco3801_app.ProjectNet;

import com.personal.deco3801_app.Networking.DataType;
import com.personal.deco3801_app.Networking.Socket;
import com.personal.deco3801_app.Networking.SocketType;
import com.personal.deco3801_app.Networking.TCPSocket;
import com.personal.deco3801_app.ProjectNet.Messages.Message;
import com.personal.deco3801_app.ProjectNet.Messages.MsgInitialise;
import com.personal.deco3801_app.ProjectNet.Messages.Type;

import java.nio.charset.StandardCharsets;

public class Client extends com.personal.deco3801_app.Networking.Client {
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
        tcp.addOnReceiveListener(new Socket.OnReceiveListener() {
            @Override
            public void onReceive(byte[] data) {
                OnTCP(data);
            }
        });
        tcp.begin();

        send(new MsgInitialise(id));
    }
}

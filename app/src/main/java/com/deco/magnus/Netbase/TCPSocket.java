package com.deco.magnus.Netbase;

import com.deco.magnus.Types.LimitedQueue;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class TCPSocket extends Socket {

    java.net.Socket client;
    boolean running = false;
    public InetSocketAddress remoteEndPoint;

    public TCPSocket(String address, int port) {
        client = new java.net.Socket();
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        try {
            client.connect(socketAddress);
            isConnected = true;
        } catch (IOException io) {
            io.printStackTrace();
        }
        remoteEndPoint = new InetSocketAddress(address, port);
    }

    public TCPSocket(InetSocketAddress endpoint) {
        client = new java.net.Socket();
        try {
            client.connect(endpoint);
        } catch (IOException io) {
            io.printStackTrace();
        }
        remoteEndPoint = endpoint;
    }

    public TCPSocket(java.net.Socket client) {
        this.client = client;
        remoteEndPoint = (InetSocketAddress) client.getRemoteSocketAddress();
    }

    @Override
    public void begin() {
        running = true;
        new Thread(() -> {
            try {
                TCPRead();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void end() {
        running = false;
    }

    // packet structure
    // { header(4), size(4), data(size) }
    void TCPRead() throws Exception {
        Byte[] headerBytes = new Byte[4];
        for (int i = 0; i < 4; i++) {
            headerBytes[i] = HEADER[i];
        }
        InputStream stream = null;
        try {
            stream = client.getInputStream();
        } catch (IOException io) {
            io.printStackTrace();
            invokeOnDisconnectListeners();
        }
        if (stream == null) return;

        LimitedQueue<Byte> headerBuffer = new LimitedQueue<>(4);
        byte[] buffer = new byte[4];
        int size;

        // while running
        // read 4 bytes for header
        // if that's not the header
        // check byte by byte until we find the header
        // once header

        // clear buffer

        while (running) {
            if (stream.read(buffer, 0, 4) != 4) {
                // probably throw an error
            }
            for (byte data : buffer) {
                headerBuffer.add(data);
            }
            if (!Arrays.equals(buffer, HEADER)) {
                do {
                    int read = stream.read();
                    headerBuffer.add((byte) read);
                } while (!Arrays.equals(headerBuffer.toArray(), headerBytes));
            }

            if (Arrays.equals(headerBuffer.toArray(), headerBytes)) {
                if (stream.read(buffer, 0, 4) == 4) {
                    size = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    if (size > Short.MAX_VALUE) {
                        throw new Exception("Size of buffer too large");
                    } else {
                        byte[] bytes = new byte[size];
                        stream.read(bytes, 0, size);
                        invokeOnReceiveListeners(bytes);
                    }
                } else {
                    throw new Exception("No data found in stream");
                }
                headerBuffer.clear();
            }
        }
    }

    @Override
    public void send(byte[]... data) {

        int dataLength = 0;
        for (byte[] byteData : data)
            dataLength += byteData.length;

        int size = HEADER.length; // header size
        size += 4; //sizeof(Int32);      // size... size
        size += dataLength;         // data size

        byte[] packet = new byte[size];
        int pos = 0;

        System.arraycopy(HEADER, 0, packet, pos, HEADER.length);
        pos += HEADER.length;

        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(dataLength).array(), 0, packet, pos, HEADER.length);
        pos += 4; //sizeof(Int32);

        for (byte[] byteData : data) {
            System.arraycopy(byteData, 0, packet, pos, byteData.length);
            pos += byteData.length;
        }
        try {
            client.getOutputStream().write(packet, 0, packet.length);
            client.getOutputStream().flush();
        } catch (IOException io) {
            io.printStackTrace();
            invokeOnDisconnectListeners();
        }
    }
}

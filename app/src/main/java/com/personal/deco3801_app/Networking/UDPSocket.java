package com.personal.deco3801_app.Networking;

import com.google.common.collect.EvictingQueue;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class UDPSocket extends Socket {

    DatagramSocket client;
    boolean running = false;

    public InetSocketAddress remoteEndPoint;

    public UDPSocket(String address, int port) {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        SocketAddress socketAddress = InetSocketAddress.createUnresolved(address, port);
        try {
            client.connect(socketAddress);
        } catch (IOException io) {
            io.printStackTrace();
        }
        remoteEndPoint = new InetSocketAddress(address, port);
    }

    public UDPSocket(InetSocketAddress endpoint) {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            client.connect(endpoint);
        } catch (IOException io) {
            io.printStackTrace();
        }
        remoteEndPoint = endpoint;
    }

    public UDPSocket(DatagramSocket client) {
        this.client = client;
        remoteEndPoint = (InetSocketAddress) client.getRemoteSocketAddress();
    }

    @Override
    public void Begin() {
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //UDPRead();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void End() {
        running = false;
    }
/**
    void UDPRead() throws Exception {
        Byte[] headerBytes = new Byte[4];
        for (int i = 0; i < 4; i++) {
            headerBytes[i] = HEADER[i];
        }

        byte[] packetBuffer = new byte[8 + Short.MAX_VALUE];
        DatagramPacket datagramPacket = new DatagramPacket(packetBuffer, packetBuffer.length);
        byte[] sizeBuffer = new byte[4];
        byte[] hashBuffer = new byte[4];
        byte[] data = null;

        int hash = 0;

        int msgPos = 0;
        int msgRem = 0;

        int bufPos = 0;
        int bufMax = 0;

        EvictingQueue<Byte> headerBuffer = new EvictingQueue<>(4);

        while (running) {
            packetBuffer = getBuffer(client, datagramPacket);
            UdpReceiveResult result = await client.ReceiveAsync();
            bufMax = result.Buffer.Length;
            readSize = datagramPacket.getLength();
            while (bufPos < bufMax) {
                if (!Arrays.equals(headerBuffer.toArray(), headerBytes)) {
                    headerBuffer.Enqueue(result.Buffer[bufPos]);
                    bufPos++;
                } else if (data == null) {
                    if (msgPos < 4) {
                        sizeBuffer[msgPos] = result.Buffer[bufPos];
                        msgPos++;
                        bufPos++;
                    } else {
                        Int32 size = BitConverter.ToInt32(sizeBuffer, 0);
                        if (size < 0 || size > ushort.MaxValue) {
                            // throw some error, this is obviously wrong
                        } else {
                            data = new byte[size];
                        }
                        Array.Clear(sizeBuffer, 0, sizeBuffer.Length);
                        msgPos = 0;
                    }
                } else if (hash == null) {
                    if (msgPos < 4) {
                        hashBuffer[msgPos] = result.Buffer[bufPos];
                        msgPos++;
                        bufPos++;
                    } else {
                        hash = BitConverter.ToUInt32(hashBuffer, 0);
                        Array.Clear(hashBuffer, 0, hashBuffer.Length);
                        msgPos = 0;
                    }
                } else {
                    if (bufMax <= bufPos + data.Length) {
                        Array.Copy(result.Buffer, bufPos, data, msgPos, bufMax - bufPos);
                        msgRem = data.Length - (bufMax - bufPos);
                        msgPos += bufMax - bufPos;
                        bufPos = bufMax;
                    } else {
                        Array.Copy(result.Buffer, bufPos, data, msgPos, msgRem);
                        bufPos += msgRem;

                        if (Crc32.Compute(data) == hash) {
                            OnReceiveListener.Invoke(data);
                        } else {
                            //probably throw an error
                        }

                        msgPos = -1;
                        msgRem = -1;
                        headerBuffer.Clear();
                        hash = null;
                    }
                }
            }
        }
    }
 */

    private byte[] getBuffer(DatagramSocket socket, DatagramPacket packet) {
        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return packet.getData();
    }

    @Override
    public void Send(byte[]... data) {

    }
}

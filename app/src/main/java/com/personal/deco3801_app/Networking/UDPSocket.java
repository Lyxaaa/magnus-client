package com.personal.deco3801_app.Networking;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

public class UDPSocket extends Socket {

    DatagramSocket client;
    boolean running = false;
    CRC32 readCRC = new CRC32();
    CRC32 sendCRC = new CRC32();

    public InetSocketAddress remoteEndPoint;
    private boolean useCRC;

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
    public void begin() {
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UDPRead();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void end() {
        running = false;
    }

    void UDPRead() throws Exception {
        Byte[] headerBytes = new Byte[4];
        for (int i = 0; i < 4; i++) {
            headerBytes[i] = HEADER[i];
        }

        byte[] resultBuffer = new byte[8 + MAXPACKETSIZE];
        DatagramPacket datagramPacket = new DatagramPacket(resultBuffer, resultBuffer.length);

        byte[] sizeBuffer = new byte[4];
        byte[] hashBuffer = new byte[4];

        byte[] data = null;

        Integer hash = 0;

        int msgPos = 0; // position in the data we're trying to form
        int msgRem = 0; // data remaining (when we hit 0, our data is complete)

        int bufPos = 0; // how much data remains in the packet we just received
        int bufMax = 0; // how much data there is total in the packet that was just received

        LimitedQueue<Byte> headerBuffer = new LimitedQueue<>(4);

        while (running) {
            resultBuffer = getBuffer(client, datagramPacket);
            bufMax = datagramPacket.getLength();

            while (bufPos < bufMax) { // while there is still data to read
                if (!Arrays.equals(headerBuffer.toArray(), headerBytes)) { // if we haven't found the header yet
                    headerBuffer.add(resultBuffer[bufPos]);
                    bufPos++;
                } else if (data == null) { // we need to find the size
                    if (msgPos < 4) { // if we haven't found the size yet
                        sizeBuffer[msgPos] = resultBuffer[bufPos];
                        msgPos++;
                        bufPos++;
                    } else { // if we've finished finding the bytes that form the size
                        int size = ByteBuffer.wrap(sizeBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt(); // convert into an int
                        if (size < 0 || size > MAXPACKETSIZE) { // sanity check
                            // throw some error, this is obviously wrong
                        } else {
                            data = new byte[size]; // create the byte[] that will hold the data
                        }
                        Arrays.fill(sizeBuffer, (byte) 0); // wipe the buffer, we need this for next round
                        msgPos = 0; // reset message pos
                    }
                } else if (hash == null) { // we need to find the hash
                    if (msgPos < 4) { // haven't found the hash yet
                        hashBuffer[msgPos] = resultBuffer[bufPos];
                        msgPos++;
                        bufPos++;
                    } else { // we've found the bytes used to make the hash
                        hash = ByteBuffer.wrap(hashBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt(); // convert into an int
                        Arrays.fill(hashBuffer, (byte) 0); // wipe the buffer, we need this for next round
                        msgPos = 0;
                    }
                } else { // we have all 3 parts of the UDP header
                    if (bufMax <= bufPos + data.length) { // if we can't form the whole of byte[] data using this packet's worth of data, copy all we can
                        System.arraycopy(resultBuffer, bufPos, data, msgPos, bufMax - bufPos);
                        msgRem = data.length - (bufMax - bufPos);
                        msgPos += bufMax - bufPos;
                        bufPos = bufMax;
                    } else { // we can complete the data, we copy what we have left
                        System.arraycopy(resultBuffer, bufPos, data, msgPos, msgRem);
                        bufPos += msgRem;

                        readCRC.reset();
                        readCRC.update(data);
                        if (useCRC && readCRC.getValue() == hash) {
                            invokeOnReceiveListeners(data);
                        } else if (!useCRC) {
                            invokeOnReceiveListeners(data);
                        }

                        msgPos = 0;
                        msgRem = 0;
                        headerBuffer.clear();
                        hash = null;
                    }
                }
            }
        }
    }

    private byte[] getBuffer(DatagramSocket socket, DatagramPacket packet) {
        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return packet.getData();
    }

    ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    // sizeof(header) + sizeof(data size) + sizeof(crc)
    int headerOffset = HEADER.length + INTSIZE + INTSIZE;

    @Override
    public void send(byte[]... data) {
        // find size
        int dataSize = 0;
        for (byte[] segment : data)
            dataSize += segment.length;

        // total size
        int size = headerOffset;
        size += dataSize; // data size

        // create new message byte array
        byte[] message = new byte[size];

        int offset = 0;
        for (byte[] segment : data) { // copy all the data arrays into the message to be sent
            System.arraycopy(segment, offset, message, 0, data.length);
            offset += data.length;
        }

        int crc;
        if (useCRC) { // calculate crc
            sendCRC.update(message, headerOffset, dataSize);
            crc = sendCRC.hashCode();
            sendCRC.reset();
        } else { // crc is zero
            crc = 0;
        }

        offset = 0;
        System.arraycopy(HEADER, 0, message, offset, HEADER.length);
        offset += HEADER.length;
        System.arraycopy(byteBuffer.putInt(dataSize).order(ByteOrder.BIG_ENDIAN).array(), 0, message, offset, INTSIZE);
        offset += INTSIZE;
        System.arraycopy(byteBuffer.putInt(crc).order(ByteOrder.BIG_ENDIAN).array(), 0, message, offset, INTSIZE);

        DatagramPacket packet = new DatagramPacket(message, 0, message.length, remoteEndPoint);
        try {
            client.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUseCRC() {
        return useCRC;
    }

    public void setUseCRC(boolean useCRC) {
        this.useCRC = useCRC;
    }
}

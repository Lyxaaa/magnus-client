package com.personal.deco3801_app;


import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.personal.deco3801_app.NetworkBinaryMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by CYao on 7/02/2018.
 */

public class DataSocket {
    public static int DEFAULT_PORT = 42424;
    public static int TIMEOUT = 1500;
    public static int ADDITIONAL_SENDS = 5;
    public String TAG = "DataSocket";

    private static DataSocket sDataSocket;

    private InetSocketAddress mServer;
    private ConcurrentLinkedQueue<byte[]> mOutGoingMessages = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<NetworkBinaryMessage> mInComingMessages = new ConcurrentLinkedQueue<>();
    private ByteBuffer mTimeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
    private static long mStartTime = System.currentTimeMillis();
    private static Gson gson = new Gson();
    private Socket mSocket;
    private boolean mIsSending = false;
    private boolean mIsDestructing = false;
    private boolean mIsHeartBeating = false;
    private long mLastMessageTime = 0;
    private HeartBeat mHeartBeat = null;
    private ReentrantLock mLock = new ReentrantLock();
    private Read mRead;
    private int mCurrentPing;
    private Status mConnectionStatus = Status.Unconnected;
    private Object mConnectionLock = new Object();

    private DisconnectionListener mDisconnectionListener;
    private ArrayList<IncomingMessageListener> mIncomingMessageListeners = new ArrayList<>();

    //gets the instance of DataSocket
    public static synchronized DataSocket getInstance() {
        if (sDataSocket == null) {
            sDataSocket = new DataSocket();
        }
        return sDataSocket;
    }

    private DataSocket() {
    }

    //used to create messages to send
    public static byte[] createDataPacket(MessageType type, Object payload) {
        String string;

        if (payload == null) string = "";
        else if (payload instanceof String) string = (String) payload;
        else string = gson.toJson(payload);

        byte[] header = new byte[16 + string.length()];
        header[0] = (byte) 0xAF; //header
        header[1] = (byte) 0xEE; //header
        header[2] = (byte) 0x00; //version
        header[3] = (byte) type.getValue(); //type
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN); //size
        System.arraycopy(sizeBuffer.putInt(string.length()).array(), 0, header, 4, 4);
        System.arraycopy(string.getBytes(), 0, header, 16, string.length());
        return header;
    }

    //use this to check if server exists and to connect to server
    public void connect(final InetSocketAddress server, final ConnectionListener listener) {
        mSocket = new Socket();
        setStatus(Status.Unconnected);
        //System.out.println("created new socket");
        new Thread() {
            @Override
            public void run() {
                try {
                    mSocket.connect(server, TIMEOUT);
                } catch (IOException e) {
                    setStatus(Status.Failure);
                    listener.onConnection(Status.Failure);
                    return;
                }
                setStatus(Status.Connected);
                listener.onConnection(Status.Connected);
                mServer = server;
            }
        }.start();
    }

    public void disconnect() {
        mIsHeartBeating = false;
        mHeartBeat = null;
        try {
            if (mSocket != null)
                mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sDataSocket = null;
        setStatus(Status.Unconnected);
    }

    //registers the device, allowing us to start streaming
    public void startConnection() {
        mIsHeartBeating = false;
        mHeartBeat = new HeartBeat();
        mHeartBeat.start();

        mRead = new Read();
        mRead.start();
    }

    public void sleepConnection() {

    }

    public void wakeConnection() {

    }

    public void stopConnection(DisconnectionListener listener) {
        send(createDataPacket(MessageType.Disconnect, null));
        //TODO: wait for reply and terminate stream gracefully
    }

    //adds a message to the queue to send
    public synchronized void send(byte[] message) {
        if (message == null)
            return;

        mLock.lock();
        mOutGoingMessages.add(message);
        mLock.unlock();

        if (mSocket == null) {
            return;
        }

        if (mSocket.isConnected()) {
            if (!mIsSending) {
                mIsSending = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendAvailableMessages();
                        mIsSending = false;
                    }
                }).start();
            }
        }
    }

    //sends all available messages in the queue
    private void sendAvailableMessages() {
        int loops = 0;
        while ((!mIsDestructing && (!mOutGoingMessages.isEmpty()) || loops++ < ADDITIONAL_SENDS)) {
            if (!mOutGoingMessages.isEmpty()) {
                loops = 0;
                sendNextMessage();
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // we get here when the socket is closed
                    Log.d(TAG, "Socket Closed");
                }
            }
        }

        mLock.lock();
        while (!mIsDestructing && !mOutGoingMessages.isEmpty()) {
            sendNextMessage();
        }
        mLock.unlock();
    }

    //sends the next message in the queue
    private void sendNextMessage() {
        byte[] b = mOutGoingMessages.poll();
        if (b != null) {
            try {
                mTimeBuffer.rewind();
                mTimeBuffer.putInt(((int) (System.currentTimeMillis() - mStartTime)));
                System.arraycopy(mTimeBuffer.array(), 0, b, 8, 4);
                /*String s = "msg: ";
                StringBuilder sb = new StringBuilder(s);
                for (int i = 0; i < b.length; i++) {
                    sb.append(String.format("%02X", b[i]));
                }
                sb.append("%n");
                System.out.println(sb.toString());*/
                mSocket.getOutputStream().write(b, 0, b.length);
                mSocket.getOutputStream().flush();
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("invoking disconnect");
                mIsDestructing = true;
                invokeDisconnectionEvent();
                disconnect();
                //we should reset at this point
            }
        }
    }

    //<editor-fold desc="Listeners and Shit">
    public void setOnDisconnectionListener(DisconnectionListener listener) {
        mDisconnectionListener = listener;
    }

    private void invokeIncomingMessageEvent(NetworkBinaryMessage message) {
        if (mIncomingMessageListeners.isEmpty()) return;
        for (IncomingMessageListener listener : mIncomingMessageListeners) {
            if (listener != null) listener.onMessage(message);
        }
    }

    public void addOnIncomingMessageListener(IncomingMessageListener listener) {
        mIncomingMessageListeners.add(listener);
    }

    public void removeOnIncomingMessageListener(IncomingMessageListener listener) {
        mIncomingMessageListeners.remove(listener);
    }

    //invokes disconnection event
    private void invokeDisconnectionEvent() {
        if (mDisconnectionListener != null)
            mDisconnectionListener.onDisconnection();
    }

    public synchronized int getPing() {
        return mCurrentPing;
    }

    public interface DisconnectionListener {
        void onDisconnection();
    }

    public interface ConnectionListener {
        void onConnection(Status status);
    }

    public interface CalibrationListener {
        void onCalibration();
    }

    public interface IncomingMessageListener {
        void onMessage(NetworkBinaryMessage message);
    }
    //</editor-fold>

    //sends a heartbeat every second, use this to compute ping and to check if socket has closed
    private class HeartBeat extends Thread {
        @Override
        public void run() {
            mIsHeartBeating = true;
            while (mSocket != null && mSocket.isConnected() && mIsHeartBeating) {
                try {
                    Thread.sleep(1000);
                    send(createDataPacket(MessageType.Heartbeat, null));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mIsHeartBeating = false;
        }
    }

    //reads messages and enqueues them in the incoming messages list
    private class Read extends Thread {
        @Override
        public void run() {
            //<editor-fold desc="Setup and PreFlight Checks">
            byte[] headerBytes = {(byte) 0xaf, (byte) 0xee};
            byte[] header = new byte[headerBytes.length];
            byte[] version = new byte[1];
            byte[] type = new byte[1];
            byte[] sentAt = new byte[4];
            byte[] length = new byte[4];

            int headerBytesFound = 0;
            InputStream stream = null;
            if (mSocket == null) return;

            try {
                stream = mSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (stream == null) return;
            //</editor-fold>
            while (mSocket != null && mSocket.isConnected()) {
                NetworkBinaryMessage nbm = new NetworkBinaryMessage();
                //<editor-fold desc="Get NetworkBinaryMessage">
                try {
                    while (headerBytesFound != headerBytes.length) { //while we haven't found all the header bytes
                        stream.read(header, headerBytesFound, 1);
                        if (header[headerBytesFound] == headerBytes[headerBytesFound]) {
                            headerBytesFound++;
                        } else if (header[headerBytesFound] == headerBytes[0]) {
                            headerBytesFound = 1;
                        }
                    }
                    stream.read(version, 0, 1);
                    stream.read(type, 0, 1);
                    stream.read(length, 0, 4);
                    stream.read(sentAt, 0, 4);
                    stream.skip(4);

                    nbm.version = (int) version[0];
                    nbm.type = MessageType.fromInt(type[0]);
                    nbm.length = ByteBuffer.wrap(length).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    nbm.sentAt = ByteBuffer.wrap(sentAt).order(ByteOrder.LITTLE_ENDIAN).getInt();
                    nbm.receivedAt = System.currentTimeMillis() - mStartTime;

                    if (nbm.length == 0) {
                        nbm.message = "";
                    } else {
                        byte[] message = new byte[nbm.length];
                        int received = 0;
                        while (received < nbm.length) {
                            received += stream.read(message, received, nbm.length - received);
                        }
                        nbm.message = new String(message);
                    }
                    //System.out.println("New Message: version(" + nbm.version + ") type(" + nbm.type + ") length(" + nbm.length + ") sentAt(" + nbm.sentAt + ") recievedAt(" + (int)nbm.receivedAt + ") message(" + nbm.message + ")");
                    headerBytesFound = 0;

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                //</editor-fold>
                //<editor-fold desc="Handle Messages">
                switch (nbm.type) {
                    case RegisterDevice:
                    case ProfileImageData:
                    case StartStreaming:
                    case StopStreaming:
                    case VRPhotoRequest:
                    case TouchInput:
                    case Pose:
                    case CapabilityResponse:
                        //case Motion:
                        //unused
                        break;
                    case Heartbeat:
                        send(createDataPacket(MessageType.Ack, String.valueOf(nbm.sentAt)));
                        break;
                    case Disconnect:
                        try {
                            mSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case VRPhotoData:
                        break;
                    case KeyboardRequest:
                        break;
                    case KeyboardInput:
                        break;
                    case VibrationRequest:
                        break;
                    case CalibrationTranslationalOffset:
                        break;
                    case CalibrationRotationalOffset:
                        break;
                    case Accelerometer:
                        break;
                    case Gyroscope:
                        break;
                    case CameraResponse:
                        break;
                    case CameraStart:
                        break;
                    case Ack:
                        try {
                            mCurrentPing = (int) (nbm.receivedAt - Integer.valueOf(nbm.message));
                        } catch (Exception e) {
                            Log.e(TAG, "got illegal number while trying to find ping");
                        }

                        break;
                    //</editor-fold>
                }
                invokeIncomingMessageEvent(nbm);
            }
        }
    }

    private void setStatus(Status status){
        synchronized (mConnectionLock){
            mConnectionStatus = status;
        }
    }

    public Status getStatus(){
        synchronized (mConnectionLock){
            return mConnectionStatus;
        }
    }

    public enum Status {
        Connected,   // we have a successsful connection to the server
        Unconnected, // no connection attempted yet
        Failure      // attempted to connect but failed
    }

    public enum MessageType { // currently 11
        //suffixes
        //Data: for byte arrays
        //Input: user explicit input
        //Request: asking for something of the other

        Heartbeat(0),           //blank packet sent to server to make sure it's still there

        RegisterDevice(1),      //tells server device details
        ProfileImageData(2),    //user's profile image
        Disconnect(8),          //asks server to disconnect phone

        StartStreaming(3),      //asks server to start sending it frames
        StopStreaming(4),       //asks server to pause stream without disconnecting

        VRPhotoRequest(5),      //sent from device to server when requesting a high res image of scene
        VRPhotoData(6),         //data sent back from server following VRPhotoRequest

        TouchInput(7),          //information about phone's current touch state

        Pose(9),                //device calculated position and rotation to send to server

        CapabilityRequest(10),  //requests the use of a feature on the device
        //request must be sent before using a capability
        CapabilityResponse(11), //the device's response to the request
        //(whether or not the server can use that feature)
        //different capabilities will then behave differently from here on out

        KeyboardRequest(12),    //requests for use of the device's keyboard
        KeyboardInput(13),      //key pressed from keyboard

        VibrationRequest(14),   //requests the device to vibrate

        CalibrationRequest(15), //requests the device to initialise calibration routine
        CalibrationTranslationalOffset(16), //two vector3's representing position of device relative to a known point
        CalibrationRotationalOffset(17),    //angle relative to the known point to position device in 3d space

        Accelerometer(18),      //Accelerometer data
        Gyroscope(19),          //Gyroscope data

        CameraResponse(20),     //Data on the cameras connected to the device
        CameraStart(21),        //Data about which camera the server has selected to start stream on

        ExternalTrackerCountRequest(22), // asks server how many trackers are available
        ExternalTrackerCount(23),        // response from the server about how many trackers there are
        ExternalTrackerSet(24),          // set which tracker to use

        Ack(127);
        public final int value;

        private static final SparseArray<MessageType> intToTypeMap = new SparseArray<>();

        static {
            for (MessageType type : MessageType.values()) {
                intToTypeMap.put(type.value, type);
            }
        }

        MessageType(final int value) {
            this.value = value;
        }

        public static MessageType fromInt(int i) {
            MessageType type = intToTypeMap.get(i);
            if (type == null)
                return null;
            return type;
        }

        public int getValue() {
            return value;
        }
    }
}


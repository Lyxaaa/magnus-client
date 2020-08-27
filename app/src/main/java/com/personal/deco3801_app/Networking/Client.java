package com.personal.deco3801_app.Networking;

import java.util.ArrayList;
import java.util.List;

public abstract class Client {
    public static int VERSION = 0;

    public List<OnReceiveListener> OnReceiveListeners = new ArrayList<>();

    public interface OnReceiveListener {
        void OnReceive(SocketType type, DataType protocol, Object data);
    }

/**
    public UDPSocket broadcast { get; set; }
    public UDPSocket udp { get; set; }
    public TCPSocket tcp { get; set; }

    protected void OnTCP(byte[] data) {
        Receive(SocketType.TCP, data);
    }
    protected void OnUDP(byte[] data) {
        Receive(SocketType.UDP, data);
    }

    public void End() {
        udp?.End();
        tcp?.End();
    }

    public void Send(SocketType socketType, DataType dataType, byte[] data) {
        switch (socketType) {
            case SocketType.TCP:
                tcp.Send(
                        BitConverter.GetBytes(VERSION),
                        BitConverter.GetBytes((int)dataType),
                        data
                );
                break;
            case SocketType.UDP:
                udp.Send(
                        BitConverter.GetBytes(VERSION),
                        BitConverter.GetBytes((int)dataType),
                        data
                );
                break;
        }
    }

    void ReportError(SocketType type, DataType protocol, object data) {
        if (protocol == DataType.Error) {
            ClientError cast = (ClientError)data;
            Console.WriteLine(cast.exception.ToString());
        }
    }

    internal void Receive(SocketType socketType, byte[] data) {
        // parse version
        int val = BitConverter.ToInt32(data, 0);
        switch (val) {
            case 0:
                val = BitConverter.ToInt32(data, 4);
                byte[] dataSegment = new byte[data.Length - 8];
                Array.Copy(data, 8, dataSegment, 0, dataSegment.Length);

                DataType dataType = Enum.IsDefined(typeof(DataType), val) ? dataType = (DataType)val : DataType.Undefined;

                object processedData = null;

                switch (dataType) {
                    case DataType.Bytes:
                        processedData = dataSegment;
                        break;
                    case DataType.RawString:
                        processedData = Encoding.ASCII.GetString(dataSegment);
                        break;
                    case DataType.JSON:
                        string str = Encoding.ASCII.GetString(dataSegment);
                        try {
                            processedData = JsonConvert.DeserializeObject<Json.Message>(str);
                            ((Json.Message)processedData).message = str;
                        } catch (Exception e) {
                            processedData = new ClientError() {
                                message = $"Error occured while trying to parse data of type {dataType} from {socketType} socket",
                                exception = e,
                                data = data
                            };
                            dataType = DataType.Error;
                        }
                        break;
                    default:
                        throw new NotSupportedException($"Protocol {dataType} unsupported");
                }
                OnReceiveListener?.Invoke(socketType, dataType, processedData);
                break;
            default:
                throw new NotSupportedException($"Version {val} unsupported");
        }
    }
}

public class ClientError {
    public SocketType socketType; // socket type that caused the error
    public DataType protocol; // protocol that caused the error
    public String message; // custom message
    public Exception exception { get; set; } // the error
    public byte[] data { get; set; } // more error data
}
*/
}

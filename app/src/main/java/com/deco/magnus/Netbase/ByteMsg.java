package com.deco.magnus.Netbase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteMsg {
    public static byte[] TryCast(DataType dataType, Object data, int identifier) {
        if (dataType == DataType.Bytes) {
            byte[] idArray = new byte[4];
            byte[] dataArray = (byte[]) data;
            System.arraycopy(dataArray, 0, idArray, 0, 4);

            int id = ByteBuffer.wrap(idArray).order(ByteOrder.LITTLE_ENDIAN).getInt();
            if (identifier == id) {
                byte[] ret = new byte[dataArray.length - 4];
                System.arraycopy(dataArray, 4, ret, 0, dataArray.length - 4);
                return ret;
            }
        }
        return null;
    }
}

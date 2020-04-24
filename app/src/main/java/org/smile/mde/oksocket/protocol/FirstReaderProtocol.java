package org.smile.mde.oksocket.protocol;

import android.util.Log;

import com.smile.commonlib.util.ByteUtil;
import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;

/**
 * OkSocket 读取数据时使用的数据解析协议
 */
public class FirstReaderProtocol implements IReaderProtocol {

    private final String TAG = getClass().getSimpleName();

    @Override
    public int getHeaderLength() {
        return 6;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        Log.e(TAG, "getBodyLength: header = " + header + ",  byteOrder = " + byteOrder);
        if (header == null) {
            return 0;
        }
        if (header.length < getHeaderLength()) {
            return 0;
        }

        byte[] len = new byte[4];
        System.arraycopy(header, 2, len, 0, len.length);

        for (int i = 0; i < header.length; i++) {
            Log.e(TAG, "getBodyLength: aByte = " + header[i]);
        }

        int bodyLength = ByteUtil.getBInt(len, 0);
        Log.e(TAG, "bodyLength = " + bodyLength);
        return bodyLength;
    }
}

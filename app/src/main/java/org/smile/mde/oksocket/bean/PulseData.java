package org.smile.mde.oksocket.bean;

import android.util.Log;

import com.smile.commonlib.util.JsonUtil;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * 心跳包数据
 */
public class PulseData implements IPulseSendable {
    private String code;

    public PulseData(String code) {
        this.code = code;
    }

    private void setCode(String code) {
        this.code = code;
    }

    private String getCode() {
        return code;
    }

    @Override
    public byte[] parse() {
        byte[] body = JsonUtil.toJson(this).getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(body);
        byte[] bytes = bb.array();
        Log.e(PulseData.class.getSimpleName(), "心跳包数据大小 = " + bytes.length);
        return bytes;
    }
}

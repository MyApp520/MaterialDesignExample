package org.smile.mde.oksocket.bean;

import com.smile.commonlib.util.JsonUtil;
import com.xuhao.didi.core.iocore.interfaces.ISendable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class LoginSendData implements ISendable {
    /**
     * code : 30001
     * policeNo : 105405
     * sessionid : mobileVideo:login:app:25ec08e3408f4217b528332fd1c403db
     * imei : test
     * userId : 9KiPec5EjxeGJ5V1ybo
     */

    private String code;
    private String policeNo;
    private String sessionid;
    private String imei;
    private String userId;

    public LoginSendData(String code) {
        this.code = code;
    }

    public LoginSendData(String code, String policeNo, String sessionid, String imei, String userId) {
        this.code = code;
        this.policeNo = policeNo;
        this.sessionid = sessionid;
        this.imei = imei;
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 第一种方式
     *
     * @return
     */
    @Override
    public byte[] parse() {
        //根据服务器的解析规则,构建byte数组
        byte[] body = JsonUtil.toJson(this).getBytes(Charset.forName("utf-8"));
        ByteBuffer bb = ByteBuffer.allocate(body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(body);
        return bb.array();
    }

//    /**
//     * 第二种方式
//     *
//     * @return
//     */
//    @Override
//    public byte[] parse() {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        byte[] body = JsonUtil.toJson(this).getBytes(Charset.forName("utf-8"));
//        bos.write(body, 0, body.length);
//        byte[] res = bos.toByteArray();
//        try {
//            bos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return res;
//    }
}

package org.smile.mde.bean;

/**
 * Created by smile on 2019/6/20.
 */

public class LocationResponse {

    /**
     * status : 1
     * msg : 打卡成功
     */

    private int status;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

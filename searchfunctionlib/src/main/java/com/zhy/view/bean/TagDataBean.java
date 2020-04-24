package com.zhy.view.bean;

/**
 * Created by xh_smile on 2019/11/14.
 */

public class TagDataBean {
    private String id;
    private String name;
    private long timeMillis;

    public TagDataBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagDataBean(String id, String name, long timeMillis) {
        this.id = id;
        this.name = name;
        this.timeMillis = timeMillis;
    }

    public TagDataBean(String name, long timeMillis) {
        this.name = name;
        this.timeMillis = timeMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private long getTimeMillis() {
        return timeMillis;
    }

    private void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }
}

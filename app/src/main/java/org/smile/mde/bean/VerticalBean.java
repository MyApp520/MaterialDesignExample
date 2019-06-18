package org.smile.mde.bean;

import java.util.List;

/**
 * Created by smile on 2019/5/22.
 */

public class VerticalBean {
    private String name;
    private List<TwoBean> twoBeanList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TwoBean> getTwoBeanList() {
        return twoBeanList;
    }

    public void setTwoBeanList(List<TwoBean> twoBeanList) {
        this.twoBeanList = twoBeanList;
    }
}

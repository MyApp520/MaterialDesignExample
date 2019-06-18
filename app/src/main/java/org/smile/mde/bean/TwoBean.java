package org.smile.mde.bean;

import java.util.List;

/**
 * Created by smile on 2019/5/22.
 */

public class TwoBean {
    private String name;
    private List<String> childListData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getChildListData() {
        return childListData;
    }

    public void setChildListData(List<String> childListData) {
        this.childListData = childListData;
    }
}

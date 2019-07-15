package org.smile.mde.bean;

import java.util.List;

/**
 * Created by smile on 2019/7/6.
 */

public class PeopleInfoBean {
    /**
     * code : 1
     * data : [{"date":[{"name":"张丹","gender":"男"}]}]
     */

    private int code;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DateBean> date;

        public List<DateBean> getDate() {
            return date;
        }

        public void setDate(List<DateBean> date) {
            this.date = date;
        }

        public static class DateBean {
            /**
             * name : 张丹
             * gender : 男
             */

            private String name;
            private String gender;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }
        }
    }
}

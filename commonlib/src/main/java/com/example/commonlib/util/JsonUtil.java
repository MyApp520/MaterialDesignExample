package com.example.commonlib.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by smile on 2019/4/22.
 */

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static String toJson(Object object) {
        if (object == null) {
            return "";
        }

        try {
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> Object fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    /**
     * @param json
     * @param typeOfT
     * @return
     * @MethodName : fromJson
     * @Description : 用来将JSON串转为对象，此方法可用来转带泛型的集合，如：Type为
     * new TypeToken<List<T>>(){}.getType()
     * ，其它类也可以用此方法调用，就是将List<T>替换为你想要转成的类
     */
    public static Object fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }
}

package com.example.commonlib.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 转成list
     * 解决泛型问题
     *
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static  <T> List<T> jsonToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }
}

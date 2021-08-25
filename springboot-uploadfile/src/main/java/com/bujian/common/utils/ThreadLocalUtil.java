package com.bujian.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

/**
 * 线程数据存储工具
 *
 * @author bujian
 * @date 2021/7/5 14:41
 */
public class ThreadLocalUtil {
    private ThreadLocalUtil() {
    }

    private static ThreadLocal<JSONObject> localVar;
    private static final int INITIAL_CAPACITY = 16;

    public static void put(JSONObject json) {
        localVar = localVar == null ? new ThreadLocal<>() : localVar;
        JSONObject jsonObject = localVar.get() == null ? new JSONObject(INITIAL_CAPACITY) : localVar.get();
        jsonObject.putAll(json);
        Assert.isTrue(jsonObject.size() < INITIAL_CAPACITY + 1, "超过容量上限");
        localVar.set(jsonObject);
    }

    public static <T> void put(String key, T value) {
        localVar = localVar == null ? new ThreadLocal<>() : localVar;
        JSONObject json = localVar.get() == null ? new JSONObject(INITIAL_CAPACITY) : localVar.get();
        json.put(key, value);
        localVar.set(json);
    }

    public static JSONObject get() {
        return localVar == null ? null : localVar.get();
    }

    public static Object get(String key) {
        return localVar == null ? null : localVar.get().get(key);
    }

    public static void removeAll() {
        if (localVar != null) {
            localVar.remove();
        }
    }

    public static void remove(String key) {
        if (localVar != null) {
            localVar.get().remove(key);
            if (localVar.get().isEmpty()) {
                localVar.remove();
            }
        }
    }

}

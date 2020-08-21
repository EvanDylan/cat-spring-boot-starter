package org.rhine.cat.spring.boot.internal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.dianping.cat.Cat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 调用链上下文
 */
public class TraceContext implements Cat.Context {

    private static TransmittableThreadLocal<Cat.Context> holder = new TransmittableThreadLocal<>();

    private Map<String, String> properties = new HashMap<>();

    private static final String UUID_KEY = "_cat_uuid_key";

    /**
     * 获取当前追踪id
     */
    public static String getTraceId() {
        return ((TraceContext) getContext()).properties.get(ROOT);
    }

    @Override
    public void addProperty(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        ((TraceContext) getContext()).properties.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        if (key == null) {
            return null;
        }
        return ((TraceContext) getContext()).properties.get(key);
    }

    public static Cat.Context getContext() {
        return getContext(null);
    }

    public static Cat.Context getContext(String uuid) {
        Cat.Context context = holder.get();
        if (context == null) {
            context = new TraceContext();
            if (uuid != null) {
                context.addProperty(UUID_KEY, uuid);
            }
            holder.set(context);
        }
        return context;
    }

    public static void remove() {
        if (holder.get() != null) {
            holder.remove();
        }
    }

    public static void remove(String uuid) {
        if (uuid == null) {
            remove();
        }
        Cat.Context context = holder.get();
        if (context != null) {
            String uuidKey = context.getProperty(UUID_KEY);
            if (Objects.equals(uuid, uuidKey)) {
                remove();
            }
        }
    }
}
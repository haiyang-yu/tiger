package org.tiger.api.spi;

import org.tiger.api.spi.annotation.Spi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link SpiLoader}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 09:47 周六
 */
public final class SpiLoader {

    private static final Map<String, Object> CACHE_MAP = new ConcurrentHashMap<>();

    public static void clear() {
        CACHE_MAP.clear();
    }

    public static <T> T load(Class<T> clazz) {
        return load(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T load(Class<T> clazz, String name) {
        String key = clazz.getName();
        Object obj = CACHE_MAP.get(key);
        if (obj == null) {
            T t = load0(clazz, name);
            CACHE_MAP.put(key, t);
            return t;
        } else if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        return load0(clazz, name);
    }

    private static <T> T load0(Class<T> clazz, String name) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        T t = filterByName(loader, name);
        if (t == null) {
            loader = ServiceLoader.load(clazz, SpiLoader.class.getClassLoader());
            t = filterByName(loader, name);
        }
        if (t == null) {
            throw new IllegalStateException("Cannot find META-INF/services/" + clazz.getName() + " on classpath");
        }
        return t;
    }

    private static <T> T filterByName(ServiceLoader<T> loader, String name) {
        Iterator<T> iterator = loader.iterator();
        if (name == null) {
            List<T> list = new ArrayList<>(2);
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            if (list.size() > 1) {
                list.sort((t1, t2) -> {
                    Spi spi1 = t1.getClass().getAnnotation(Spi.class);
                    Spi spi2 = t2.getClass().getAnnotation(Spi.class);
                    int order1 = spi1.order();
                    int order2 = spi2.order();
                    return order1 - order2;
                });
            }
            if (!list.isEmpty()) {
                return list.get(0);
            }
        } else {
            while (iterator.hasNext()) {
                T t = iterator.next();
                if (t.getClass().getName().equals(name) || t.getClass().getSimpleName().equals(name)) {
                    return t;
                }
            }
        }
        return null;
    }
}

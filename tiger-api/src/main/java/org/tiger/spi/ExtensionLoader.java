package org.tiger.spi;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.tiger.spi.annotation.SPI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link ExtensionLoader}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-14 09:43 周二
 */
public class ExtensionLoader {

    private static final ConcurrentMap<String, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    public static <T> T loadExtensionClasses(Class<T> clazz) {
        return loadExtensionClasses(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadExtensionClasses(Class<T> clazz, String name) {
        String key = clazz.getName();
        Object obj = EXTENSION_INSTANCES.get(key);
        if (Objects.isNull(obj)) {
            T t = load(clazz, name);
            EXTENSION_INSTANCES.putIfAbsent(key, t);
            return t;
        } else if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        return load(clazz, name);
    }

    public static void clear() {
        EXTENSION_INSTANCES.clear();
    }

    private static <T> T load(Class<T> clazz, String name) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        T t = filterByName(loader, name);
        if (Objects.isNull(t)) {
            loader = ServiceLoader.load(clazz, ExtensionLoader.class.getClassLoader());
            t = filterByName(loader, name);
        }
        if (Objects.isNull(t)) {
            throw new IllegalStateException("Cannot find META-INF/services/" + clazz.getName() + " on classpath");
        }
        return t;
    }

    private static <T> T filterByName(ServiceLoader<T> loader, String name) {
        Iterator<T> iterator = loader.iterator();
        if (StringUtils.isBlank(name)) {
            List<T> list = Lists.newArrayList();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            if (!list.isEmpty()) {
                return list.stream()
                        .min((obj1, obj2) -> {
                            SPI spi1 = obj1.getClass().getAnnotation(SPI.class);
                            SPI spi2 = obj2.getClass().getAnnotation(SPI.class);
                            int order1 = Objects.isNull(spi1) ? Integer.MAX_VALUE : spi1.order();
                            int order2 = Objects.isNull(spi2) ? Integer.MAX_VALUE : spi2.order();
                            return order1 - order2;
                        }).orElse(null);
            }
        } else {
            while (iterator.hasNext()) {
                T t = iterator.next();
                if (name.equals(t.getClass().getName()) || name.equals(t.getClass().getSimpleName())) {
                    return t;
                }
            }
        }
        return null;
    }
}

package org.tiger.tools.config.data;

import com.typesafe.config.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ConfigBean}
 *
 * @author SongQingWei
 * @since 1.0.0
 * 2020-01-11 14:40 周六
 */
public class ConfigBean {

    public static <T> T createInternal(Config config, Class<T> clazz) {
        Map<String, String> originalNameMap = new HashMap<>(16);
        for (Map.Entry<String, ConfigValue> entry : config.root().entrySet()) {
            String originalName = entry.getKey();
            // 处理字段为驼峰式命名
            String camelName = toCamelCase(originalName);
            if (!originalNameMap.containsKey(camelName) || originalName.equals(camelName)) {
                originalNameMap.put(camelName, originalName);
            }
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new ConfigException.BadBean("Could not get bean information for class " + clazz.getName(), e);
        }
        try {
            List<PropertyDescriptor> descriptors = new ArrayList<>();
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
                    continue;
                }
                descriptors.add(descriptor);
            }
            T t = clazz.newInstance();
            for (PropertyDescriptor descriptor : descriptors) {
                Method setterMethod = descriptor.getWriteMethod();
                Type parameterType = setterMethod.getGenericParameterTypes()[0];
                Class<?> parameterClass = setterMethod.getParameterTypes()[0];
                String configKey = originalNameMap.get(descriptor.getName());
                if (configKey != null) {
                    Object unwrapped = getValue(clazz, parameterType, parameterClass, config, configKey);
                    setterMethod.invoke(t, unwrapped);
                }
            }
            return t;
        } catch (InstantiationException e) {
            throw new ConfigException.BadBean(clazz.getName() + " needs a public no-args constructor to be used as a bean", e);
        } catch (IllegalAccessException e) {
            throw new ConfigException.BadBean(clazz.getName() + " getters and setters are not accessible, they must be for use as a bean", e);
        } catch (InvocationTargetException e) {
            throw new ConfigException.BadBean("Calling bean method on " + clazz.getName() + " caused an exception", e);
        }
    }

    private static Object getValue(Class<?> clazz, Type parameterType, Class<?> parameterClass,
                                   Config config, String configKey) {
        if (parameterClass == Boolean.class || parameterClass == boolean.class) {
            return config.getBoolean(configKey);
        } else if (parameterClass == Integer.class || parameterClass == int.class) {
            return config.getInt(configKey);
        } else if (parameterClass == Double.class || parameterClass == double.class) {
            return config.getDouble(configKey);
        } else if (parameterClass == Long.class || parameterClass == long.class) {
            return config.getLong(configKey);
        } else if (parameterClass == String.class) {
            return config.getString(configKey);
        } else if (parameterClass == Duration.class) {
            return config.getDuration(configKey);
        } else if (parameterClass == ConfigMemorySize.class) {
            return config.getMemorySize(configKey);
        } else if (parameterClass == Object.class) {
            return config.getAnyRef(configKey);
        } else if (parameterClass == List.class) {
            return getListValue(clazz, parameterType, config, configKey);
        } else if (parameterClass == Map.class) {
            Type[] typeArgs = ((ParameterizedType) parameterType).getActualTypeArguments();
            if (typeArgs[0] != String.class || typeArgs[1] != Object.class) {
                throw new ConfigException.BadBean("Bean property '" + configKey + "' of class " + clazz.getName() + " has unsupported Map<" + typeArgs[0] + "," + typeArgs[1] + ">, only Map<String,Object> is supported right now");
            }
            return config.getObject(configKey).unwrapped();
        } else if (parameterClass == Config.class) {
            return config.getConfig(configKey);
        } else if (parameterClass == ConfigObject.class) {
            return config.getObject(configKey);
        } else if (parameterClass == ConfigValue.class) {
            return config.getValue(configKey);
        } else if (parameterClass == ConfigList.class) {
            return config.getList(configKey);
        } else if (hasAtLeastOneBeanProperty(parameterClass)) {
            return createInternal(config.getConfig(configKey), parameterClass);
        } else {
            throw new ConfigException.BadBean("Bean property " + configKey + " of class " + clazz.getName() + " has unsupported type " + parameterType);
        }
    }

    private static Object getListValue(Class<?> clazz, Type parameterType, Config config, String configKey) {
        Type elementType = ((ParameterizedType) parameterType).getActualTypeArguments()[0];

        if (elementType == Boolean.class) {
            return config.getBooleanList(configKey);
        } else if (elementType == Integer.class) {
            return config.getIntList(configKey);
        } else if (elementType == Double.class) {
            return config.getDoubleList(configKey);
        } else if (elementType == Long.class) {
            return config.getLongList(configKey);
        } else if (elementType == String.class) {
            return config.getStringList(configKey);
        } else if (elementType == Duration.class) {
            return config.getDurationList(configKey);
        } else if (elementType == ConfigMemorySize.class) {
            return config.getMemorySizeList(configKey);
        } else if (elementType == Object.class) {
            return config.getAnyRefList(configKey);
        } else if (elementType == Config.class) {
            return config.getConfigList(configKey);
        } else if (elementType == ConfigObject.class) {
            return config.getObjectList(configKey);
        } else if (elementType == ConfigValue.class) {
            return config.getList(configKey);
        } else {
            throw new ConfigException.BadBean("Bean property '" + configKey + "' of class " + clazz.getName() + " has unsupported list element type " + elementType);
        }
    }

    private static boolean hasAtLeastOneBeanProperty(Class<?> clazz) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            return false;
        }
        for (PropertyDescriptor beanProp : beanInfo.getPropertyDescriptors()) {
            if (beanProp.getReadMethod() != null && beanProp.getWriteMethod() != null) {
                return true;
            }
        }
        return false;
    }

    private static String toCamelCase(String originalName) {
        String[] words = originalName.split("-+");
        StringBuilder builder = new StringBuilder(originalName.length());
        for (String word : words) {
            if (builder.length() == 0) {
                builder.append(word);
            } else {
                builder.append(word.substring(0, 1).toUpperCase());
                builder.append(word.substring(1));
            }
        }
        return builder.toString();
    }
}

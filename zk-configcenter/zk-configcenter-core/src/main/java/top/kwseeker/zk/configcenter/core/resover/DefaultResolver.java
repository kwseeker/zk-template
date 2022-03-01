package top.kwseeker.zk.configcenter.core.resover;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class DefaultResolver extends Resolver<String> {

    public DefaultResolver(Class<?> clazz, Field field) {
        super(clazz, field);
    }

    @Override
    public String toString() {
        return "DefaultResolver";
    }

    @Override
    public String get() {
        return getStr(clazz, field);
    }

    @Override
    public void set(String src) {
        Object value = null;
        Class<?> type = field.getType();
        if (type == String.class) {
            value = src;
        } else if (type == Boolean.class || type == boolean.class) {
            value = Boolean.valueOf(src);
        } else if (type == Integer.class || type == int.class) {
            value = Integer.valueOf(src);
        } else if (type == Long.class || type == long.class) {
            value = Long.valueOf(src);
        } else if (type == Double.class || type == double.class) {
            value = Double.valueOf(src);
        } else if (type == Float.class || type == float.class) {
            value = Float.valueOf(src);
        } else if (type == Short.class || type == short.class) {
            value = Short.valueOf(src);
        } else if (FieldType.class.isAssignableFrom(type)) {
            try {
                value = ((FieldType) field.get(clazz)).valueOf(src);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            log.info("UNKNOWN TYPE!");
            return;
        }
        setValue(clazz, field, value);
    }
}

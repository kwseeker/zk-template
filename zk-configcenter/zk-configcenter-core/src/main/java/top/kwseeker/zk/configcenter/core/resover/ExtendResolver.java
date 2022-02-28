package top.kwseeker.zk.configcenter.core.resover;

import lombok.extern.slf4j.Slf4j;
import top.kwseeker.zk.configcenter.core.ExtendDataStore;
import top.kwseeker.zk.configcenter.core.Resolver;

import java.lang.reflect.Field;

@Slf4j
public class ExtendResolver<T> extends Resolver<T> {

    private String tempKey;
    private ExtendDataStore<T> store;

    public ExtendResolver(Class clazz, Field field) {
        super(clazz, field);
    }

    public ExtendResolver(String tempKey, ExtendDataStore<T> store, Class clazz, Field field) {
        super(clazz,field);
        this.tempKey = tempKey;
        this.store = store;
    }

    @Override
    public T get() {
        return (T) tempKey;
    }

    @Override
    public void set(String src) {
        if(src == null) {
            log.info("temp key null null");
            return;
        }
        T t = store.getValue(src);
        try {
            field.setAccessible(true);
            field.set(clazz,t);
        } catch (IllegalAccessException e) {
            log.debug("illegal access exception..", e);
        }
    }

    public String getTempKey() {
        return tempKey;
    }

    public void setTempKey(String tempKey) {
        this.tempKey = tempKey;
    }
}

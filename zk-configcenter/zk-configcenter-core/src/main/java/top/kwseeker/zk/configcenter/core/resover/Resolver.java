package top.kwseeker.zk.configcenter.core.resover;

import lombok.extern.slf4j.Slf4j;
import top.kwseeker.zk.configcenter.core.utils.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 反射设置/读取类实例的Field值
 */
@Slf4j
public abstract class Resolver<T> {

    protected Class<?> clazz;
    protected Field field;

    public Resolver(Class<?> clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
    }

    /**
     * 获取配置Field的值
     */
    public abstract T get();

    /**
     * 设置Field的值
     */
    public abstract void set(String src);


    protected String getStr(Class<?> clazz,Field field){
        try {
            ReflectionUtils.makeAccessible(field);
            return field.get(clazz).toString();//can not be (String)field.get(clazz)
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.debug("illegal access exception..",e);
        }
        return "";
    }

    protected void setValue(Class clazz,Field field,Object value) {
        try {
            ReflectionUtils.makeAccessible(field);
            field.set(clazz, value);
        } catch (IllegalArgumentException e) {
            log.debug("illegal agreements exception..",e);
        } catch (IllegalAccessException e) {
            log.debug("illegal access exception..",e);
        }
    }
}

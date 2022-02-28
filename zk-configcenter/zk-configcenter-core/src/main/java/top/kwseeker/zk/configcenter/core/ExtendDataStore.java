package top.kwseeker.zk.configcenter.core;

/**
 *
 */
public interface ExtendDataStore<T> {
    /**
     * 把值设置到扩展存储中
     *
     * @param key       扩展存储存储位置
     * @param t         值
     */
    public void setValue(String key,T t);

    /**
     * 从扩展存储中获取值
     *
     * @param key       扩展存储存储位置
     * @return
     */
    public T getValue(String key);
}

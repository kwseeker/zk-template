/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package top.kwseeker.zk.configcenter.core;

/**
 * <b>项目名</b>： ucc <br>
 * <b>包名称</b>： com.bitium10.ucc.agent <br>
 * <b>类名称</b>： ExtendDataStore <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/4/6 8:39<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
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

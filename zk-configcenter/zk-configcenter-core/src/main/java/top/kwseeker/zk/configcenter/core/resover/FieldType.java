package top.kwseeker.zk.configcenter.core.resover;

/**
 * 如果配置项是一个复合对象，那么这个对象必须继承自FieldType
 *
 * 例如：有一个应用程序，他必须运行在一个特定的配置环境下，那么我们可以这样干：
 *
 * 1、现定义一个环境需求的类型，且继承自FieldType：
 * class Requirements extends FieldType {
 *      String cpu;
 *      String cache;
 *      String ...
 * }
 *
 * 2、使用这个类型：
 *
 * @ZkFieldConfigurable(....)
 * Requirements　requirements
 */
public abstract class FieldType {
    /**
     *
     * @param src
     * @return
     */
    public abstract FieldType valueOf(String src);

    /**
     * use a string to description the class
     * @return
     */
    public abstract String toString();
}

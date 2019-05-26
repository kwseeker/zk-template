package top.kwseeker.zk.discovery;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * 微服务描述拓展信息, ServiceInstance中已经有了一些IP、port、服务名等信息
 * 这里面就主要是提供服务的接口信息
 */
@JsonRootName("detail")
public class ServiceDetail {

    private String        description;

    public ServiceDetail()
    {
        this("");
    }

    public ServiceDetail(String description)
    {
        this.description = description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}


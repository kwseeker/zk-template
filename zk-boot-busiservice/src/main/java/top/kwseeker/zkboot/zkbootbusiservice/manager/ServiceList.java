package top.kwseeker.zkboot.zkbootbusiservice.manager;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务名单
 * 服务与发现的本地缓存服务列表
 *
 */
public class ServiceList {

    private static final Map<String, List<Service>> serviceList = new ConcurrentHashMap<>();
}

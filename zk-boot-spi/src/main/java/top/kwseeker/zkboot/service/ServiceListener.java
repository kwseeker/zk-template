package top.kwseeker.zkboot.service;

public interface ServiceListener {

    void onServiceAdded(String path, ServiceNode node);

    void onServiceUpdated(String path, ServiceNode node);

    void onServiceRemoved(String path, ServiceNode node);
}

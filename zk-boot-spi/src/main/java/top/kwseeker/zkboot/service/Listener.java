package top.kwseeker.zkboot.service;

public interface Listener {

    void onSuccess(Object... args);

    void onFailure(Throwable cause);
}

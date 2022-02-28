package top.kwseeker.zk.configcenter.core.listener;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import top.kwseeker.zk.configcenter.core.operator.Updater;

@Slf4j
public class DataChangeListener implements IZkDataListener {

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        log.warn("change event : " + dataPath + " data:" + data);
        Updater.update(dataPath, data.toString());
    }

    @Override
    public void handleDataDeleted(String s) throws Exception {

    }
}

package top.kwseeker.zk.srcDebug;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class MyWatcher implements Watcher {

    private boolean printWatches = true;

    public boolean getPrintWatches() {
        return printWatches;
    }

    public void process(WatchedEvent event) {
        if (getPrintWatches()) {
            System.out.println("\n" + "WATCHER::");
            System.out.println(event.toString());
        }
    }
}

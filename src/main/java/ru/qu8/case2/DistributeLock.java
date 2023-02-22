package ru.qu8.case2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributeLock {

    private final String connectString = "150.230.45.169:2181,150.230.35.86:2181,150.230.38.161:2181";
    private final int sessionTimeout = 5000;
    ZooKeeper zk;
    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    String currentLock;
    String waitPath;

    public DistributeLock() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState()==Event.KeeperState.SyncConnected){
                    connectLatch.countDown();
                }

                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)){
                    waitLatch.countDown();
                }

            }
        });

        connectLatch.await();

        Stat stat = zk.exists("/locks", false);

        if (stat == null){
            String zkLock = zk.create("/locks", "zk lock".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }

    public void zkLock(){
        try {
            currentLock = zk.create("/locks/seq-", "zk subNode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            Thread.sleep(10);
            List<String> children = zk.getChildren("/locks", false);
            if (children.size() == 1){
                return;
            }
            Collections.sort(children);
            String currentNode = currentLock.substring("/locks/".length());
            int index = children.indexOf(currentNode);
            if (index == -1){
                System.out.println("数据错误");
            }else if (index == 0){
                return;
            }else {
                this.waitPath = "/locks/"+children.get(index-1);
                zk.getData(waitPath,true,null);

                waitLatch.await();
                return;
            }

        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void zkUnLock() throws InterruptedException, KeeperException {
        zk.delete(currentLock,-1);
    }

}

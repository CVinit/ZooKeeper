package ru.qu8.case1;

import org.apache.zookeeper.*;

import java.io.IOException;

public class DistributeServer {
    private static String connectString = "150.230.45.169:2181,150.230.35.86:2181,150.230.38.161:2181";
    private static int sessionTimeout = 2000;
    ZooKeeper zk;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeServer server = new DistributeServer();

        server.getConn();

        server.regist(args[0]);

        server.bussiness();
    }

    private void bussiness() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void regist(String hostname) throws InterruptedException, KeeperException {
        String created = zk.create("/servers/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + "is online!");
    }

    private void getConn() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}

package ru.qu8.case3;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class zkCuratorTest {
    private String rootNode = "/locks";
    private String connectString = "150.230.45.169:2181,150.230.35.86:2181,150.230.38.161:2181";

    public static void main(String[] args) {
        new zkCuratorTest().test();
    }

    public void test(){
        InterProcessMutex lock1 = new InterProcessMutex(getCuratorFramework(), rootNode);
        InterProcessMutex lock2 = new InterProcessMutex(getCuratorFramework(), rootNode);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.acquire();
                    System.out.println("线程1获取锁");
                    lock1.release();
                    System.out.println("线程1释放锁");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.acquire();
                    System.out.println("线程2获取锁");
                    lock2.release();
                    System.out.println("线程2释放锁");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private CuratorFramework getCuratorFramework() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(2000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString(connectString).connectionTimeoutMs(2000).sessionTimeoutMs(2000).retryPolicy(retry).build();
        client.start();
        System.out.println("zookeeper 初始化完成..............");
        return client;
    }
}

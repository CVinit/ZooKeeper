package ru.qu8.case2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributeLock lock1 = new DistributeLock();
        DistributeLock lock2 = new DistributeLock();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.zkLock();
                    System.out.println("lock1上锁");
                    Thread.sleep(5 * 1000);
                    lock1.zkUnLock();
                    System.out.println("lock1解锁");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.zkLock();
                    System.out.println("lock2上锁");
                    Thread.sleep(5 * 1000);
                    lock2.zkUnLock();
                    System.out.println("lock2解锁");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}

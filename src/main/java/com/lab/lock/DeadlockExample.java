package com.lab.lock;

import org.springframework.stereotype.Service;

@Service
public class DeadlockExample {
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public void deadlockRunner () {
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("Thread 1: Locked A");

                sleep(100); // giả lập xử lý

                System.out.println("Thread 1: Waiting for B...");
                synchronized (lockB) {
                    System.out.println("Thread 1: Locked B");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("Thread 2: Locked B");

                sleep(100); // giả lập xử lý

                System.out.println("Thread 2: Waiting for A...");
                synchronized (lockA) {
                    System.out.println("Thread 2: Locked A");
                }
            }
        });

        t1.start();
        t2.start();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }
}

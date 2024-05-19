package com.virtualthreads;

import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedVsReentrantLock {

    private static final int NUMBER_OF_SLEEP_MILLISECONDS = 3000;

    public static synchronized void synchronizedMethod() {
        try {
            System.out.println("Synchronized method - before" + Thread.currentThread());
            Thread.sleep(NUMBER_OF_SLEEP_MILLISECONDS);
            System.out.println("Synchronized method - after" + Thread.currentThread());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reentrantLockMethod() {
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lock();
            System.out.println("ReentrantLock method - before" + Thread.currentThread());
            Thread.sleep(NUMBER_OF_SLEEP_MILLISECONDS);
            System.out.println("ReentrantLock method - after" + Thread.currentThread());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}

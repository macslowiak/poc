package com.virtualthreads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class VirtualThreadsApplication {

    private final static int NUMBER_OF_VIRTUAL_THREADS = 4;
    private final static int NUMBER_OF_METHOD_INVOKATIONS = 6;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

        try (ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_VIRTUAL_THREADS, virtualThreadFactory)) {
            System.out.println("Starting synchronized method");
            for (int i = 0; i < NUMBER_OF_METHOD_INVOKATIONS; i++) {
                executor.submit(SynchronizedVsReentrantLock::synchronizedMethod).get();
            }

            System.out.println("Starting reentrantlock method");
            for (int i = 0; i < NUMBER_OF_METHOD_INVOKATIONS; i++) {
                executor.submit(SynchronizedVsReentrantLock::reentrantLockMethod).get();
            }
            executor.shutdown();
        }
    }
}

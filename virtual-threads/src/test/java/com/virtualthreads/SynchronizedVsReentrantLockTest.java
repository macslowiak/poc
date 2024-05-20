package com.virtualthreads;

import com.virtualthreads.model.ThreadData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

class SynchronizedVsReentrantLockTest {

    private final static int NUMBER_OF_VIRTUAL_THREADS = 10;
    private final static int NUMBER_OF_METHOD_INVOCATIONS = 30;
    private final static int SERVICE_CALL_DURATION_IN_MILIS = 300;

    private final VirtualThreadsService synchronizedVsReentrantLock = new VirtualThreadsService(
            new ReentrantLock(), SERVICE_CALL_DURATION_IN_MILIS);


    @Test
    @Timeout(1)
    // fast and furious
    void shouldExecuteDefaultMethod() {
        //given
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();
        long start = System.currentTimeMillis();

        //when
        try (ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_VIRTUAL_THREADS, virtualThreadFactory)) {
            System.out.println("Starting synchronized method");
            for (int i = 0; i < NUMBER_OF_METHOD_INVOCATIONS; i++) {
                executor.submit(synchronizedVsReentrantLock::defaultMethod);
            }
            executor.shutdown();
        }

        //then
        long end = System.currentTimeMillis();
        System.out.println("Execution time: " + (end - start));
        assertThat(end - start)
                .isLessThan(1000);

    }

    @Test
    // Synchronized method is blocking and virtual threads cannot be unattached from OS threads
    void shouldExecuteSynchronizedMethod() {
        //given
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();
        List<Future<ThreadData>> obtainedThreadData = new ArrayList<>();

        //when
        try (ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_VIRTUAL_THREADS, virtualThreadFactory)) {
            System.out.println("Starting synchronized method");
            for (int i = 0; i < NUMBER_OF_METHOD_INVOCATIONS; i++) {
                var threadData = executor.submit(synchronizedVsReentrantLock::synchronizedMethod);
                obtainedThreadData.add(threadData);
            }
            executor.shutdown();
        }

        //then
        List<String> threadsBeforeCall = obtainedThreadData.stream()
                .map(future -> mapFutureToThreadData(future).getThreadBeforeCall())
                .toList();
        List<String> threadsAfterCall = obtainedThreadData.stream()
                .map(future -> mapFutureToThreadData(future).getThreadAfterCall())
                .toList();
        assertThat(threadsBeforeCall).isEqualTo(threadsAfterCall);
    }

    @Test
    // Virtual Thread can be unattached from OS thread, so OS thread can be used elsewhere
    void shouldExecuteReentrantLockMethod() {
        //given
        ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();
        List<Future<ThreadData>> obtainedThreadData = new ArrayList<>();

        //when and then
        try (ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_VIRTUAL_THREADS, virtualThreadFactory)) {
            System.out.println("Starting synchronized method");
            for (int i = 0; i < NUMBER_OF_METHOD_INVOCATIONS; i++) {
                var threadData = executor.submit(synchronizedVsReentrantLock::reentrantLockMethod);
                obtainedThreadData.add(threadData);
            }
            executor.shutdown();
        }

        //then
        List<String> threadsBeforeCall = obtainedThreadData.stream()
                .map(future -> mapFutureToThreadData(future).getThreadBeforeCall())
                .toList();
        List<String> threadsAfterCall = obtainedThreadData.stream()
                .map(future -> mapFutureToThreadData(future).getThreadAfterCall())
                .toList();
        assertThat(threadsBeforeCall).isNotEqualTo(threadsAfterCall);

    }

    private ThreadData mapFutureToThreadData(Future<ThreadData> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
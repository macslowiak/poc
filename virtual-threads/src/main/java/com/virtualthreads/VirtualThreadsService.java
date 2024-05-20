package com.virtualthreads;

import com.virtualthreads.model.CallType;
import com.virtualthreads.model.ThreadData;

import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadsService {
    private final int serviceCallDurationInMilis;
    private final ReentrantLock reentrantLock;

    public VirtualThreadsService(ReentrantLock reentrantLock, int serviceCallDurationInMilis) {
        this.serviceCallDurationInMilis = serviceCallDurationInMilis;
        this.reentrantLock = reentrantLock;
    }

    public ThreadData defaultMethod() {
        var threadData = new ThreadData();
        try {
            threadData.logAndGetThreadDataBeforeCall(CallType.DEFAULT);
            Thread.sleep(serviceCallDurationInMilis);
            threadData.logAndGetThreadDataAfterCall(CallType.DEFAULT);
            return threadData;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized ThreadData synchronizedMethod() {
        var threadData = new ThreadData();
        try {
            threadData.logAndGetThreadDataBeforeCall(CallType.SYNCHRONIZED);
            Thread.sleep(serviceCallDurationInMilis);
            threadData.logAndGetThreadDataAfterCall(CallType.SYNCHRONIZED);
            return threadData;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ThreadData reentrantLockMethod() {
        var threadData = new ThreadData();
        try {
            reentrantLock.lock();
            threadData.logAndGetThreadDataBeforeCall(CallType.REENTRANT_LOCK);
            Thread.sleep(serviceCallDurationInMilis);
            threadData.logAndGetThreadDataAfterCall(CallType.REENTRANT_LOCK);
            return threadData;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            reentrantLock.unlock();
        }
    }


}

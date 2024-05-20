package com.virtualthreads.model;

import lombok.Data;

@Data
public class ThreadData {
    private String threadBeforeCall;
    private String threadAfterCall;

    public void logAndGetThreadDataBeforeCall(CallType callType) {
        System.out.println(STR."\{callType}before\{Thread.currentThread()}");
        this.setThreadBeforeCall(Thread.currentThread().toString());
    }

    public void logAndGetThreadDataAfterCall(CallType callType) {
        System.out.println(STR."\{callType}after\{Thread.currentThread()}");
        this.setThreadAfterCall(Thread.currentThread().toString());
    }
}

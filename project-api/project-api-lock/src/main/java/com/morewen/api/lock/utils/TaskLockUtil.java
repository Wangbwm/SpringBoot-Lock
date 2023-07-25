package com.morewen.api.lock.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * @author Wangbw
 */
public interface TaskLockUtil {
    int getTypeId();
    // 锁名 + 异步方法
    boolean tryAcquireLock(String lockName, Callable<Boolean> callable);
}

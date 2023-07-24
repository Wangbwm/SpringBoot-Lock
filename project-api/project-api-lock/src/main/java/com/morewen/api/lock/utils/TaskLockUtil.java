package com.morewen.api.lock.utils;

/**
 * @author Wangbw
 */
public interface TaskLockUtil {
    int getTypeId();
    boolean tryAcquireLock(String lockName);
    void releaseLock(String lockName);
}

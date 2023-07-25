package com.morewen.api.lock.utils.impl;

import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.morewen.api.lock.generator.service.DistributedLocksService;
import com.morewen.api.lock.utils.TaskLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Wangbw
 * @description 乐观锁实现
 * @createDate 2023-07-24 15:45:08
 */
@Component
public class TaskOptimisticLockImpl implements TaskLockUtil {
    private final DistributedLocksService lockService;
    @Autowired
    public TaskOptimisticLockImpl(DistributedLocksService lockService) {
        this.lockService = lockService;
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean tryAcquireLock(String lockName, Callable<Boolean> callable) {
        DistributedLocks lock = lockService.selectLockByName(lockName);
        if (lock == null) {
            lock = new DistributedLocks();
            lock.setLockName(lockName);
            lock.setVersion(1);
            lockService.save(lock);
        }
        if (lock.getVersion() == -1) { // 悲观锁占用
            return false;
        } else {
            int currentVersion = lock.getVersion();
            lock.setVersion(currentVersion + 1);
            lock.setCurrentVersion(currentVersion);
            // 在tryAcquireLock中执行业务逻辑
            try {
                CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                });
                boolean result = future.get(); // 等待业务逻辑执行完成并获取结果

                if (result) {
                    // 业务逻辑执行成功，释放锁并提交事务
                    if (releaseLock(lockName, lock)) {
                        return true;
                    } else {
                        throw new RuntimeException("释放锁失败-version不匹配");
                    }
                } else {
                    // 业务逻辑执行失败，回滚事务
                    throw new RuntimeException("业务逻辑执行失败");
                }
            } catch (InterruptedException | ExecutionException e) {
                // 线程执行中断或执行异常，回滚事务
                throw new RuntimeException("业务逻辑执行异常", e);
            }
        }
    }

    public boolean releaseLock(String lockName, DistributedLocks lock) {
        if (Objects.isNull(lock)) {
            return false;
        }
        boolean check = lockService.updateLockStatusWithVersion(lock) == 1;
        if (check) {
            lockService.removeByName(lockName);
            return true;
        } else {
            throw new RuntimeException("释放锁失败-version不匹配");
        }
    }
}

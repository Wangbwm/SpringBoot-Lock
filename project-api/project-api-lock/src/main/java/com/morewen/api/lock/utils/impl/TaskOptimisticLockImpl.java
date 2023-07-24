package com.morewen.api.lock.utils.impl;

import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.morewen.api.lock.generator.mapper.DistributedLocksMapper;
import com.morewen.api.lock.utils.TaskLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wangbw
 * @description 乐观锁实现
 * @createDate 2023-07-24 15:45:08
 */
@Component
public class TaskOptimisticLockImpl implements TaskLockUtil {
    private final DistributedLocksMapper lockMapper;

    @Autowired
    public TaskOptimisticLockImpl(DistributedLocksMapper lockMapper) {
        this.lockMapper = lockMapper;
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public boolean tryAcquireLock(String lockName) {
        DistributedLocks lock = lockMapper.selectForName(lockName);
        if (lock == null) {
            lock = new DistributedLocks();
            lock.setLockName(lockName);
            lock.setVersion(0);
            lock.setLocked(1);
            return lockMapper.insert(lock) == 1;
        } else if (!lock.ifLocked()) {
            int currentVersion = lock.getVersion();
            lock.setVersion(currentVersion + 1);
            lock.setLocked(1);
            lock.setCurrentVersion(currentVersion);
            return lockMapper.updateLockStatusWithVersion(lock) == 1;
        }
        return false;
    }

    @Override
    public void releaseLock(String lockName) {
        DistributedLocks lock = lockMapper.selectForName(lockName);
        if (lock != null && lock.ifLocked()) {
            lock.setLocked(0);
            lockMapper.updateLockStatus(lock);
        }
    }
}

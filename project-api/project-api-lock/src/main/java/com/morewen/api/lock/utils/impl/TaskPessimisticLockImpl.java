package com.morewen.api.lock.utils.impl;

import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.morewen.api.lock.generator.mapper.DistributedLocksMapper;
import com.morewen.api.lock.utils.TaskLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wangbw
 * @description 悲观锁实现
 * @createDate 2023-07-24 15:45:08
 */
@Component
public class TaskPessimisticLockImpl implements TaskLockUtil {
    private final DistributedLocksMapper lockMapper;

    @Autowired
    public TaskPessimisticLockImpl(DistributedLocksMapper lockMapper) {
        this.lockMapper = lockMapper;
    }

    @Override
    public int getTypeId() {
        return 0;
    }
    @Override
    public boolean tryAcquireLock(String lockName) {
        DistributedLocks lock = lockMapper.selectLockForUpdate(lockName);
        if (lock == null) {
            lock = new DistributedLocks();
            lock.setLockName(lockName);
            lock.setLocked(1);
            lock.setVersion(0);
            return lockMapper.insert(lock) == 1;
        } else if (!lock.ifLocked()) {
            lockMapper.selectLockForUpdate(lockName);
            lock.setLocked(1);
            return lockMapper.updateLockStatus(lock) == 1;
        }
        return false;
    }
    @Override
    public void releaseLock(String lockName) {
        DistributedLocks lock = lockMapper.selectLockForUpdate(lockName);
        if (lock != null && lock.ifLocked()) {
            lock.setLocked(0);
            lockMapper.updateLockStatus(lock);
        }
    }


}

package com.morewen.api.lock.generator.service;

import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Wangbw
* @description 针对表【distributed_locks】的数据库操作Service
* @createDate 2023-07-25 08:21:11
*/
public interface DistributedLocksService extends IService<DistributedLocks> {

    DistributedLocks selectLockForUpdate(String lockName);
    DistributedLocks selectLockByName(String lockName);

    int updateLockStatusWithVersion(DistributedLocks lock);

    boolean removeByName(String lockName);
}

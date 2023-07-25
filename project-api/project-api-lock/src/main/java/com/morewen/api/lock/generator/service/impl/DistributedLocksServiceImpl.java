package com.morewen.api.lock.generator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.morewen.api.lock.generator.mapper.DistributedLocksMapper;
import com.morewen.api.lock.generator.service.DistributedLocksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
* @author Wangbw
* @description 针对表【distributed_locks】的数据库操作Service实现
* @createDate 2023-07-25 08:21:11
*/
@Service
public class DistributedLocksServiceImpl extends ServiceImpl<DistributedLocksMapper, DistributedLocks>
    implements DistributedLocksService {
    private final DistributedLocksMapper lockMapper;

    @Autowired
    public DistributedLocksServiceImpl(DistributedLocksMapper lockMapper) {
        this.lockMapper = lockMapper;
    }

    @Override
    public DistributedLocks selectLockForUpdate(String lockName) {
        DistributedLocks distributedLocks = selectLockByName(lockName);
        if (Objects.isNull(distributedLocks)) {
            return null;
        }
        return lockMapper.selectLockForUpdate(distributedLocks.getId());
    }

    @Override
    public DistributedLocks selectLockByName(String lockName) {
        QueryWrapper<DistributedLocks> wrapper = new QueryWrapper<>();
        wrapper.eq("lock_name", lockName);
        return lockMapper.selectOne(wrapper);
    }

    @Override
    public int updateLockStatusWithVersion(DistributedLocks lock) {
        return lockMapper.updateLockStatusWithVersion(lock);
    }

    @Override
    public boolean removeByName(String lockName) {
        QueryWrapper<DistributedLocks> wrapper = new QueryWrapper<>();
        wrapper.eq("lock_name", lockName);
        return lockMapper.delete(wrapper) > 0;
    }
}





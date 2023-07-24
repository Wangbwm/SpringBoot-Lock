package com.morewen.api.lock.generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.morewen.api.lock.generator.mapper.DistributedLocksMapper;
import com.morewen.api.lock.generator.service.DistributedLocksService;
import org.springframework.stereotype.Service;

/**
* @author Wangbw
* @description 针对表【distributed_locks】的数据库操作Service实现
* @createDate 2023-07-24 15:45:08
*/
@Service
public class DistributedLocksServiceImpl extends ServiceImpl<DistributedLocksMapper, DistributedLocks>
    implements DistributedLocksService{

}





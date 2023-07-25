package com.morewen.api.lock.generator.mapper;

import com.morewen.api.lock.generator.entity.DistributedLocks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author Wangbw
* @description 针对表【distributed_locks】的数据库操作Mapper
* @createDate 2023-07-25 08:21:11
* @Entity generator.entity.DistributedLocks
*/
@Mapper
public interface DistributedLocksMapper extends BaseMapper<DistributedLocks> {
    @Select("SELECT * FROM distributed_locks WHERE id = #{id} FOR UPDATE")
    DistributedLocks selectLockForUpdate(Integer id);
    @Update("UPDATE distributed_locks SET version = #{version} WHERE id = #{id} AND version = #{currentVersion}")
    int updateLockStatusWithVersion(DistributedLocks lock);
}





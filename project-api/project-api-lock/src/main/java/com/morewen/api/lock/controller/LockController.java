package com.morewen.api.lock.controller;

import com.morewen.api.lock.utils.CommonRedisHelper;
import com.morewen.api.lock.utils.TaskLockUtil;
import com.morewen.projectcommoncore.anno.Log;
import com.morewen.projectcommoncore.enums.BusinessType;
import com.morewen.projectcommoncore.utils.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Wangbw
 */
@RestController
@Slf4j
@RequestMapping("/lock")
public class LockController {
    private Map<Integer, TaskLockUtil> map; // 0:悲观锁 1:乐观锁
    @Autowired
    CommonRedisHelper commonRedisHelper; // redis工具类
    @Autowired
    RedissonClient redisson; // redisson客户端
    @Autowired
    public LockController(List<TaskLockUtil> list) { // 通过构造器注入所有的锁实现类
        map = new HashMap<>();
        map = list.stream().collect(Collectors.toMap(TaskLockUtil::getTypeId, Function.identity()));
    }

    // 测试乐观锁
    @GetMapping("/tryOptimisticLock")
    @Log(title = "测试乐观锁", businessType = BusinessType.OTHER)
    protected AjaxResult opLock() {
        // 1.获取乐观锁
        TaskLockUtil taskLock = map.get(1);
        return doService(taskLock) ? AjaxResult.success() : AjaxResult.error();
    }

    // 测试悲观锁
    @GetMapping("/tryPessimisticLock")
    @Log(title = "测试悲观锁", businessType = BusinessType.OTHER)
    protected AjaxResult peLock() {
        // 1.获取悲观锁
        TaskLockUtil taskLock = map.get(0);
        return doService(taskLock) ? AjaxResult.success() : AjaxResult.error();
    }

    // 业务逻辑
    private boolean doService(TaskLockUtil taskLock) {
        Callable<Boolean> callable = () -> {
            // 模拟业务执行
            Thread.sleep(10000);
            return true;
        };
        try {
            taskLock.tryAcquireLock("test", callable);
        }catch (Exception e) {
            log.error("获取锁失败", e);
            return false;
        }
        return true;
    }
    // 测试redis锁
    @GetMapping("/tryRedisLock")
    @Log(title = "测试redis锁", businessType = BusinessType.OTHER)
    protected AjaxResult redisLock() {
        commonRedisHelper.lock("test", 5000);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            log.error("获取锁失败", e);
            return AjaxResult.error(e.getMessage());
        }
        commonRedisHelper.delete("test");
        return AjaxResult.success();
    }
    // 测试redisson锁
    @GetMapping("/tryRedissonLock")
    @Log(title = "测试redisson锁", businessType = BusinessType.OTHER)
    protected AjaxResult redissonLock() {
        // 获取锁对象
        RLock lock = redisson.getLock("test");
        try{

            boolean res = lock.tryLock(2, 8, TimeUnit.SECONDS);
            if(res){ //成功
                //处理业务
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            return AjaxResult.error(e.getMessage());
        } finally {
            //释放锁
            lock.unlock();
        }
        return AjaxResult.success();
    }



}

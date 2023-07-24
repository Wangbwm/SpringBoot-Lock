package com.morewen.api.lock.controller;

import com.morewen.api.lock.utils.TaskLockUtil;
import com.morewen.projectcommoncore.anno.Log;
import com.morewen.projectcommoncore.enums.BusinessType;
import com.morewen.projectcommoncore.utils.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Wangbw
 */
@RestController
@Slf4j
@RequestMapping("/lock")
public class LockController {
    private Map<Integer, TaskLockUtil> map;
    @Autowired
    public LockController(List<TaskLockUtil> list) {
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

    private boolean doService(TaskLockUtil taskLock) {
        if (taskLock.tryAcquireLock("test")) {
            try {
                // 模拟业务执行
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.error("业务执行异常", e);
                return false;
            }
            taskLock.releaseLock("test");
            return true;
        }
        return false;
    }

}

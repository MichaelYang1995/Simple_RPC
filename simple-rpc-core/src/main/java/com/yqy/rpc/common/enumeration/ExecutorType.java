package com.yqy.rpc.common.enumeration;

import com.yqy.rpc.common.enumeration.support.ExtensionBaseType;
import com.yqy.rpc.executor.api.TaskExecutor;
import com.yqy.rpc.executor.threadPool.ThreadPoolTaskExecutor;

/**
 * @ClassName: ExecutorType
 * @Description: 任务执行器的枚举类
 *               springboot的autoConfiguration根据配置文件信息选择加载对应的实现类
 *               属于应用类的依赖注入,所以使用简单的枚举单例
 * @Author: YangQingyuan
 * @Date: 2019/12/1 13:39
 * @Version: 1.0
 */
public enum ExecutorType implements ExtensionBaseType<TaskExecutor> {
    THREADPOOL(new ThreadPoolTaskExecutor()); //线程池

    private TaskExecutor executor;

    ExecutorType(TaskExecutor executor){
        this.executor = executor;
    }

    @Override
    public TaskExecutor getInstance() {
        return executor;
    }
}

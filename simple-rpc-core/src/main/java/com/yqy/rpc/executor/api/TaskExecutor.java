package com.yqy.rpc.executor.api;

/**
 * @ClassName: TaskExecutor
 * @Description: 任务执行器接口:
 *               1)关闭线程池
 *               2)向线程池提交任务
 *               3)初始化线程池(参数为线程池内线程数量)
 * @Author: YangQingyuan
 * @Data: 2019/11/26
 * @Version: V1.0
 **/
public interface TaskExecutor {
    void close();

    void submit(Runnable task);

    void init(int threads);
}

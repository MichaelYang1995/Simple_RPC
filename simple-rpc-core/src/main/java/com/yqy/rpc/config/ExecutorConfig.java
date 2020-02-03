package com.yqy.rpc.config;

import com.yqy.rpc.executor.api.TaskExecutor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ExecutorConfig
 * @Description: executor配置类，主要配置以下属性：
 *               1）线程池线程数量
 *               2）executor类型，有两种：disruptor高性能队列、线程池（Executor框架）
 *               2）TaskExecutor实例
 * @Author: YangQingyuan
 * @Data: 2019/11/12
 * @Version: V1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorConfig {
    /**
     *默认线程数为处理器核心数量
     */
    public static final Integer DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    private Integer threads;

    private String type;

    private TaskExecutor taskExecutorInstance;

    public int getThreads(){
        if(threads!=null){
            return threads;
        }
        return DEFAULT_THREADS;
    }

    public void close(){
        if (taskExecutorInstance!=null){
            taskExecutorInstance.close();
        }
    }
}

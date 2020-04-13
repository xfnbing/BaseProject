package com.easyder.wrapper.core.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 刘琛慧
 *         date 2016/5/30.
 *         最新实现请使用{@link com.easyder.wrapper.core.scheduler.TaskScheduler}
 */
@Deprecated
public class TaskManager {
    private static TaskManager instance;
    private ExecutorService threadPoolExecutor;
    private ExecutorService serialTaskExecutor;

    private TaskManager() {
        threadPoolExecutor = Executors.newFixedThreadPool(3);
        serialTaskExecutor = Executors.newSingleThreadExecutor();
    }

    public static TaskManager getDefault() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                instance = new TaskManager();
            }

            return instance;
        }
        return instance;
    }

    /**
     *  最新实现请使用{@link com.easyder.wrapper.core.scheduler.TaskScheduler#execute(Runnable)}
     * @param runnable
     */
    @Deprecated
    public void post(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    /**
     *  最新实现请使用{@link com.easyder.wrapper.core.scheduler.TaskScheduler#execute(Runnable)}
     * @param task
     */
    @Deprecated
    public void enqueue(Runnable task) {
        serialTaskExecutor.execute(task);
    }

    /**
     * 关闭所有任务线程
     */
    public void destory() {
        threadPoolExecutor.shutdown();
        threadPoolExecutor = null;
        serialTaskExecutor.shutdown();
        serialTaskExecutor = null;
        instance = null;
    }
}

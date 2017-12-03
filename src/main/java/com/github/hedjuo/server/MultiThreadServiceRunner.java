package com.github.hedjuo.server;

import java.util.concurrent.*;

public class MultiThreadServiceRunner  implements ServiceRunner {
    private final ExecutorService executorService = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            10L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

    public synchronized Future<Object> executeService(RunServiceActionTask task) {
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> execute(final Callable<T> callable) {
        return executorService.submit(callable);
    }
}

package com.github.hedjuo.server;

import java.util.concurrent.*;

public class MultiThreadServiceRunner  implements ServiceRunner {
    private final ServerConfiguration configuration = Server.INJECTOR.getInstance(ServerConfiguration.class);

    /**
     * Customized CachedThreadPool with reduced keepAliveTime value.
     */
    private final ExecutorService executorService = new ThreadPoolExecutor(
            0,
            configuration.getMaxConcurrentExecutionThreads(),
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

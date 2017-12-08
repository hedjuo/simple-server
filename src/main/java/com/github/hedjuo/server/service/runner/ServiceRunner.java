package com.github.hedjuo.server.service.runner;

import com.github.hedjuo.server.task.RunServiceActionTask;

import java.util.concurrent.*;

public interface ServiceRunner {
    Future<Object> executeService(RunServiceActionTask task);
    <T> Future<T> execute(Callable<T> callable);
}

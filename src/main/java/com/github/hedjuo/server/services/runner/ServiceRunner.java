package com.github.hedjuo.server.services.runner;

import com.github.hedjuo.server.RunServiceActionTask;

import java.util.concurrent.*;

public interface ServiceRunner {
    Future<Object> executeService(RunServiceActionTask task);
    <T> Future<T> execute(Callable<T> callable);
}

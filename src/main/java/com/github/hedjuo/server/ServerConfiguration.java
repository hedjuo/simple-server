package com.github.hedjuo.server;

import com.google.inject.name.Named;

import javax.inject.Inject;

public class ServerConfiguration {
    private final Integer maxConcurrentExecutionThreads;
    private final Integer maxClientTimeout;

    @Inject
    public ServerConfiguration(
            @Named("maxConcurrentExecutionThreads") final String maxConcurrentExecutionThreads,
            @Named("maxClientTimeout") final String maxClientTimeout) {
        this.maxConcurrentExecutionThreads = Integer.valueOf(maxConcurrentExecutionThreads);
        this.maxClientTimeout = Integer.valueOf(maxClientTimeout);
    }

    public Integer getMaxConcurrentExecutionThreads() {
        return maxConcurrentExecutionThreads;
    }

    public Integer getClientTimeout() {
        return maxClientTimeout;
    }
}

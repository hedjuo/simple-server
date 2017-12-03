package com.github.hedjuo.server;

import java.util.concurrent.*;

public interface ServiceRunner {
    Future<Object> executeService(RunServiceActionTask task);
}

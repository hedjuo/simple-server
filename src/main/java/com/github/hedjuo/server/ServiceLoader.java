package com.github.hedjuo.server;

import java.util.Map;

public interface ServiceLoader {
    Map<String, Service> getServices();
}

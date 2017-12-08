package com.github.hedjuo.server.services.loader;

import com.github.hedjuo.server.services.metadata.Service;

import java.util.Map;

public interface ServiceLoader {
    Map<String, Service> getServices();
}

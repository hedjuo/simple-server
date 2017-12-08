package com.github.hedjuo.server.service.loader;

import com.github.hedjuo.server.service.metadata.Service;

import java.util.Map;

public interface ServiceLoader {
    Map<String, Service> getServices();
}

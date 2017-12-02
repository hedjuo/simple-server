package com.github.hedjuo.server;

public class Service {
    private final ServiceMetadata metadata;
    private final Object instance;

    public Service(final ServiceMetadata metadata, final Object instance) {
        this.metadata = metadata;
        this.instance = instance;
    }

    public ServiceMetadata getMetadata() {
        return metadata;
    }

    public Object getInstance() {
        return instance;
    }
}

package com.github.hedjuo.server.guice;

import com.github.hedjuo.server.ConnectionHandler;
import com.github.hedjuo.server.PropertyFileServiceLoader;
import com.github.hedjuo.server.ServiceLoader;
import com.github.hedjuo.server.ServiceManager;
import com.github.hedjuo.server.services.AuthService;
import com.github.hedjuo.server.services.DateService;
import com.github.hedjuo.server.services.UUIDService;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ServerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ServiceLoader.class).to(PropertyFileServiceLoader.class).in(Singleton.class);
        bind(ServiceManager.class).in(Singleton.class);
        bind(ConnectionHandler.class).in(Singleton.class);

        bind(AuthService.class).in(Singleton.class);
        bind(DateService.class).in(Singleton.class);

        // Internal services
        bind(UUIDService.class).in(Singleton.class);
    }
}

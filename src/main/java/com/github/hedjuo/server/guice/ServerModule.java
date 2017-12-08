package com.github.hedjuo.server.guice;

import com.github.hedjuo.server.*;
import com.github.hedjuo.server.services.AuthService;
import com.github.hedjuo.server.services.DateService;
import com.github.hedjuo.server.services.UUIDService;
import com.github.hedjuo.server.services.loader.PropertyFileServiceLoader;
import com.github.hedjuo.server.services.loader.ServiceLoader;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class ServerModule extends AbstractModule {
    private static Logger logger = LoggerFactory.getLogger(ServerModule.class);

    @Override
    protected void configure() {
        loadServerConfiguration();

        bind(ServiceLoader.class).to(PropertyFileServiceLoader.class).in(Singleton.class);

        bind(AuthService.class).in(Singleton.class);
        bind(DateService.class).in(Singleton.class);

        bind(ServiceRunner.class).to(MultiThreadServiceRunner.class).in(Singleton.class);

        // Internal services
        bind(UUIDService.class).in(Singleton.class);
    }

    private void loadServerConfiguration() {
        Properties defaults = new Properties();
        defaults.setProperty("maxConcurrentExecutionThreads", "3000");
        defaults.setProperty("maxClientTimeout", "20");

        Properties serverConfig = new Properties(defaults);

        InputStream input = null;
        try {
            final String propertyFileName = "services.properties";
            try {
                File file = new File(getClass().getClassLoader().getResource(propertyFileName).getFile());
                input = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                input = getClass().getClassLoader().getResourceAsStream(propertyFileName);
            }
            serverConfig.load(input);
        } catch (IOException e) {
            logger.info("Server configuration file not found. Load default configuration.");
        }
        Names.bindProperties(binder(), serverConfig);
    }
}

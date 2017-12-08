package com.github.hedjuo.server.guice;

import com.github.hedjuo.server.service.AuthService;
import com.github.hedjuo.server.service.DateService;
import com.github.hedjuo.server.service.UUIDService;
import com.github.hedjuo.server.service.loader.PropertyFileServiceLoader;
import com.github.hedjuo.server.service.loader.ServiceLoader;
import com.github.hedjuo.server.service.runner.MultiThreadServiceRunner;
import com.github.hedjuo.server.service.runner.ServiceRunner;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
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
            final String propertyFileName = "server.properties";
            final URL resource = getClass().getClassLoader().getResource(propertyFileName);
            if (resource == null) {
                throw new FileNotFoundException();
            }
            File file = new File(resource.getFile());
            input = new FileInputStream(file);
            serverConfig.load(input);
        } catch (FileNotFoundException e) {
            logger.info("Server configuration file not found. Load default configuration.");
        } catch (IOException e) {
            logger.info("Unable to read server configuration file. Load default configuration.");
        }
        Names.bindProperties(binder(), serverConfig);
    }
}

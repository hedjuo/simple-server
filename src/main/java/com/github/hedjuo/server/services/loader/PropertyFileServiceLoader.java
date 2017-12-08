package com.github.hedjuo.server.services.loader;

import com.github.hedjuo.server.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyFileServiceLoader implements ServiceLoader {
    private static Logger logger = LoggerFactory.getLogger(PropertyFileServiceLoader.class);
    private final Map<String, Service> services = new HashMap<>();

    public PropertyFileServiceLoader() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream input = null;
        try {
            final String propertyFileName = "services.properties";
            try {
                File file = new File(classLoader.getResource(propertyFileName).getFile());
                input = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                input = classLoader.getResourceAsStream(propertyFileName);
            }

            Properties prop = new Properties();
            prop.load(input);

            final Enumeration serviceNames = prop.propertyNames();
            while (serviceNames.hasMoreElements()) {
                final String serviceName = (String) serviceNames.nextElement();
                final String serviceClassName = (String) prop.get(serviceName);
                try {
                    Class clazz = classLoader.loadClass(serviceClassName);
                    services.put(serviceName, new Service(clazz));
                } catch (ClassNotFoundException e) {
                    logger.error("Unable to load {}", serviceClassName);
                    throw e;
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("Fatal error: configuration file not found. Exit.");
            System.exit(1);

        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    @Override
    public Map<String, Service> getServices() {
        return services;
    }

}

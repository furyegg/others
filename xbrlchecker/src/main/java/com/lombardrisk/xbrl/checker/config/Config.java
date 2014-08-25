package com.lombardrisk.xbrl.checker.config;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Cesar on 11/05/2014.
 */
public enum Config {
    INSTANCE, Config;

    private static final String PROPERTIES_FILE = "xbrlChecker.properties";

    private AbstractFileConfiguration configuration;

    private Config() {
        try {
            configuration = new PropertiesConfiguration(PROPERTIES_FILE);
            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException e) {
            LoggerFactory.getLogger(Config.class).error("Could not find properties file {}",
                    PROPERTIES_FILE);
        }
    }

    public String getString(String key) {
        check();
        return configuration.getString(key);
    }

    public int getInt(String key) {
        check();
        return configuration.getInt(key);

    }

    public long getLong(String key){
        check();
        return configuration.getLong(key);
    }

    public List<String> getList(String key){
        check();
        return configuration.getList(key);
    }

    private void check() {
        if (configuration == null) {
            throw new IllegalStateException("No properties file");
        }
    }

}

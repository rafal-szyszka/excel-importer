package com.prodactivv.excelimporter.watcher.excel;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.watcher.excel.configuration.CloudConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.DefaultConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.ExactConfigurationLoader;
import com.prodactivv.excelimporter.watcher.excel.configuration.GroupConfigurationLoader;

import java.nio.file.Path;

public class ExcelConfigurationLoader {

    public static final String DEFAULT_CONFIG_FILE = "config.json";

    private final GroupConfigurationLoader groupConfigLoader;
    private final ExactConfigurationLoader exactConfigLoader;
    private final DefaultConfigurationLoader defaultConfigurationLoader;
    private final CloudConfigurationLoader cloudConfigLoader;

    private ExcelConfigurationLoader(GroupConfigurationLoader groupConfigLoader, ExactConfigurationLoader exactConfigLoader, DefaultConfigurationLoader defaultConfigurationLoader, CloudConfigurationLoader cloudConfigLoader) {
        this.groupConfigLoader = groupConfigLoader;
        this.exactConfigLoader = exactConfigLoader;
        this.defaultConfigurationLoader = defaultConfigurationLoader;
        this.cloudConfigLoader = cloudConfigLoader;
    }

    public ExcelConfiguration loadConfiguration(Credentials credentials, Path excelFile) throws Exception {

        cloudConfigLoader.setCredentials(credentials);
        cloudConfigLoader.setCredentials(credentials);
        var cloudConfig = cloudConfigLoader.load(excelFile);
        if (cloudConfig.isPresent()) return cloudConfig.get();
        var exactConfig = exactConfigLoader.load(excelFile);
        if (exactConfig.isPresent()) return exactConfig.get();
        var groupConfig = groupConfigLoader.load(excelFile);
        if (groupConfig.isPresent()) return groupConfig.get();
        var defaultConfig = defaultConfigurationLoader.load(excelFile);
        if (defaultConfig.isPresent()) return defaultConfig.get();

        throw new Exception("Missing configuration");
    }

    public static class Builder {
        private GroupConfigurationLoader groupConfigLoader;
        private ExactConfigurationLoader exactConfigLoader;
        private DefaultConfigurationLoader defaultConfigurationLoader;
        private CloudConfigurationLoader cloudConfigLoader;

        public ExcelConfigurationLoader build() {
            return new ExcelConfigurationLoader(
                    groupConfigLoader,
                    exactConfigLoader,
                    defaultConfigurationLoader,
                    cloudConfigLoader
            );
        }

        public Builder setGroupConfigLoader(GroupConfigurationLoader groupConfigLoader) {
            this.groupConfigLoader = groupConfigLoader;
            return this;
        }

        public Builder setExactConfigLoader(ExactConfigurationLoader exactConfigLoader) {
            this.exactConfigLoader = exactConfigLoader;
            return this;
        }

        public Builder setDefaultConfigurationLoader(DefaultConfigurationLoader defaultConfigurationLoader) {
            this.defaultConfigurationLoader = defaultConfigurationLoader;
            return this;
        }

        public Builder setCloudConfigLoader(CloudConfigurationLoader cloudConfigLoader) {
            this.cloudConfigLoader = cloudConfigLoader;
            return this;
        }
    }

}

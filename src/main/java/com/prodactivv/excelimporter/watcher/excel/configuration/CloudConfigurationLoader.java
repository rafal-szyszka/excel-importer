package com.prodactivv.excelimporter.watcher.excel.configuration;

import com.prodactivv.excelimporter.Credentials;
import com.prodactivv.excelimporter.api.ApiClient;
import com.prodactivv.excelimporter.utils.ExcelFiles;
import com.prodactivv.excelimporter.watcher.excel.ExcelConfiguration;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CloudConfigurationLoader extends ConfigurationLoader<CloudConfigurationLocation> {

    private Credentials credentials;

    @Override
    public Optional<ExcelConfiguration> load(Path fileName) {
        Optional<List<ExcelConfiguration>> configurations = ApiClient.getAppConfiguration(
                credentials,
//                "08e0af6e-1967-490f-9ea5-f8e067232f39",
//                "66690566544611e80fb1b18a4e4abcecf357448615877359ec77950202779db3" // TEST
//                "339db03b-d04a-4e4a-a33d-4cd61cc79b76",
//                "e701cfb81df1fdf491cd059fb7b41a7bb4f7bc77aaccfa67e4ac758c27c39f4c" // PRE
                "201ff816-eb85-4ed4-9456-f5ee425bf086",
                "3e3a15d5ebdb335d52cefb91c8d1bd423d424075e46424fbdfbbbd1c9c787f54" // PROD
//                "9a5f6802-29a7-4221-9029-3de9e10eaf57",
//                "60b1c6b2b02cfee13fb3558e74af09e02e6700c12c816ee87502d3da4a69b534" // Local
        );

        if (configurations.isPresent()) {
            String fileNameString = ExcelFiles.getFileNameWithoutExtension(fileName);
            Optional<ExcelConfiguration> exactConfig = configurations.get().stream()
                    .filter(configuration -> configuration.name().equals(fileNameString))
                    .findAny();

            if (exactConfig.isPresent()) {
                return exactConfig;
            }

            Optional<ExcelConfiguration> groupConfig = configurations.get().stream()
                    .filter(configuration -> fileNameString.startsWith(configuration.name()))
                    .findAny();

            if (groupConfig.isPresent()) {
                return groupConfig;
            }
        }

        return Optional.empty();
    }

    @Override
    protected Optional<Path> findConfigInLocation(Path fileName) {
        return Optional.empty();
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}

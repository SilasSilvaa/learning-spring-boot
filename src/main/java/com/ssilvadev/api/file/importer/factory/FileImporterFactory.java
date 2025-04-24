package com.ssilvadev.api.file.importer.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.ssilvadev.api.exception.BadRequestException;
import com.ssilvadev.api.file.importer.contract.FileImporter;
import com.ssilvadev.api.file.importer.impl.CsvImporter;
import com.ssilvadev.api.file.importer.impl.XlsxImporter;

@Component
public class FileImporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public FileImporter getImporter(String fileName) throws Exception {
        logger.info("Chosing the class importer");

        if (fileName.endsWith(".xlsx")) {
            logger.info("Chosen XlsxImporter class ");
            return context.getBean(XlsxImporter.class);
        }

        if (fileName.endsWith(".csv")) {
            logger.info("Chosen CsvImporter class ");
            return context.getBean(CsvImporter.class);
        }

        throw new BadRequestException();
    };
}

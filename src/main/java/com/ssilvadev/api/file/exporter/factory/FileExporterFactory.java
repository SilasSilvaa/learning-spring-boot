package com.ssilvadev.api.file.exporter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.ssilvadev.api.exception.BadRequestException;
import com.ssilvadev.api.file.exporter.MediaTypes;
import com.ssilvadev.api.file.exporter.contract.FileExporter;
import com.ssilvadev.api.file.exporter.impl.CsvExporter;
import com.ssilvadev.api.file.exporter.impl.XlsxExporter;

@Component
public class FileExporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    @Autowired
    private ApplicationContext context;

    public FileExporter getExporter(String acceptHeader) throws Exception {
        logger.info("Chosing the class importer");

        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_XLSX_VALUE.getMediaType())) {
            logger.info("Chosen XlsxImporter class");
            return context.getBean(XlsxExporter.class);
        }

        if (acceptHeader.equalsIgnoreCase(MediaTypes.APPLICATION_CSV_VALUE.getMediaType())) {
            logger.info("Chosen CsvImporter class ");
            return context.getBean(CsvExporter.class);
        }

        throw new BadRequestException();
    };
}

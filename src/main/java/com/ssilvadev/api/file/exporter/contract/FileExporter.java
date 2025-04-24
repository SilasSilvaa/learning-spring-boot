package com.ssilvadev.api.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import com.ssilvadev.api.data.dto.PersonDTO;

public interface FileExporter {
    Resource exportFile(List<PersonDTO> people) throws Exception;
}

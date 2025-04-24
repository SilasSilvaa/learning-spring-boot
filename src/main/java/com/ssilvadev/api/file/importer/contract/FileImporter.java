package com.ssilvadev.api.file.importer.contract;

import java.io.InputStream;
import java.util.List;

import com.ssilvadev.api.data.dto.PersonDTO;

public interface FileImporter {
    List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}

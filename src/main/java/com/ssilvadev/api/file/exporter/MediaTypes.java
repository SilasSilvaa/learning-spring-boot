package com.ssilvadev.api.file.exporter;

public enum MediaTypes {
    APPLICATION_XLSX_VALUE("application/vnd.openxml-formats-officedocument.spreadsheetnk.sheet"),
    APPLICATION_CSV_VALUE("text/csv");

    private final String mediaType;

    private MediaTypes(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return this.mediaType;
    };
}

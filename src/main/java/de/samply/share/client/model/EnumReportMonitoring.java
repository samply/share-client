package de.samply.share.client.model;

public enum EnumReportMonitoring {

    ICINGA_STATUS_OK("0"),
    ICINGA_STATUS_WARNING("1"),
    ICINGA_STATUS_ERROR("2");

    private final String value;

    EnumReportMonitoring(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

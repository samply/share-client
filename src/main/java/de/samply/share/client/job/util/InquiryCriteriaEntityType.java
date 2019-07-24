package de.samply.share.client.job.util;

public enum InquiryCriteriaEntityType {
    PATIENT("Patient"), SPECIMEN("Specimen"), ALL("Donor + Sample"), ERROR("Error");

    private String name;

    InquiryCriteriaEntityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static InquiryCriteriaEntityType readFrom(String entityType) {
        try {
            return InquiryCriteriaEntityType.valueOf(entityType);
        } catch (IllegalArgumentException exception) {
            return InquiryCriteriaEntityType.ERROR;
        }
    }
}

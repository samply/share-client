package de.samply.share.client.job.util;

import org.apache.commons.lang3.StringUtils;

public enum InquiryCriteriaEntityType {
  PATIENT("Patient"), SPECIMEN("Specimen"), ALL("Donor + Sample"),
  ERROR("Error");

  private final String name;

  InquiryCriteriaEntityType(String name) {
    this.name = name;
  }

  /**
   * Get the InquiryCriteriaEntityType for the entityType.
   *
   * @param entityType the entityType
   * @return InquiryCriteriaEntityType
   */
  public static InquiryCriteriaEntityType readFrom(String entityType) {
    for (InquiryCriteriaEntityType type : InquiryCriteriaEntityType.values()) {
      if (StringUtils.equalsIgnoreCase(type.name, entityType)) {
        return type;
      }
    }

    // As fallback use ENUM values itself instead of its name
    try {
      return InquiryCriteriaEntityType.valueOf(entityType);
    } catch (IllegalArgumentException exception) {
      return InquiryCriteriaEntityType.ERROR;
    }
  }

  public String getName() {
    return name;
  }
}

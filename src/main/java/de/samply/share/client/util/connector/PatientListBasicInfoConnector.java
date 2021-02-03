package de.samply.share.client.util.connector;

import de.samply.share.client.model.EnumConfiguration;

/**
 * The patientlist connector for basic information.
 */
public class PatientListBasicInfoConnector extends AbstractComponentBasicInfoConnector {
  
  /**
   * Instantiates a new Patient list basic info connector.
   */
  public PatientListBasicInfoConnector() {
    super(EnumConfiguration.PATIENTLIST_URL);
  }
}

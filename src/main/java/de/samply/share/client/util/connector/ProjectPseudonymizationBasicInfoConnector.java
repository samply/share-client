package de.samply.share.client.util.connector;

import de.samply.share.client.model.EnumConfiguration;

/**
 * The projectpseudonymization connector for basic information.
 */
public class ProjectPseudonymizationBasicInfoConnector extends AbstractComponentBasicInfoConnector {

  public ProjectPseudonymizationBasicInfoConnector() {

    super(EnumConfiguration.PROJECT_PSEUDONYMISATION_URL);
  }
}

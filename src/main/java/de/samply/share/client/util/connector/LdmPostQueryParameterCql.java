package de.samply.share.client.util.connector;

public class LdmPostQueryParameterCql extends AbstractLdmPostQueryParameter {

  private final String entityType;

  /**
   * Parameter for posting a query to local datamanagement.
   *
   * @param statisticsOnly if true, set a parameter to only request a count of the results, not the
   *                       whole result lists
   * @param entityType     type of entity to be counted
   */
  public LdmPostQueryParameterCql(boolean statisticsOnly, String entityType) {
    super(statisticsOnly);

    this.entityType = entityType;
  }

  String getEntityType() {
    return entityType;
  }
}

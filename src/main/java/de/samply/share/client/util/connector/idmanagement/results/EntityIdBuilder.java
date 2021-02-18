package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.model.ccp.Entity;
import de.samply.share.model.ccp.Patient;
import java.util.HashSet;
import java.util.Set;

public class EntityIdBuilder extends QueryResultParser {

  private final Set<EntityId> entityIds = new HashSet<>();

  /**
   * Todo David.
   * @param patient Todo David
   * @return Todo David
   * @throws QueryResultParserException QueryResultParserException
   */
  public Set<EntityId> createEntityIdSet(Patient patient) throws QueryResultParserException {

    parse(patient);
    return entityIds;

  }


  @Override
  protected void parse(Entity entity, Entity entityParent) throws QueryResultParserException {

    EntityId entityId = new EntityId(entity);
    entityIds.add(entityId);

  }

}

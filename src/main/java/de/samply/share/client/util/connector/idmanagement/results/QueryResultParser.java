package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.model.ccp.Entity;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import java.util.ArrayList;
import java.util.List;

public abstract class QueryResultParser {

  protected abstract void parse(Entity entity, Entity entityParent)
      throws QueryResultParserException;

  protected void parse(Entity entity) throws QueryResultParserException {
    parseEntityAndChildren(entity, null);
  }

  protected void parseQueryResult(QueryResult queryResult) throws QueryResultParserException {

    for (Patient patient : queryResult.getPatient()) {
      parse(patient);
    }

  }

  private void parseEntityAndChildren(Entity entity, Entity entityParent)
      throws QueryResultParserException {

    parse(entity, entityParent);
    for (Entity entityChild : getChildren(entity)) {
      parseEntityAndChildren(entityChild, entity);
    }

  }

  private List<Entity> getChildren(Entity entity) {

    List<Entity> entityList = new ArrayList<>();

    if (entity instanceof Patient) {

      Patient patient = (Patient) entity;

      entityList.addAll(patient.getCase());
      entityList.addAll(patient.getSample());

    }

    entityList.addAll(entity.getContainer());

    return entityList;
  }

}

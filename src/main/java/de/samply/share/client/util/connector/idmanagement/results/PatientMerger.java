package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Case;
import de.samply.share.model.ccp.Container;
import de.samply.share.model.ccp.Entity;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.Sample;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PatientMerger extends QueryResultParser {

  private List<EntityId> entityIds;

  /**
   * Todo David.
   * @param currentPatient Todo David
   * @param newPatient Todo David
   * @throws QueryResultParserException QueryResultParserException
   */
  public void merge(Patient currentPatient, Patient newPatient) throws QueryResultParserException {

    generateEntityIds(currentPatient);
    parse(newPatient);

  }

  private void generateEntityIds(Patient currentPatient) throws QueryResultParserException {

    EntityIdBuilder entityIdBuilder = new EntityIdBuilder();
    Set<EntityId> entityIdSet = entityIdBuilder.createEntityIdSet(currentPatient);
    entityIds = new ArrayList<>(entityIdSet);

  }

  @Override
  protected void parse(Entity entity, Entity entityParent) throws QueryResultParserException {

    EntityId entityId = new EntityId(entity);

    if (entityIds.contains(entityId)) {

      Entity currentEntity = getCurrentEntity(entityId);
      mergeEntitiesAttributes(currentEntity, entity);

    } else {

      EntityId entityParentId = new EntityId(entityParent);
      Entity currentEntityParent = getCurrentEntity(entityParentId);
      addEntityToCurrentParentEntity(entity, currentEntityParent);

    }

  }

  private Entity getCurrentEntity(EntityId entityId) {

    int index = entityIds.indexOf(entityId);
    EntityId currentEntityId = entityIds.get(index);
    return currentEntityId.getEntity();

  }

  private void mergeEntitiesAttributes(Entity currentEntity, Entity newEntity) {

    List<Attribute> currentEntityAttributes = currentEntity.getAttribute();

    for (Attribute newEntityAttribute : newEntity.getAttribute()) {

      if (!currentEntityAttributes.contains(newEntityAttribute)) {
        currentEntityAttributes.add(newEntityAttribute);
      }

    }

  }

  private void addEntityToCurrentParentEntity(Entity newEntity, Entity currentParentEntity) {

    boolean isMerged = false;

    if (currentParentEntity instanceof Patient) {

      Patient patient = (Patient) currentParentEntity;

      if (newEntity instanceof Case) {

        List<Case> caseList = patient.getCase();
        caseList.add((Case) newEntity);
        isMerged = true;

      } else if (newEntity instanceof Sample) {

        List<Sample> sampleList = patient.getSample();
        sampleList.add((Sample) newEntity);
        isMerged = true;

      }

    }

    if (!isMerged && newEntity instanceof Container) {

      List<Container> container = currentParentEntity.getContainer();
      container.add((Container) newEntity);
      isMerged = true;

    }

    if (isMerged) {

      EntityId entityId = new EntityId(newEntity);
      entityIds.add(entityId);

    }

  }


}

package de.samply.share.client.util.connector.idmanagement.results;

import de.samply.share.model.ccp.Container;
import de.samply.share.model.ccp.Entity;
import de.samply.share.model.ccp.Patient;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

public class EntityId {

  private static final String DEFAULT_DESIGNATION = "Entity";
  private final String designation;
  private final String id;
  private final Entity entity;

  /**
   * Todo David.
   * @param entity Todo David
   */
  public EntityId(Entity entity) {

    this.entity = entity;
    this.designation = getDesignation(entity);
    this.id = entity.getId();

  }

  private String getDesignation(Entity entity) {
    String designation = DEFAULT_DESIGNATION;
    if (entity instanceof Container) {
      designation = ((Container) entity).getDesignation();
    } else if (entity instanceof XmlRootElement) {
      designation = getXmlRootElementName(entity);
    }
    return designation;
  }

  public String getDesignation() {
    return designation;
  }

  private String getXmlRootElementName(Entity entity) {
    return ((XmlRootElement) entity).name();
  }

  public String getId() {
    return id;
  }

  public Entity getEntity() {
    return entity;
  }

  public boolean isPatient() {
    return entity instanceof Patient;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntityId that = (EntityId) o;
    return designation.equals(that.designation)
        && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(designation, id);
  }


}

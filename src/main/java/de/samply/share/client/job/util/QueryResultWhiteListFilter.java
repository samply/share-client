package de.samply.share.client.job.util;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Entity;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryResultWhiteListFilter {

  private final Set<String> urnWhiteList = getCentralSearchWhiteList();


  /**
   * Todo.
   *
   * @param queryResult Todo.
   * @return Todo.
   */
  public QueryResult filter(QueryResult queryResult) {

    List<Patient> patientList = queryResult.getPatient();

    for (Patient patient : patientList) {
      filterEntity(patient);
    }

    return queryResult;

  }

  private void filterEntity(Entity entity) {

    filterAttributes(entity);
    for (Entity entityChild : getChildren(entity)) {
      filterEntity(entityChild);
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

  private void filterAttributes(Entity entity) {

    List<Attribute> attributeList = entity.getAttribute();
    List<Attribute> attributeListAux = new ArrayList<>();
    attributeListAux.addAll(attributeList);

    for (Attribute attribute : attributeList) {

      if (!isInCentralSearchWhiteList(attribute)) {
        attributeListAux.remove(attribute);
      }

    }

    if (attributeList.size() != attributeListAux.size()) {

      attributeList.clear();
      attributeList.addAll(attributeListAux);

    }


  }

  private boolean isInCentralSearchWhiteList(Attribute attribute) {
    return attribute != null && isInCentralSearchWhiteList(attribute.getMdrKey());
  }

  private boolean isInCentralSearchWhiteList(String urn) {

    MdrIdDatatype mdrId = new MdrIdDatatype(urn);
    String mdrIdS = mdrId.getMajor();

    return urnWhiteList.contains(mdrIdS);

  }

  private Set<String> getCentralSearchWhiteList() {

    List<String> urnWhitelist = ConfigurationUtil
        .getConfigurationElementValueList(EnumConfiguration.CENTRAL_MDS_DATABASE_WHITELIST);
    Set<String> urnWhiteListMajor = new HashSet<>();

    for (String urn : urnWhitelist) {

      MdrIdDatatype mdrId = new MdrIdDatatype(urn);
      urnWhiteListMajor.add(mdrId.getMajor());

    }

    return urnWhiteListMajor;

  }

}

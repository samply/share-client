package de.samply.share.client.util;

import static de.samply.share.client.model.EnumConfiguration.MDR_GRP_MDSB;
import static de.samply.share.client.model.EnumConfiguration.MDR_GRP_MDSK;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Meaning;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Result;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.common.ViewFields;
import de.samply.web.mdrfaces.MdrContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.xml.bind.JAXBElement;

/**
 * Handle MDR related tasks.
 */
public class MdrUtils {

  private static final String VALIDATION_DATATYPE_ENUMERATED = "enumerated";

  /**
   * Gets all dataelements from a group and its subgroups.
   *
   * @param theList  the list to extend with new dataelements
   * @param groupKey the mdr id of the dataelementgroup to add
   * @return the elements from the group and its subgroups
   */
  private static ArrayList<MdrIdDatatype> getElementsFromGroupAndSubgroups(
      ArrayList<MdrIdDatatype> theList, String groupKey)
      throws MdrConnectionException, ExecutionException {
    List<Result> resultL = ApplicationBean.getMdrClient().getMembers(groupKey, "de");
    for (Result r : resultL) {
      if (r.getType().equalsIgnoreCase("dataelementgroup")) {
        theList = getElementsFromGroupAndSubgroups(theList, r.getId());
      } else {
        theList.add(new MdrIdDatatype(r.getId()));
      }
    }

    return theList;
  }
  
  /**
   * Get a ViewFields object with all elements from mds-b and mds-k.
   *
   * @return a ViewFields object with all elements from mds-b and mds-k
   * @throws MdrConnectionException the mdr connection exception
   * @throws ExecutionException     the execution exception
   */
  public static ViewFields getViewFields() throws MdrConnectionException, ExecutionException {
    return getViewFields(true);
  }
  
  /**
   * Get a ViewFields object.
   *
   * @param completeMds should all elements of the mds-b and mds-k be included?
   * @return a ViewFields object with either just one (dummy) entry or the complete set of
   *     dataelements from mds-b/k
   * @throws MdrConnectionException the mdr connection exception
   * @throws ExecutionException     the execution exception
   */
  public static ViewFields getViewFields(boolean completeMds)
      throws MdrConnectionException, ExecutionException {
    ArrayList<MdrIdDatatype> mdskAttributes = new ArrayList<>();
    ArrayList<MdrIdDatatype> mdsbAttributes = new ArrayList<>();
    ViewFields viewFields = new ViewFields();

    if (completeMds) {
      mdskAttributes = getElementsFromGroupAndSubgroups(mdskAttributes,
          ConfigurationUtil.getConfigurationElementValue(MDR_GRP_MDSK));
      mdsbAttributes = getElementsFromGroupAndSubgroups(mdsbAttributes,
          ConfigurationUtil.getConfigurationElementValue(MDR_GRP_MDSB));
      for (MdrIdDatatype attribute : mdskAttributes) {
        viewFields.getMdrKey().add(attribute.getLatestCentraxx());
      }
      for (MdrIdDatatype attribute : mdsbAttributes) {
        viewFields.getMdrKey().add(attribute.getLatestCentraxx());
      }
    } else {
      // Add a dummy viewfield because empty viewfields are not allowed
      viewFields.getMdrKey().add("urn:dktk:dataelement:1:*");
    }
    return viewFields;
  }
  
  /**
   * Gets the designation for a dataelement in the mdr.
   *
   * @param dataElement  the data element id
   * @param languageCode the language code
   * @return the designation
   */
  public static String getDesignation(String dataElement, String languageCode) {

    MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

    try {
      return mdrClient.getDataElementDefinition(dataElement, languageCode).getDesignations().get(0)
          .getDesignation();
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      e.printStackTrace();
      return ("??" + dataElement + "??");
    }

  }
  
  /**
   * Gets the designation of a certain value of a dataelement.
   *
   * @param dataElement  the data element
   * @param value        the value
   * @param languageCode the language code
   * @return the designation
   */
  public static String getValueDesignation(String dataElement, Object value, String languageCode) {
    String designation;

    if (value.getClass().equals(String.class)) {
      designation = (String) value;
    } else if (value.getClass().equals(JAXBElement.class)) {
      @SuppressWarnings("unchecked")
      JAXBElement<String> jaxbElement = ((JAXBElement<String>) value);
      designation = jaxbElement.getValue();
    } else {
      designation = "error!";
    }

    MdrClient mdrClient = MdrContext.getMdrContext().getMdrClient();

    try {
      Validations validations = mdrClient.getDataElementValidations(dataElement, languageCode);
      String dataType = validations.getDatatype();
      if (dataType.equalsIgnoreCase(VALIDATION_DATATYPE_ENUMERATED)) {
        List<PermissibleValue> permissibleValues = validations.getPermissibleValues();
        for (PermissibleValue pv : permissibleValues) {
          List<Meaning> meanings = pv.getMeanings();
          if (pv.getValue().equals(designation)) {
            for (Meaning m : meanings) {
              if (m.getLanguage().equalsIgnoreCase(languageCode)) {
                return m.getDesignation();
              }
            }
          }
        }
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      e.printStackTrace();
    }
    return designation;
  }
  
  /**
   * Gets a Map of permitted values and their designation of a dataelement.
   *
   * @param mdrKey           the dataelements mdr id
   * @param languageCode     the language code
   * @param includeIdentical if set to false, values are only added if value and designation differ
   * @return the list of values and designations or null if it's not an enumerated value domain
   */
  public static Map<String, String> getValuesAndDesignations(String mdrKey, String languageCode,
      boolean includeIdentical) {
    MdrClient mdrClient = ApplicationBean.getMdrClient();

    try {
      Validations validations = mdrClient.getDataElementValidations(mdrKey, languageCode);
      String dataType = validations.getDatatype();
      if (dataType.equalsIgnoreCase(VALIDATION_DATATYPE_ENUMERATED)) {
        Map<String, String> valueAndDesignationMap = new HashMap<>();
        List<PermissibleValue> permissibleValues = validations.getPermissibleValues();
        for (PermissibleValue pv : permissibleValues) {
          List<Meaning> meanings = pv.getMeanings();
          for (Meaning m : meanings) {
            if (m.getLanguage().equalsIgnoreCase(languageCode)) {
              // Add it to the map if either the value differs from the designation or the include
              // switch is true
              if (includeIdentical || !pv.getValue().equals(m.getDesignation())) {
                valueAndDesignationMap.put(pv.getValue(), m.getDesignation());
              }
            }
          }
        }
        return valueAndDesignationMap;
      } else {
        return null;
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      return null;
    }
  }
}

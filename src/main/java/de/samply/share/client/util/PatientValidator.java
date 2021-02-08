package de.samply.share.client.util;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.dktk.converter.PatientAttributeOperator;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.xml.bind.JAXBElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PatientValidator {

  private static final Logger logger = LogManager.getLogger(PatientValidator.class);
  private static final String languageCode = "de";

  private static final String DATATYPE_ENUMERATED = "enumerated";
  private static final String DATATYPE_STRING = "STRING";
  private static final String DATATYPE_BOOLEAN = "BOOLEAN";

  private static final String VALIDATIONTYPE_REGEX = "REGEX";

  private static final String PREFIX_CASE_INSENSITIVE = "(?i)";


  private final MdrClient mdrClient;


  public PatientValidator(MdrClient mdrClient) {
    this.mdrClient = mdrClient;
  }

  /**
   * Fix or remove wrong attributes of patient.
   *
   * @param patient Patient.
   * @return Patient updated.
   */
  public Patient fixOrRemoveWrongAttributes(Patient patient) {

    patient.setDktkId(null);
    patient.setCentraxxId(null);

    PatientAttributeFixer patientAttributeFixer = new PatientAttributeFixer(patient);
    patient = patientAttributeFixer.fixAttributes();
    String errors = patientAttributeFixer.getErrors();
    if (errors != null && errors.length() > 0) {
      logger.warn(errors);
    }

    return patient;

  }

  /**
   * Try to fix invalid attributes.
   *
   * @param attribute     the attribute to fix
   * @param caseSensitive should the check be case sensitive?
   * @return the fixed attribute or null if it could not be fixed
   */
  private Attribute fixOrNull(Attribute attribute, boolean caseSensitive) {
    Attribute fixedAttribute = null;
    ObjectFactory objectFactory = new ObjectFactory();
    Validations validations;

    try {
      validations = mdrClient.getDataElementValidations(attribute.getMdrKey(), languageCode);
    } catch (ExecutionException | MdrInvalidResponseException | MdrConnectionException e) {
      logger.error("Could not get validations from MDR.", e);
      return null;
    }

    if (validations.getDatatype().equalsIgnoreCase(DATATYPE_ENUMERATED)) {
      try {
        for (PermissibleValue pv : validations.getPermissibleValues()) {
          if (caseSensitive) {
            if (pv.getValue().equals(attribute.getValue().getValue())) {
              fixedAttribute = attribute;
              break;
            }
          } else {
            if (pv.getValue().equalsIgnoreCase((attribute.getValue().getValue()))) {
              fixedAttribute = attribute;
              JAXBElement<String> newValue = objectFactory.createValue(pv.getValue());
              fixedAttribute.setValue(newValue);
              break;
            }
          }
        }
      } catch (NullPointerException npe) {
        logger.warn(
            "Null pointer exception caught when trying to validate: " + attribute.getMdrKey()
                + " -> " + attribute.getValue().getValue());
      }
    } else if (validations.getDatatype().equalsIgnoreCase(DATATYPE_STRING)) {
      if (validations.getValidationType().equalsIgnoreCase(VALIDATIONTYPE_REGEX)) {
        String regexPattern;
        if (caseSensitive) {
          regexPattern = validations.getValidationData();
        } else {
          regexPattern = PREFIX_CASE_INSENSITIVE + validations.getValidationData();
        }

        if (attribute.getValue().getValue().matches(regexPattern)) {
          fixedAttribute = attribute;
        }
      } else {
        // TODO: for now return true for other strings. check max size later
        fixedAttribute = attribute;
      }

    } else if (validations.getDatatype().equalsIgnoreCase(DATATYPE_BOOLEAN)) {
      try {
        String validationData = validations.getValidationData();
        String[] validationsArray = validationData.substring(1, validationData.length() - 1)
            .split("\\|");
        List<String> allowedValues = Arrays.asList(validationsArray);
        if (allowedValues.contains(attribute.getValue().getValue())) {
          fixedAttribute = attribute;
        }
      } catch (NullPointerException npe) {
        logger.debug(
            "NPE caught while trying to validate boolean...returning value as is: " + attribute
                .getValue().getValue());
      }
    } else {
      fixedAttribute = attribute;
      // TODO: check other datatypes
    }
    return fixedAttribute;
  }

  // TODO: Expand this to other than enumerated value domain

  private class PatientAttributeFixer extends PatientAttributeOperator {

    private final StringBuilder stringBuilder = new StringBuilder();
    private final Patient patient;

    public PatientAttributeFixer(Patient patient) {
      this.patient = patient;
    }

    public Patient fixAttributes() {
      return this.operateAttributes(patient);
    }

    @Override
    protected Attribute operateAttribute(Attribute attribute) {
      return fixOrNull(attribute, false);
    }

    private String getErrors() {
      return stringBuilder.toString();
    }

  }

}

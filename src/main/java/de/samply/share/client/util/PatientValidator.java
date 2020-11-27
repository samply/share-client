/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Case;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.Sample;

/**
 * Check if the attributes in a patient dataset are valid according to the mdr entry
 */
public class PatientValidator {
    private static final Logger logger = LogManager.getLogger(PatientValidator.class);
    private static final String languageCode = "de";
    
    private static final String DATATYPE_ENUMERATED = "enumerated";
    private static final String DATATYPE_STRING = "STRING";
    private static final String DATATYPE_BOOLEAN = "BOOLEAN";
    
    private static final String VALIDATIONTYPE_REGEX = "REGEX";
    
    private static final String PREFIX_CASE_INSENSITIVE = "(?i)";
    
    private MdrClient mdrClient;

    /**
     * Creates an instance of the patient validator with the given mdr client.
     *
     * @param mdrClient an mdr client
     */
    public PatientValidator(MdrClient mdrClient) {
        this.mdrClient = mdrClient;
    }

    /**
     * Check all attributes of a given patient. Try to fix them wherever possible, remove them otherwise.
     *
     * @param patient the patient to check
     * @return the patient that only contains valid attributes
     */
    public Patient fixOrRemoveWrongAttributes(Patient patient) {
        Patient newPatient = new Patient();
        StringBuilder stringBuilder = new StringBuilder();
        
        newPatient.setCentraxxId(patient.getCentraxxId());
        newPatient.setDktkId(patient.getDktkId());
        newPatient.setId(patient.getId());
        
        for (Case _case : patient.getCase()) {
            Case newCase = new Case();
            newCase.setId(_case.getId());
            
            for (Attribute attribute : _case.getAttribute()) {
                Attribute fixedAttribute = fixOrNull(attribute, false);
                if (fixedAttribute == null) {
                    stringBuilder.append("\nInvalid in case ").append(_case.getId()).append(": ").append(attribute.getMdrKey()).append(" -> ").append(attribute.getValue().getValue());
                } else {
                    newCase.getAttribute().add(fixedAttribute);
                }
            }
            newPatient.getCase().add(newCase);
        }

        for (Sample sample : patient.getSample()) {
            Sample newSample = new Sample();
            newSample.setId(sample.getId());
            
            for (Attribute attribute : sample.getAttribute()) {
                Attribute fixedAttribute = fixOrNull(attribute, false);
                if (fixedAttribute == null) {
                    stringBuilder.append("\nInvalid in sample ").append(sample.getId()).append(": ").append(attribute.getMdrKey()).append(" -> ").append(attribute.getValue().getValue());
                } else {
                    newSample.getAttribute().add(fixedAttribute);
                }
            }
            newPatient.getSample().add(newSample);
        }
        return newPatient;
    }

    /**
     * Check if the patient is free of any invalid attributes
     *
     * @param patient the patient to check
     * @return true if no invalid attributes are present
     */
    public boolean isPatientValid(Patient patient) {
        return getMdrKeysForErroneousEntries(patient).isEmpty();
    }

    /**
     * Get a list of all keys that belonged to an attribute with an invalid value
     *
     * @param patient the patient to check
     * @return a list of all keys that belonged to an attribute with an invalid value
     */
    public List<String> getMdrKeysForErroneousEntries(Patient patient) {
        List<String> mdrKeys = new ArrayList<>();
        
        StringBuilder stringBuilder = new StringBuilder(patient.getId() + ": ");

        for (Case _case : patient.getCase()) {
            for (Attribute attribute : _case.getAttribute()) {
                if (!isValid(attribute)) {
                    mdrKeys.add(attribute.getMdrKey());
                    stringBuilder.append("\nInvalid in case ").append(_case.getId()).append(": ").append(attribute.getMdrKey()).append(" -> ").append(attribute.getValue().getValue());
                }
            }
        }

        for (Sample sample : patient.getSample()) {
            for (Attribute attribute : sample.getAttribute()) {
                if (!isValid(attribute)) {
                    mdrKeys.add(attribute.getMdrKey());
                    stringBuilder.append("\nInvalid in sample ").append(sample.getId()).append(": ").append(attribute.getMdrKey()).append(" -> ").append(attribute.getValue().getValue());
                }
            }
        }

        return mdrKeys;
    }

    
    // TODO: Expand this to other than enumerated value domain
    /**
     * Try to fix invalid attributes.
     *
     * @param attribute the attribute to fix
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
                logger.warn("Null pointer exception caught when trying to validate: " + attribute.getMdrKey() + " -> " + attribute.getValue().getValue());
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
                String[] validationsArray = validationData.substring(1, validationData.length()-1).split("\\|");
                List<String> allowedValues = Arrays.asList(validationsArray);
                if (allowedValues.contains(attribute.getValue().getValue())) {
                    fixedAttribute = attribute;
                }
            } catch (NullPointerException npe) {
                logger.debug("NPE caught while trying to validate boolean...returning value as is: " + attribute.getValue().getValue());
            }
        } else {
            fixedAttribute = attribute;
            // TODO: check other datatypes
        }
        return fixedAttribute;
    }

    // TODO: Expand this to other than enumerated value domain
    /**
     * Check if an attribute is valid
     *
     * @param attribute the attribute to check
     * @return true if it is valid
     */
    private boolean isValid(Attribute attribute) {
        boolean isValid = false;
        Validations validations;

        try {
            validations = mdrClient.getDataElementValidations(attribute.getMdrKey(), languageCode);
        } catch (ExecutionException | MdrInvalidResponseException | MdrConnectionException e) {
            logger.error("Could not get validations from MDR.", e);
            return false;
        }

        if (validations.getDatatype().equalsIgnoreCase(DATATYPE_ENUMERATED)) {
            try {
                for (PermissibleValue pv : validations.getPermissibleValues()) {
                    if (pv.getValue().equals(attribute.getValue().getValue())) {
                        isValid = true;
                        break;
                    }
                }
            } catch (NullPointerException npe) {
                logger.warn("Null pointer exception caught when trying to validate: " + attribute.getMdrKey() + " -> " + attribute.getValue().getValue());
            }
        } else if (validations.getDatatype().equalsIgnoreCase(DATATYPE_STRING)) {
            if (validations.getValidationType().equalsIgnoreCase(VALIDATIONTYPE_REGEX)) {
                // TODO: should this stay case sensitive?
                // String regexPattern = PREFIX_CASE_INSENSITIVE + validations.getValidationData();
                String regexPattern = validations.getValidationData();
                isValid = attribute.getValue().getValue().matches(regexPattern);
            } else {
                // TODO: for now return true for other strings. check max size later
                return true;
            }
            
        } else if (validations.getDatatype().equalsIgnoreCase(DATATYPE_BOOLEAN)) {
            try {
                String validationData = validations.getValidationData();
                String[] validationsArray = validationData.substring(1, validationData.length()-1).split("\\|");
                List<String> allowedValues = Arrays.asList(validationsArray);
                isValid = allowedValues.contains(attribute.getValue().getValue());
            } catch (NullPointerException npe) {
                logger.debug("NPE caught while trying to validate boolean...returning true for: " + attribute.getValue().getValue());
                isValid = true;
            }
        } else {
            isValid = true;
            // TODO: check other datatypes
        }
        return isValid;
    }
}

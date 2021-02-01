package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.share.client.util.connector.idmanagement.utils.IdManagementUtils;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.common.In;
import de.samply.share.model.common.MultivalueAttribute;
import de.samply.share.model.common.ObjectFactory;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBElement;

public abstract class PatientInConverter implements SerializableConverter {

  protected ObjectFactory objectFactory = new ObjectFactory();

  protected In createInTerm(Set<String> localPatientIds) {

    In inTerm = objectFactory.createIn();

    addLocalPatientIds(inTerm, localPatientIds);

    return inTerm;

  }

  private void addLocalPatientIds(In inTerm, Set<String> localPatientIds) {

    MultivalueAttribute multivalueAttribute = new MultivalueAttribute();

    for (String localPatientId : localPatientIds) {
      addLocalPatientId(multivalueAttribute, localPatientId);
    }

    MdrIdDatatype localPatientIdMdrId = IdManagementUtils.getLocalPatientIdMdrId();
    multivalueAttribute.setMdrKey(localPatientIdMdrId.getLatestCentraxx());

    inTerm.setMultivalueAttribute(multivalueAttribute);

  }

  private void addLocalPatientId(MultivalueAttribute multivalueAttribute, String localPatientId) {

    List<JAXBElement<String>> jaxbLocalPatientIds = multivalueAttribute.getValue();
    JAXBElement<String> jaxbLocalPatientId = objectFactory.createValue(localPatientId);

    jaxbLocalPatientIds.add(jaxbLocalPatientId);

  }

}

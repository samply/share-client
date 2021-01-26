package de.samply.share.client.quality.report;

import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxxExtension;
import de.samply.share.client.util.connector.centraxx.CxxMappingElement;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MdrMappedElements {

  private final Map<MdrIdDatatype, CxxMappingElement> mdrIdCxxMappingElementMap = new HashMap<>();

  /**
   * Todo.
   *
   * @param ldmConnector Todo.
   */
  public MdrMappedElements(LdmConnector ldmConnector) {

    addMappedElements(ldmConnector);

  }

  private void addMappedElements(LdmConnector<?, ?, ?> ldmConnector) {

    if (ldmConnector instanceof LdmConnectorCentraxxExtension) {

      List<CxxMappingElement> mapping = ((LdmConnectorCentraxxExtension) ldmConnector).getMapping();

      for (CxxMappingElement mappingElement : mapping) {

        MdrIdDatatype mdrId = mappingElement.getMdrId();
        MdrIdDatatype basicMdrId = getBasicMdrIdDataType(mdrId);

        mdrIdCxxMappingElementMap.put(basicMdrId, mappingElement);

      }

    }

  }

  private MdrIdDatatype getBasicMdrIdDataType(MdrIdDatatype mdrIdDatatype) {
    return new MdrIdDatatype(mdrIdDatatype.getMajor());
  }

  /**
   * Todo.
   *
   * @param mdrId Todo.
   * @return Todo.
   */
  public boolean isMapped(MdrIdDatatype mdrId) {

    boolean isMapped = true;

    if (mdrIdCxxMappingElementMap.size() > 0) {

      MdrIdDatatype basicMdrId = getBasicMdrIdDataType(mdrId);
      isMapped = mdrIdCxxMappingElementMap.containsKey(basicMdrId);

    }
    return isMapped;
  }

  public boolean isMapped(String mdrId) {
    return isMapped(new MdrIdDatatype(mdrId));
  }

  public CxxMappingElement getCxxMappingElement(MdrIdDatatype mdrId) {
    MdrIdDatatype basicMdrId = getBasicMdrIdDataType(mdrId);
    return mdrIdCxxMappingElementMap.get(basicMdrId);
  }

}

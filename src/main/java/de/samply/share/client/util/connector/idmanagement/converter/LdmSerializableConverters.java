package de.samply.share.client.util.connector.idmanagement.converter;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.Map;

public class LdmSerializableConverters {

  private final Map<MdrIdDatatype, SerializableConverter> mdrIdSerializableConverterMap =
      new HashMap<>();

  /**
   * Todo David.
   * @param mdrId Todo David
   * @param serializableConverter Todo David
   */
  public void addSerializableConverter(MdrIdDatatype mdrId,
      SerializableConverter serializableConverter) {

    MdrIdDatatype majorMdrId = convertToMajor(mdrId);
    mdrIdSerializableConverterMap.put(majorMdrId, serializableConverter);

  }

  /**
   * Todo David.
   * @param mdrId Todo David
   * @return Todo David
   */
  public SerializableConverter getSerializableConverter(MdrIdDatatype mdrId) {

    MdrIdDatatype majorMdrId = convertToMajor(mdrId);
    return mdrIdSerializableConverterMap.get(majorMdrId);

  }

  private MdrIdDatatype convertToMajor(MdrIdDatatype mdrId) {
    return new MdrIdDatatype(mdrId.getMajor());
  }


}

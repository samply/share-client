package de.samply.share.client.util.connector.idmanagement.converter;

import java.io.Serializable;

public interface SerializableConverter {

  Serializable convert(Serializable serializable) throws SerializableConverterException;

}

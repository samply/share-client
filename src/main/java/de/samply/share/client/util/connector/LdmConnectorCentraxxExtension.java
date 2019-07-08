package de.samply.share.client.util.connector;

import de.samply.share.client.util.connector.centraxx.CxxMappingElement;

import java.util.ArrayList;
import java.util.List;

public interface LdmConnectorCentraxxExtension {

    default String getMappingVersion() {
        return "undefined";
    }

    default String getMappingDate() {
        return "undefined";
    }

    default List<CxxMappingElement> getMapping() {
        return new ArrayList<>();
    }
}

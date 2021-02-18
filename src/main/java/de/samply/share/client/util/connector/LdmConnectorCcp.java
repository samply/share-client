package de.samply.share.client.util.connector;

import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.Query;

public interface LdmConnectorCcp extends
    LdmConnector<Query, LdmPostQueryParameterView, QueryResult>, LdmConnectorCentraxxExtension {

}

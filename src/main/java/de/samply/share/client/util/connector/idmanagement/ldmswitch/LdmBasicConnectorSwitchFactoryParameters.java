package de.samply.share.client.util.connector.idmanagement.ldmswitch;

import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverter;
import de.samply.share.client.util.connector.idmanagement.results.LdmResultBuilder;

public class LdmBasicConnectorSwitchFactoryParameters {


  private LdmQueryConverter ldmQueryConverter;
  private LdmQueryLocationMapper ldmQueryLocationMapper;
  private LdmConnectorCentraxx ldmConnectorCentraxx;
  private LdmResultBuilder ldmResultBuilder;

  public LdmQueryConverter getLdmQueryConverter() {
    return ldmQueryConverter;
  }

  public void setLdmQueryConverter(LdmQueryConverter ldmQueryConverter) {
    this.ldmQueryConverter = ldmQueryConverter;
  }

  public LdmQueryLocationMapper getLdmQueryLocationMapper() {
    return ldmQueryLocationMapper;
  }

  public void setLdmQueryLocationMapper(LdmQueryLocationMapper ldmQueryLocationMapper) {
    this.ldmQueryLocationMapper = ldmQueryLocationMapper;
  }

  public LdmConnectorCentraxx getLdmConnectorCentraxx() {
    return ldmConnectorCentraxx;
  }

  public void setLdmConnectorCentraxx(LdmConnectorCentraxx ldmConnectorCentraxx) {
    this.ldmConnectorCentraxx = ldmConnectorCentraxx;
  }

  public LdmResultBuilder getLdmResultBuilder() {
    return ldmResultBuilder;
  }

  public void setLdmResultBuilder(LdmResultBuilder ldmResultBuilder) {
    this.ldmResultBuilder = ldmResultBuilder;
  }

}

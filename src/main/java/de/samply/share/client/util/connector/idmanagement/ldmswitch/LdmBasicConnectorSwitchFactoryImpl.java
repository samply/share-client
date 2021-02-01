package de.samply.share.client.util.connector.idmanagement.ldmswitch;

import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.idmanagement.query.LdmId;
import de.samply.share.client.util.connector.idmanagement.query.LdmQueryConverter;
import de.samply.share.client.util.connector.idmanagement.results.LdmResultBuilder;

public class LdmBasicConnectorSwitchFactoryImpl implements LdmBasicConnectorSwitchFactory {

  private final LdmQueryConverter ldmQueryConverter;
  private final LdmQueryLocationMapper ldmQueryLocationMapper;
  private final LdmConnectorCentraxx ldmConnectorCentraxx;
  private final LdmResultBuilder ldmResultBuilder;


  /**
   * Todo David.
   * @param ldmBasicConnectorSwitchFactoryParameters Todo David
   */
  public LdmBasicConnectorSwitchFactoryImpl(
      LdmBasicConnectorSwitchFactoryParameters ldmBasicConnectorSwitchFactoryParameters) {

    ldmConnectorCentraxx = ldmBasicConnectorSwitchFactoryParameters.getLdmConnectorCentraxx();
    ldmQueryConverter = ldmBasicConnectorSwitchFactoryParameters.getLdmQueryConverter();
    ldmQueryLocationMapper = ldmBasicConnectorSwitchFactoryParameters.getLdmQueryLocationMapper();
    ldmResultBuilder = ldmBasicConnectorSwitchFactoryParameters.getLdmResultBuilder();

  }

  @Override
  public LdmBasicConnectorSwitch createLdmBasicConnectorSwitch() {

    LdmConnectorSwitchParameters ldmConnectorSwitchParameters = getLdmConnectorSwitchParameters();
    LdmBasicConnectorSwitch ldmBasicConnectorSwitch = new LdmBasicConnectorSwitch(
        ldmConnectorSwitchParameters);

    ldmBasicConnectorSwitch.addLdmBasicConnector(LdmId.CENTRAXX, ldmConnectorCentraxx);

    return ldmBasicConnectorSwitch;

  }

  private LdmConnectorSwitchParameters getLdmConnectorSwitchParameters() {

    LdmConnectorSwitchParameters ldmConnectorSwitchParameters = new LdmConnectorSwitchParameters();

    ldmConnectorSwitchParameters.setLdmQueryConverter(ldmQueryConverter);
    ldmConnectorSwitchParameters.setLdmQueryLocationMapper(ldmQueryLocationMapper);
    ldmConnectorSwitchParameters.setLdmResultBuilder(ldmResultBuilder);

    return ldmConnectorSwitchParameters;

  }

}

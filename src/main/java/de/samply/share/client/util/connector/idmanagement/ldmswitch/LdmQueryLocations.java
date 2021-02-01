package de.samply.share.client.util.connector.idmanagement.ldmswitch;

import de.samply.share.client.util.connector.idmanagement.query.LdmId;
import java.util.HashMap;
import java.util.Map;

public class LdmQueryLocations {

  private final Map<LdmId, String> ldmIdLocation = new HashMap<>();

  public void addLdmQueryLocation(LdmId ldmId, String ldmQueryLocation) {
    ldmIdLocation.put(ldmId, ldmQueryLocation);
  }

  public String getLdmQueryLocation(LdmId ldmId) {
    return ldmIdLocation.get(ldmId);
  }

  public Map<LdmId, String> getAllLdmQueryLocations() {
    return ldmIdLocation;
  }

}

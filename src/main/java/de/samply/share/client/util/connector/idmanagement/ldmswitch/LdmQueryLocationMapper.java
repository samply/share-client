package de.samply.share.client.util.connector.idmanagement.ldmswitch;

import de.samply.share.client.util.connector.idmanagement.query.LdmId;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;

public class LdmQueryLocationMapper {

  private final Map<String, LdmQueryLocations> globalQueryLocationLdmQueryLocation =
      new HashMap<>();


  /**
   * Todo David.
   * @param ldmQueryLocations Todo David
   * @return Todo David
   */
  public String generateGlobalQueryLocation(LdmQueryLocations ldmQueryLocations) {

    //TODO: use generateRandomGlobalQueryLocation
    // TODO: persist ldm query locations
    //String globalQueryLocation = generateRandomGlobalQueryLocation();
    String globalQueryLocation = generateSimpleGlobalQueryLocation(ldmQueryLocations);

    globalQueryLocationLdmQueryLocation.put(globalQueryLocation, ldmQueryLocations);

    return globalQueryLocation;

  }

  private String generateSimpleGlobalQueryLocation(LdmQueryLocations ldmQueryLocations) {

    Map<LdmId, String> allLdmQueryLocations = ldmQueryLocations.getAllLdmQueryLocations();

    String simpleGlobalQueryLocation = null;

    if (allLdmQueryLocations.size() > 0) {

      LdmId firstKey = allLdmQueryLocations.keySet().stream().findFirst().get();
      simpleGlobalQueryLocation = allLdmQueryLocations.get(firstKey);

    } else {
      simpleGlobalQueryLocation = generateRandomGlobalQueryLocation();
    }

    return simpleGlobalQueryLocation;


  }

  private String generateRandomGlobalQueryLocation() {
    return RandomStringUtils.random(12, true, true);
  }

}

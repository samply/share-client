package de.samply.share.client.util;

import java.util.ArrayList;

public class Replace {

  /**
   * Replace some mdr data element ids with the itframes mdr ids.
   * Itframes are intended to search an mdr data element in a different entity.
   * Nevertheless, the data element is returned in the results in the right
   * entity.
   *
   * @param originalString regular mdr data element id.
   * @return itframe mdr data element id.
   */
  public static String replaceMdrKey(String originalString) {
    ArrayList<String[]> mdrKeys = new ArrayList<>();
    String[] mdrKey = {"urn:dktk:dataelement:36:2", "urn:itframe:dataelement:3:1"};
    String[] mdrKey2 = {"urn:dktk:dataelement:39:2", "urn:itframe:dataelement:5:1"};
    String[] mdrKey3 = {"urn:dktk:dataelement:38:2", "urn:itframe:dataelement:4:1"};
    String[] mdrKey4 = {"urn:dktk:dataelement:40:2", "urn:itframe:dataelement:6:1"};
    String[] mdrKey5 = {"urn:dktk:dataelement:33:2", "urn:itframe:dataelement:1:1"};
    String[] mdrKey6 = {"urn:dktk:dataelement:34:2", "urn:itframe:dataelement:2:1"};
    mdrKeys.add(mdrKey);
    mdrKeys.add(mdrKey2);
    mdrKeys.add(mdrKey3);
    mdrKeys.add(mdrKey4);
    mdrKeys.add(mdrKey5);
    mdrKeys.add(mdrKey6);
    String replacedString = originalString;
    for (String[] tmpMdrKey : mdrKeys) {
      replacedString = replacedString.replace(tmpMdrKey[0], tmpMdrKey[1]);
    }
    return replacedString;
  }
}

package de.samply.share.client.quality.report;

import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MdrIgnoredElements implements Iterable<MdrIdDatatype> {


  private final Set<String> stringMdrIds = new HashSet<>();
  private final Set<MdrIdDatatype> mdrIds = new HashSet<>();

  @Override
  public Iterator<MdrIdDatatype> iterator() {
    return mdrIds.iterator();
  }

  /**
   * Returns a list of the mdr ids to be ignored.
   *
   * @param mdrId List of mrd ids to be ignored.
   */
  public void add(MdrIdDatatype mdrId) {

    mdrIds.add(mdrId);
    stringMdrIds.add(getKey(mdrId));
  }

  /**
   * Checks if an mdr id should be ignored.
   *
   * @param mdrId Mdr Id to be checked .
   * @return Check.
   */
  public boolean isIgnored(MdrIdDatatype mdrId) {

    String key = getKey(mdrId);
    return stringMdrIds.contains(key);
  }

  /**
   * Returns key for the latest centraxx version of an mdr id.
   *
   * @param mdrId mdr id in question.
   * @return Key for the latest centraxx version of an mdr id.
   */
  public String getKey(MdrIdDatatype mdrId) {

    return (mdrId != null) ? mdrId.getLatestCentraxx() : null;

  }

}

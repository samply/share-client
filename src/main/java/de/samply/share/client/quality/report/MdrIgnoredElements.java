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
   * Todo.
   *
   * @param mdrId Todo.
   */
  public void add(MdrIdDatatype mdrId) {

    mdrIds.add(mdrId);
    stringMdrIds.add(getKey(mdrId));
  }

  /**
   * Todo.
   *
   * @param mdrId Todo.
   * @return Todo.
   */
  public boolean isIgnored(MdrIdDatatype mdrId) {

    String key = getKey(mdrId);
    return stringMdrIds.contains(key);
  }

  /**
   * Todo.
   *
   * @param mdrId Todo.
   * @return Todo.
   */
  public String getKey(MdrIdDatatype mdrId) {

    return (mdrId != null) ? mdrId.getLatestCentraxx() : null;

  }

}

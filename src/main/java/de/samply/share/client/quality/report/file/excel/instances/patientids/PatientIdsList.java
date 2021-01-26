package de.samply.share.client.quality.report.file.excel.instances.patientids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PatientIdsList implements Iterable<List<String>> {

  private final List<List<String>> lists = new ArrayList<>();
  private List<String> maxList = new ArrayList<>();

  /**
   * Todo.
   *
   * @param myCollection Todo.
   */
  public void addList(Collection<String> myCollection) {

    List<String> myList = new ArrayList<>(myCollection);
    Collections.sort(myList);

    lists.add(myList);

    if (myList.size() > maxList.size()) {
      maxList = myList;
    }

  }

  public Integer getMaxNumberOfPatientsOfAllPatientLists() {
    return maxList.size();
  }

  @Override
  public Iterator<List<String>> iterator() {
    return new PatientIdsIterator();
  }

  private class PatientIdsIterator implements Iterator<List<String>> {

    int index = 0;
    private final Iterator<String> myIterator;

    public PatientIdsIterator() {
      myIterator = maxList.iterator();
    }

    @Override
    public boolean hasNext() {
      return myIterator.hasNext();
    }

    @Override
    public List<String> next() {

      myIterator.next();
      List<String> myList = createList(index);
      index++;

      return myList;
    }

    private List<String> createList(int index) {

      List<String> newList = new ArrayList<>();

      for (List<String> myList : lists) {
        String element = (myList.size() > index) ? myList.get(index) : "";
        newList.add(element);
      }

      return newList;

    }

    @Override
    public void remove() {

    }

  }

}

package de.samply.share.client.model.graphdata;

import java.util.Map;
import java.util.TreeMap;

public class AgeDistribution {

  private Map<Integer, Integer> data;

  public AgeDistribution() {
    data = new TreeMap<>();
  }

  public Map<Integer, Integer> getData() {
    return data;
  }

  public void setData(Map<Integer, Integer> data) {
    this.data = data;
  }

  /**
   * The result count for a given age.
   *
   * @param age the age
   * @return the result by age
   */
  public int getAmountByAge(int age) {
    try {
      return data.get(age);
    } catch (Exception e) {
      return 0;
    }
  }

  public synchronized void incrementCountForAge(int age) {
    addToAge(age, 1);
  }

  public synchronized void addToAge(int age, int amount) {
    data.put(age, getAmountByAge(age) + amount);
  }

  @Override
  public String toString() {
    return "AgeDistribution{"
        + "data=" + data
        + '}';
  }
}

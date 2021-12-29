package de.samply.share.client.util.connector;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class NumberDisguiserTest {

  @ParameterizedTest
  @CsvFileSource(resources = "NumberDisguiserTest.csv")
  public void testGetDisguisedNumber(int number, int lower, int upper) {
    List<Integer> possibleValues = new ArrayList<>();
    possibleValues.add(lower);
    possibleValues.add(upper);

    assertTrue(possibleValues.contains(NumberDisguiser.getDisguisedNumber(number)));
  }
}

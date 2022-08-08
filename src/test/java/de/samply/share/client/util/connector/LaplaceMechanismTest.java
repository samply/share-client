package de.samply.share.client.util.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.SecureRandom;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class LaplaceMechanismTest {

  @ParameterizedTest
  @CsvFileSource(resources = "LaplaceMechanismTest.csv")
  public void testGetDisguisedNumber(int number, int result) {
    assertEquals(result,
        LaplaceMechanism.privatize(number, 1, 0.12, new SecureRandom("Test Seed".getBytes())));
  }
}
package de.samply.share.client.util.connector;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class LaplaceMechanismTest {

  public static final double EPSILON = 0.28;

  @ParameterizedTest
  @CsvFileSource(resources = "LaplaceMechanismTest.csv")
  public void testPrivatize(int number, int result) throws Exception {
    //to get the same randomness for each test invocation
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    sr.setSeed("testSeed12".getBytes(US_ASCII));
    assertEquals(result, LaplaceMechanism.privatize(number, 1, EPSILON, sr));
  }

  @RepeatedTest(1000)
  public void testPrivatize_zeroReturnsAlwaysZeroSensitivityOne() {
    assertEquals(0, LaplaceMechanism.privatize(0, 1, EPSILON));
  }

  @RepeatedTest(1000)
  public void testPrivatize_zeroReturnsAlwaysZeroSensitivityTen() {
    assertEquals(0, LaplaceMechanism.privatize(0, 10, EPSILON));
  }

  @RepeatedTest(1000)
  public void testPrivatize_NeverReturnsNegativeValuesSensitivityOne() {
    assertTrue(0 <= LaplaceMechanism.privatize((long) (Math.random() * 10000), 1,
        EPSILON));
  }

  @RepeatedTest(1000)
  public void testPrivatize_NeverReturnsNegativeValuesSensitivityTen() {
    assertTrue(0 <= LaplaceMechanism.privatize((long) (Math.random() * 10000), 10,
        EPSILON));
  }
}

package de.samply.share.client.util.connector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class LaplaceMechanismTest {

  @ParameterizedTest
  @CsvFileSource(resources = "LaplaceMechanismTest.csv")
  public void testGetDisguisedNumber(int number, int result)
      throws NoSuchAlgorithmException, UnsupportedEncodingException {
    //to get the same randomness for each test invocation
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
    sr.setSeed("testSeed12".getBytes("us-ascii"));
    assertEquals(result,
        LaplaceMechanism.privatize(number, 1, 0.12, sr));
  }
}
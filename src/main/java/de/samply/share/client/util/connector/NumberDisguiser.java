package de.samply.share.client.util.connector;

import java.util.Random;

public class NumberDisguiser {


  /**
   * Get a random disguisedNumber.
   *
   * @param originalNumber the originalNumber
   * @return disguisedNumber
   */
  public static int getDisguisedNumber(int originalNumber) {
    Random random = new Random();
    if (originalNumber > 0) {
      return 10 * ((originalNumber + random.nextInt(4) + 9) / 10);
    } else {
      return 0;
    }
  }


}

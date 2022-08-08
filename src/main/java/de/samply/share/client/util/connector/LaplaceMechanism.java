package de.samply.share.client.util.connector;

import java.security.SecureRandom;

/**
 * This class implements the laplacian mechanism.
 *
 * @author Tobias Kussel
 */
public class LaplaceMechanism {

  /**
   * Draw from a laplacian distribution.
   *
   * @param mu mean of distribution
   * @param b  diversity of distribution
   */
  private static double laplace(double mu, double b, SecureRandom rand) {
    double min = -0.5;
    double max = 0.5;
    double random = rand.nextDouble();
    double uniform = min + random * (max - min);
    return mu - b * Math.signum(uniform) * Math.log(1 - 2 * Math.abs(uniform));
  }

  /**
   * Permute a value with the (epsilo, 0) laplacian mechanism.
   *
   * @param value       clear value to permute
   * @param sensitivity sensitivity of query
   * @param epsilon     epsilon parameter of differential privacy
   */
  public static long privatize(double value, double sensitivity, double epsilon,
      SecureRandom rand) {
    value = (long) (value + LaplaceMechanism.laplace(0, sensitivity / epsilon, rand));
    if (value > 0) {
      long rem = (long) (value % 10);
      return (long) (rem >= 5 ? (value - rem + 10) : (value - rem));
    } else {
      return 0;
    }
  }

  public static long privatize(double value, double sensitivity, double epsilon) {
    return privatize(value, sensitivity, epsilon, new SecureRandom());
  }

}

package de.samply.share.client.util.connector;

import java.security.SecureRandom;

/**
 * This class implements the laplacian mechanism.
 *
 * @author Tobias Kussel, Deniz Tas, Alexander Kiel
 */
public class LaplaceMechanism {

  /**
   * Permute a value with the (epsilon, 0) laplacian mechanism.
   *
   * @param value       clear value to permute
   * @param sensitivity sensitivity of query
   * @param epsilon     epsilon parameter of differential privacy
   * @return the permuted value
   */
  public static long privatize(long value, double sensitivity, double epsilon) {
    return privatize(value, sensitivity, epsilon, new SecureRandom());
  }

  static long privatize(long value, double sensitivity, double epsilon, SecureRandom rand) {
    if (value > 0) {
      return Math.max(0, Math.round((value + laplace(0, sensitivity / epsilon, rand)) / 10) * 10);
    } else {
      return 0;
    }
  }

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
}

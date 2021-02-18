package de.samply.share.client.quality.report.file.id.generator;

import java.math.BigInteger;
import java.security.SecureRandom;

public class QualityFileIdGeneratorImpl implements QualityFileIdGenerator {


  private final SecureRandom random = new SecureRandom();

  /**
   * Test class that generates a file id.
   *
   * @param args no arguments.
   */
  public static void main(String[] args) {

    //TODO Test
    QualityFileIdGenerator fileIdGenerator = new QualityFileIdGeneratorImpl();
    System.out.println(fileIdGenerator.generateFileId());
  }

  @Override
  public String generateFileId() {
    return new BigInteger(130, random).toString(32);
  }

}

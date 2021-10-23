package de.samply.share.client.quality.report.views.fromto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ViewFromToFactoryTest {

  private final ViewFromToFactory factory = new ViewFromToFactory();

  @Test
  void createYear() {
    ViewFromTo viewFromTo = factory.createYear(2021);

    assertEquals("2021-01-01T00:00:00Z", viewFromTo.getFrom());
    assertEquals("2022-01-01T00:00:00Z", viewFromTo.getTo());
  }

  @Test
  void createMonth_2() {
    ViewFromTo viewFromTo = factory.createMonth(2021, 2);

    assertEquals("2021-02-01T00:00:00Z", viewFromTo.getFrom());
    assertEquals("2021-03-01T00:00:00Z", viewFromTo.getTo());
  }

  @Test
  void createMonth_12() {
    ViewFromTo viewFromTo = factory.createMonth(2021, 12);

    assertEquals("2021-12-01T00:00:00Z", viewFromTo.getFrom());
    assertEquals("2022-01-01T00:00:00Z", viewFromTo.getTo());
  }
}

package de.samply.share.client.quality.report.views.fromto.scheduler;

import static de.samply.share.client.model.EnumConfiguration.QUALITY_REPORT_SCHEDULER_FORMAT;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import de.samply.share.client.util.db.ConfigurationUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ViewFromToSchedulerFactoryTest {

  private final ViewFromToSchedulerFactory factory = new ViewFromToSchedulerFactory();

  @Test
  void createViewFromToScheduler_default() {
    try (MockedStatic<ConfigurationUtil> mocked = mockStatic(ConfigurationUtil.class)) {
      mocked.when(() -> ConfigurationUtil.getConfigurationElementValue(
          QUALITY_REPORT_SCHEDULER_FORMAT)).thenReturn(null);

      ViewFromToScheduler scheduler = factory.createViewFromToScheduler();

      assertTrue(scheduler instanceof ViewFromToSchedulerByYear);
    }
  }

  @Test
  void createViewFromToScheduler_QUALITY_REPORT_SCHEDULER_BY_YEAR() {
    try (MockedStatic<ConfigurationUtil> mocked = mockStatic(ConfigurationUtil.class)) {
      mocked.when(() -> ConfigurationUtil.getConfigurationElementValue(
          QUALITY_REPORT_SCHEDULER_FORMAT)).thenReturn("YEAR");

      ViewFromToScheduler scheduler = factory.createViewFromToScheduler();

      assertTrue(scheduler instanceof ViewFromToSchedulerByYear);
    }
  }

  @Test
  void createViewFromToScheduler_QUALITY_REPORT_SCHEDULER_BY_MONTH() {
    try (MockedStatic<ConfigurationUtil> mocked = mockStatic(ConfigurationUtil.class)) {
      mocked.when(() -> ConfigurationUtil.getConfigurationElementValue(
          QUALITY_REPORT_SCHEDULER_FORMAT)).thenReturn("MONTH");

      ViewFromToScheduler scheduler = factory.createViewFromToScheduler();

      assertTrue(scheduler instanceof ViewFromToSchedulerByMonth);
    }
  }

  @Test
  void createViewFromToScheduler_QUALITY_REPORT_SCHEDULER_ALL() {
    try (MockedStatic<ConfigurationUtil> mocked = mockStatic(ConfigurationUtil.class)) {
      mocked.when(() -> ConfigurationUtil.getConfigurationElementValue(
          QUALITY_REPORT_SCHEDULER_FORMAT)).thenReturn("ALL");

      ViewFromToScheduler scheduler = factory.createViewFromToScheduler();

      assertTrue(scheduler instanceof ViewFromToSchedulerAll);
    }
  }
}

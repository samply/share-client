package de.samply.share.client.quality.report.views.fromto.scheduler;

import static de.samply.share.client.model.EnumConfiguration.QUALITY_REPORT_GROUP_MODUL;
import static de.samply.share.client.model.EnumConfiguration.QUALITY_REPORT_SCHEDULER_FORMAT;
import static de.samply.share.client.model.EnumConfiguration.QUALITY_REPORT_SCHEDULER_YEARS;
import static de.samply.share.client.util.db.ConfigurationUtil.getConfigurationElementValue;

import de.samply.share.client.quality.report.QualityReportSchedulerFormat;
import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewFromToSchedulerFactory {

  private static final Map<String, QualityReportSchedulerFormat> FORMATS = Arrays.stream(
      QualityReportSchedulerFormat.values()).collect(
      Collectors.toMap(Enum::name, Function.identity()));

  private final ViewFromToFactory viewFromToFactory = new ViewFromToFactory();

  /**
   * Creates a scheduler for {@link ViewFromTo ViewFromTos} according to the config.
   *
   * @return a scheduler
   */
  public ViewFromToScheduler createViewFromToScheduler() {
    QualityReportSchedulerFormat format = FORMATS.getOrDefault(getConfigurationElementValue(
        QUALITY_REPORT_SCHEDULER_FORMAT), QualityReportSchedulerFormat.YEAR);

    switch (format) {
      case MONTH:
        return createViewFromToSchedulerByMonthImpl();
      case YEAR:
        return createViewFromToSchedulerByYearImpl();
      case ALL:
        return ViewFromToSchedulerAll.INSTANCE;
      default:
        throw new IllegalStateException();
    }
  }

  private ViewFromToScheduler createViewFromToSchedulerByMonthImpl() {
    ViewFromToSchedulerByMonth viewFromToSchedulerByMonth = new ViewFromToSchedulerByMonth(
        viewFromToFactory);

    Integer groupsModul = tryParseInt(getConfigurationElementValue(QUALITY_REPORT_GROUP_MODUL));
    Integer years = tryParseInt(getConfigurationElementValue(QUALITY_REPORT_SCHEDULER_YEARS));

    if (groupsModul != null) {
      viewFromToSchedulerByMonth.setGroupsModul(groupsModul);
    }

    if (years != null) {
      viewFromToSchedulerByMonth.setNumberOfYears(years);
    }

    return viewFromToSchedulerByMonth;
  }

  private ViewFromToScheduler createViewFromToSchedulerByYearImpl() {
    ViewFromToSchedulerByYear viewFromToSchedulerByYear = new ViewFromToSchedulerByYear(
        viewFromToFactory);

    Integer years = tryParseInt(getConfigurationElementValue(QUALITY_REPORT_SCHEDULER_YEARS));
    if (years != null) {
      viewFromToSchedulerByYear.setYears(years);
    }

    return viewFromToSchedulerByYear;
  }

  private static Integer tryParseInt(String s) {
    try {
      return Integer.valueOf(s);
    } catch (Exception e) {
      return null;
    }
  }
}

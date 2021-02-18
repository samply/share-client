package de.samply.share.client.quality.report.views.fromto.scheduler;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import de.samply.share.client.util.db.ConfigurationUtil;

public class ViewFromToSchedulerFactory {


  private final ViewFromToFactory viewFromToFactory = new ViewFromToFactory();

  /**
   * Create Scheduler for View From-To.
   *
   * @return View From-To Scheduler.
   */
  public ViewFromToScheduler createViewFromToScheduler() {

    String format = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_SCHEDULER_FORMAT);

    ViewFromToSchedulerFormat viewFromToSchedulerFormat = ViewFromToSchedulerFormat
        .getViewFromToSchedulerFormat(format);
    if (viewFromToSchedulerFormat == null) {
      viewFromToSchedulerFormat = ViewFromToSchedulerFormat.getDefault();
    }

    switch (viewFromToSchedulerFormat) {

      case BY_MONTH:
        return createViewFromToSchedulerByMonthImpl();
      case BY_YEAR:
        return createViewFromToSchedulerByYearImpl();
      default:
        return null;

    }


  }

  private ViewFromToScheduler createViewFromToSchedulerByMonthImpl() {

    ViewFromToSchedulerByMonthImpl viewFromToSchedulerByMonth = new ViewFromToSchedulerByMonthImpl(
        viewFromToFactory);

    String groupsModulS = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_GROUP_MODUL);
    Integer groupsModul = convert(groupsModulS);
    String yearsS = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_SCHEDULER_YEARS);
    Integer years = convert(yearsS);

    if (groupsModul != null) {
      viewFromToSchedulerByMonth.setGroupsModul(groupsModul);
    }

    if (years != null) {
      viewFromToSchedulerByMonth.setNumberOfYears(years);
    }

    return viewFromToSchedulerByMonth;
  }

  private ViewFromToScheduler createViewFromToSchedulerByYearImpl() {

    ViewFromToSchedulerByYearImpl viewFromToSchedulerByYear = new ViewFromToSchedulerByYearImpl(
        viewFromToFactory);

    String yearsS = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_SCHEDULER_YEARS);
    Integer years = convert(yearsS);

    if (years != null) {
      viewFromToSchedulerByYear.setYears(years);
    }

    return viewFromToSchedulerByYear;

  }

  Integer convert(String number) {

    try {
      return Integer.valueOf(number);

    } catch (Exception e) {
      return null;
    }
  }

  public enum ViewFromToSchedulerFormat {

    BY_YEAR(EnumConfiguration.QUALITY_REPORT_SCHEDULER_BY_YEAR.name()),
    BY_MONTH(EnumConfiguration.QUALITY_REPORT_SCHEDULER_BY_MONTH.name());

    String title;

    ViewFromToSchedulerFormat(String title) {
      this.title = title;
    }

    public static ViewFromToSchedulerFormat getDefault() {
      return BY_YEAR;
    }

    /**
     * Gets format of scheduler of view from-to .
     *
     * @param format format as String.
     * @return format of scheduler of view from-to.
     */
    public static ViewFromToSchedulerFormat getViewFromToSchedulerFormat(String format) {

      for (ViewFromToSchedulerFormat viewFromToSchedulerFormat : values()) {

        if (viewFromToSchedulerFormat.title.equals(format)) {
          return viewFromToSchedulerFormat;
        }
      }

      return null;

    }

  }


}

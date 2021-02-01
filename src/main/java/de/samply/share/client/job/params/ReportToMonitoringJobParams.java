package de.samply.share.client.job.params;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;

/**
 * The settings for an ReportToMonitoringJob are kept in an instance of this class. Reads the
 * settings for checks to perform from the database.
 */
public class ReportToMonitoringJobParams {

  public static final String JOBGROUP = "MaintenanceGroup";
  public static final String JOBNAME = "ReportToMonitoringJob";
  public static final String TRIGGERNAME = "ReportToMonitoringJobTrigger";

  private final boolean countTotal;
  private final boolean countDktkFlagged;
  private final boolean countReferenceQuery;
  private final boolean timeReferenceQuery;
  private final boolean centraxxMappingInformation;

  /**
   * Read the configs for the reporting job from the database.
   */
  public ReportToMonitoringJobParams() {
    this.countTotal = ConfigurationUtil
        .getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_COUNT_TOTAL);
    this.countDktkFlagged = ConfigurationUtil
        .getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_COUNT_DKTKFLAG);
    this.countReferenceQuery = ConfigurationUtil.getConfigurationElementValueAsBoolean(
        EnumConfiguration.MONITORING_REPORT_COUNT_REFERENCEQUERY);
    this.timeReferenceQuery = ConfigurationUtil.getConfigurationElementValueAsBoolean(
        EnumConfiguration.MONITORING_REPORT_TIME_REFERENCEQUERY);
    this.centraxxMappingInformation = ConfigurationUtil.getConfigurationElementValueAsBoolean(
        EnumConfiguration.MONITORING_REPORT_CENTRAXX_MAPPING_INFORMATION);
  }

  public boolean isCountTotal() {
    return countTotal;
  }

  public boolean isCountDktkFlagged() {
    return countDktkFlagged;
  }

  public boolean isCountReferenceQuery() {
    return countReferenceQuery;
  }

  public boolean isTimeReferenceQuery() {
    return timeReferenceQuery;
  }

  public boolean isCentraxxMappingInformation() {
    return centraxxMappingInformation;
  }

  /**
   * Is any of the (currently) four checks enabled?.
   *
   * @return true if one ore more are enabled, false otherwise
   */
  public boolean anyCheckToPerform() {
    return (countTotal || countDktkFlagged || countReferenceQuery || timeReferenceQuery
        || centraxxMappingInformation);
  }

  @Override
  public String toString() {
    return "ReportToMonitoringJobParams{"
        + "countTotal=" + countTotal
        + ", countDktkFlagged=" + countDktkFlagged
        + ", countReferenceQuery=" + countReferenceQuery
        + ", timeReferenceQuery=" + timeReferenceQuery
        + ", centraxxMappingVersion=" + centraxxMappingInformation
        + '}';
  }
}

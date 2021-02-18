package de.samply.share.client.job.params;

import java.util.Date;

/**
 * A representation of a quartz job, keeping the information that is needed on the job list page.
 */
public class QuartzJob {

  private String jobName;
  private String jobGroup;
  private Date started;
  private Date nextFireTime;
  private Date previousFireTime;
  private String cronExpression;
  private boolean paused;
  private String description;

  /**
   * Create a Quartz job with the given settings.
   *
   * @param jobName          name of the job
   * @param jobGroup         name of the group
   * @param started          the starting time
   * @param nextFireTime     next time when the job will be executing
   * @param previousFireTime last time executed
   * @param cronExpression   the cron expression
   * @param paused           if the job is paused
   * @param description      description of the job
   */
  public QuartzJob(String jobName, String jobGroup, Date started, Date nextFireTime,
      Date previousFireTime, String cronExpression, boolean paused, String description) {
    this.jobName = jobName;
    this.jobGroup = jobGroup;
    this.started = started;
    this.nextFireTime = nextFireTime;
    this.previousFireTime = previousFireTime;
    this.cronExpression = cronExpression;
    this.paused = paused;
    this.description = description;
  }

  /**
   * Create a Quartz job with the given settings.
   *
   * @param jobKey           jobKey with the name and the group of the job
   * @param started          the starting time
   * @param nextFireTime     next time when the job will be executing
   * @param previousFireTime last time executed
   * @param cronExpression   the cron expression
   * @param paused           if the job is paused
   * @param description      description of the job
   */
  public QuartzJob(String jobKey, Date started, Date nextFireTime, Date previousFireTime,
      String cronExpression, boolean paused, String description) {
    this.jobName = jobKey.substring(jobKey.indexOf('.') + 1);
    this.jobGroup = jobKey.substring(0, jobKey.indexOf('.'));
    this.started = started;
    this.nextFireTime = nextFireTime;
    this.previousFireTime = previousFireTime;
    this.cronExpression = cronExpression;
    this.paused = paused;
    this.description = description;
  }

  public String getJobName() {
    return jobName;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getJobGroup() {
    return jobGroup;
  }

  public void setJobGroup(String jobGroup) {
    this.jobGroup = jobGroup;
  }

  public Date getStarted() {
    return started;
  }

  public void setStarted(Date started) {
    this.started = started;
  }

  public Date getNextFireTime() {
    return nextFireTime;
  }

  public void setNextFireTime(Date nextFireTime) {
    this.nextFireTime = nextFireTime;
  }

  public Date getPreviousFireTime() {
    return previousFireTime;
  }

  public void setPreviousFireTime(Date previousFireTime) {
    this.previousFireTime = previousFireTime;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public String getJobKey() {
    return jobGroup + "." + jobName;
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}

package de.samply.share.client.util.db;

import de.samply.share.client.model.db.tables.daos.JobScheduleDao;
import de.samply.share.client.model.db.tables.pojos.JobSchedule;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;

/**
 * Helper Class for CRUD operations with job scheduling objects.
 */
public class JobScheduleUtil {

  private static final Logger logger = LogManager.getLogger(JobScheduleUtil.class);

  private static final JobScheduleDao jobScheduleDao;

  static {
    jobScheduleDao = new JobScheduleDao(ResourceManager.getConfiguration());
  }

  // Prevent instantiation
  private JobScheduleUtil() {
  }

  /**
   * Get the job scheduling DAO.
   *
   * @return the job scheduling DAO
   */
  public static JobScheduleDao getJobScheduleDao() {
    return jobScheduleDao;
  }

  /**
   * Get all Jobs to be scheduled from the database.
   *
   * @return the list of all jobs with their scheduling info
   */
  public static List<JobSchedule> getJobSchedules() {
    return jobScheduleDao.findAll();
  }

  /**
   * Get one job by its id.
   *
   * @param id the id of the job to get
   * @return the job and its scheduling info
   */
  public static JobSchedule fetchJobScheduleById(int id) {
    return jobScheduleDao.fetchOneById(id);
  }

  /**
   * Get one job by its job key.
   *
   * @param jobKey the job key of the job to get
   * @return the job and its scheduling info
   */
  public static JobSchedule fetchJobScheduleByJobKey(String jobKey) {
    return jobScheduleDao.fetchOneByJobKey(jobKey);
  }

  /**
   * Insert a new job with scheduling info into the database.
   *
   * @param jobSchedule the new job to insert
   */
  public static void insertJobSchedule(JobSchedule jobSchedule) {
    jobScheduleDao.insert(jobSchedule);
  }

  /**
   * Update a job in the database.
   *
   * @param jobSchedule the job to update
   */
  public static void updateJobSchedule(JobSchedule jobSchedule) {
    jobScheduleDao.update(jobSchedule);
  }

  /**
   * Delete a job from the database.
   *
   * @param jobSchedule the job to delete
   */
  public static void deleteJobSchedule(JobSchedule jobSchedule) {
    jobScheduleDao.delete(jobSchedule);
  }

  /**
   * Delete a job from the database.
   *
   * @param jobKey the job key of the job to delete
   */
  public static void deleteJobScheduleByJobKey(String jobKey) {
    jobScheduleDao.delete(fetchJobScheduleByJobKey(jobKey));
  }

  /**
   * Set the suspended state of a job.
   *
   * @param jobKey    the job key of the job to suspend/resume
   * @param suspended true if the job shall be suspended, false if it shall be running
   */
  public static void setSuspended(JobKey jobKey, boolean suspended) {
    JobSchedule jobSchedule = fetchJobScheduleByJobKey(jobKey.toString());
    if (jobSchedule != null) {
      jobSchedule.setPaused(suspended);
      jobScheduleDao.update(jobSchedule);
    } else {
      logger.error("Job " + jobKey.toString() + " not found and could not be suspended");
    }
  }

}

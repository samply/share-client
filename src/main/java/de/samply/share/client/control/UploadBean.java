package de.samply.share.client.control;

import de.samply.share.client.job.params.UploadJobParams;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.EventLog;
import de.samply.share.client.model.db.tables.pojos.Upload;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.UploadUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.util.Messages;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

/**
 * ViewScoped backing bean that is used on upload related pages.
 */
@ManagedBean(name = "uploadBean")
@ViewScoped
public class UploadBean implements Serializable {

  private static final Logger logger = LogManager.getLogger(UploadBean.class);

  @ManagedProperty(value = "#{loginBean}")
  private LoginBean loginBean;

  private List<Upload> uploads;

  private int selectedUploadId;
  private Upload selectedUpload;
  private List<EventLog> uploadEvents;

  private boolean dryrun;
  private boolean fullUpload;
  private boolean dktkFlagged = true;

  public LoginBean getLoginBean() {
    return loginBean;
  }

  public void setLoginBean(LoginBean loginBean) {
    this.loginBean = loginBean;
  }

  public List<Upload> getUploads() {
    return uploads;
  }

  public void setUploads(List<Upload> uploads) {
    this.uploads = uploads;
  }

  public int getSelectedUploadId() {
    return selectedUploadId;
  }

  public void setSelectedUploadId(int selectedUploadId) {
    this.selectedUploadId = selectedUploadId;
  }

  public Upload getSelectedUpload() {
    return selectedUpload;
  }

  public void setSelectedUpload(Upload selectedUpload) {
    this.selectedUpload = selectedUpload;
  }

  public List<EventLog> getUploadEvents() {
    return uploadEvents;
  }

  public void setUploadEvents(List<EventLog> uploadEvents) {
    this.uploadEvents = uploadEvents;
  }

  public boolean isDryrun() {
    return dryrun;
  }

  public void setDryrun(boolean dryrun) {
    this.dryrun = dryrun;
  }

  public boolean isFullUpload() {
    return fullUpload;
  }

  public void setFullUpload(boolean fullUpload) {
    this.fullUpload = fullUpload;
  }

  public boolean isDktkFlagged() {
    return dktkFlagged;
  }

  public void setDktkFlagged(boolean dktkFlagged) {
    this.dktkFlagged = dktkFlagged;
  }

  @PostConstruct
  public void init() {
    refreshUploadList();
  }

  /**
   * Load the selected upload (selected by view parameter) and the corresponding event log
   * messages.
   */
  public void loadSelectedUpload() {
    selectedUpload = UploadUtil.fetchUploadById(selectedUploadId);
    uploadEvents = EventLogUtil.fetchEventLogForUploadById(selectedUploadId);
  }

  /**
   * Reload the list of uploads from the database.
   */
  private void refreshUploadList() {
    uploads = UploadUtil.fetchUploads();
  }

  /**
   * Spawn a new Upload to central search. Depending on the setting of the switches on the page,
   * spawn either an upload or dry run - a full or an incremental upload and either upload patient
   * datasets for patients with explicit dktk consent or those without that.
   *
   * @return navigation information
   */
  public String spawnNewUpload() {
    int uploadId = storeUpload(dryrun, fullUpload, dktkFlagged);
    String jobName = dktkFlagged ? UploadJobParams.JOBNAME_DKTK : UploadJobParams.JOBNAME_NO_DKTK;
    JobKey jobKey = JobKey.jobKey(jobName, UploadJobParams.JOBGROUP);

    // Fill the JobDataMap for the trigger
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put(UploadJobParams.UPLOAD_ID, uploadId);
    jobDataMap.put(UploadJobParams.STATUS, UploadStatusType.US_NEW.getLiteral());
    jobDataMap.put(UploadJobParams.DKTK_FLAGGED, dktkFlagged);

    try {
      logger.info("Give Execute Job to scheduler for new upload");
      ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);

      Messages.create("Upload Job spawned")
          .detail("The Job has been spawned. It might take a while until it is completed.")
          .add();
      return "upload_list?faces-redirect=true";
    } catch (SchedulerException e) {
      logger.error("Error spawning Upload Job", e);
      Messages.create("Upload Job could not be spawned")
          .detail("An Scheduler Exception occurred: " + e.getMessage())
          .error().add();
      return "";
    }
  }

  /**
   * Write the upload object to the database.
   *
   * @return the database id of the upload
   */
  private int storeUpload(boolean dryrun, boolean fullUpload, boolean dktkFlagged) {
    Upload upload = new Upload();
    upload.setStatus(UploadStatusType.US_NEW);
    upload.setTriggeredAt(SamplyShareUtils.getCurrentSqlTimestamp());
    upload.setTriggeredBy(loginBean.getUser().getUsername());
    upload.setIsDryrun(dryrun);
    upload.setIsFullUpload(fullUpload);
    upload.setDktkFlagged(dktkFlagged);
    upload.setSuccessCount(0);
    upload.setFailureCount(0);
    return UploadUtil.insertUpload(upload);
  }

  /**
   * Check if any upload getPatientIds is running at the moment.
   *
   * @return true if an upload is active, false otherwise
   */
  public boolean isUploadRunning() {
    return (UploadUtil.getActiveUpload() != null);
  }

  /**
   * Cancel all upload related jobs.
   */
  public void cancelUploadJobs() {
    Upload activeUpload = UploadUtil.getActiveUpload();
    activeUpload.setStatus(UploadStatusType.US_CANCELED);
    UploadUtil.updateUpload(activeUpload);
    ApplicationBean.cancelAllJobsForUpload();
  }

  public boolean isUploadNonDktkAllowed() {
    return ConfigurationUtil.getConfigurationElementValueAsBoolean(
        EnumConfiguration.CENTRAL_MDS_DATABASE_UPLOAD_PATIENTS_WITH_LOCAL_CONSENT);
  }
}

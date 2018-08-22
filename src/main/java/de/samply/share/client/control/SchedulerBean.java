package de.samply.share.client.control;

import de.samply.share.client.job.params.QuartzJob;
import de.samply.share.client.model.db.tables.pojos.JobSchedule;
import de.samply.share.client.util.db.JobScheduleUtil;
import de.samply.share.common.utils.ProjectInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A ViewScoped backing bean for the job scheduling page
 */
@ManagedBean(name = "schedulerBean")
@ViewScoped
public class SchedulerBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(SchedulerBean.class);

    private Scheduler scheduler;

    private List<QuartzJob> jobList = new ArrayList<>();

    public SchedulerBean() throws SchedulerException {
        init();
    }

    /**
     * Load the scheduler and read all jobs
     */
    private void init() throws SchedulerException  {
        ServletContext servletContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();

        //Get QuartzInitializerListener
        StdSchedulerFactory stdSchedulerFactory = (StdSchedulerFactory) servletContext
                .getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);

        scheduler = stdSchedulerFactory.getScheduler();
        readAllJobs();
    }

    /**
     * Load all jobs and their triggers and add them to the job list to display
     */
    private void readAllJobs() throws SchedulerException {
        jobList = new ArrayList<>();
        // loop jobs by group
        for (String groupName : scheduler.getJobGroupNames()) {
            // get jobkey
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                JobDataMap jobDataMap = jobDetail.getJobDataMap();

                // get job's trigger
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);

                if (triggers == null || triggers.isEmpty()) {
                    if (jobDetail.isDurable()) {
                        if (jobDataMap != null && jobDataMap.getBooleanFromString("SHOW")) {
                            jobList.add(new QuartzJob(jobKey.getName(), jobKey.getGroup(), null, null, null, "", isPaused(jobKey), jobDetail.getDescription()));
                        }
                    }
                } else {
                    for (Trigger trigger : triggers) {
                        Date nextFireTime = trigger.getNextFireTime();
                        Date previousFireTime = trigger.getPreviousFireTime();
                        String cronExpression;
                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = ((CronTrigger) trigger);
                            cronExpression = cronTrigger.getCronExpression();
                            if (ProjectInfo.INSTANCE.getProjectName().toLowerCase().equals("samply") &&( jobKey.getGroup().equals("CentralSearchGroup"))) {
                                if(!JobScheduleUtil.fetchJobScheduleByJobKey(jobKey.toString()).getPaused()) {
                                   suspend(jobName,jobGroup);
                                }
                            }else {
                                jobList.add(new QuartzJob(jobName, jobGroup, null, nextFireTime, previousFireTime, cronExpression, isPaused(jobKey), jobDetail.getDescription()));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Attach a single-fire trigger to the job, that fires right away
     *
     * @param jobName name of the job
     * @param jobGroup name of the group
     */
    public void fireNow(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.triggerJob(jobKey);
        readAllJobs();
    }

    /**
     * Pause a job
     *
     * @param jobName name of the job
     * @param jobGroup name of the group
     */
    public void suspend(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.pauseJob(jobKey);
        JobScheduleUtil.setSuspended(jobKey, true);
        readAllJobs();
    }

    /**
     * Resume a job
     *
     * @param jobName name of the job
     * @param jobGroup name of the group
     */
    public void resume(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        scheduler.resumeJob(jobKey);
        JobScheduleUtil.setSuspended(jobKey, false);
        readAllJobs();
    }

    /**
     * Cancel a job
     *
     * @param jobName name of the job
     * @param jobGroup name of the group
     */
    public void cancel(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            scheduler.interrupt(jobKey);
            readAllJobs();
        } catch (UnableToInterruptJobException e) {
            e.printStackTrace();
        }
    }

    /**
     * Re-schedule a job with the cron expression from the database
     *
     * @param job the job that should be rescheduled
     */
    public void rescheduleJob(QuartzJob job) throws SchedulerException {
        JobSchedule jobSchedule = JobScheduleUtil.fetchJobScheduleByJobKey(job.getJobKey());
        if (jobSchedule != null) {
            jobSchedule.setCronExpression(job.getCronExpression());
            JobScheduleUtil.updateJobSchedule(jobSchedule);
            ApplicationBean.scheduleJobsFromDatabase();
            readAllJobs();
        }
    }

    /**
     * Check if the trigger assigned to the job is paused
     *
     * Iterate through all triggers with cron expressions (should be 0 or 1) assigned to a job. If any of them is paused,
     * return true. False otherwise.
     *
     * @param jobKey the job key for which the check is performed
     */
    private boolean isPaused(JobKey jobKey) {
        try {
            List<Trigger> triggers = (List<Trigger>) ApplicationBean.getScheduler().getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                if (trigger instanceof CronTrigger) {
                    CronTrigger cronTrigger = ((CronTrigger) trigger);
                    if (cronTrigger.getCronExpression() != null) {
                        Trigger.TriggerState triggerState = ApplicationBean.getScheduler().getTriggerState(cronTrigger.getKey());
                        return triggerState == Trigger.TriggerState.PAUSED;
                    }
                } else {
                    return false;
                }
            }
        } catch (SchedulerException e) {
            logger.warn("Scheduler Exception caught", e);
        }
        return false;
    }

    public List<QuartzJob> getJobList() {
        return jobList;
    }

}
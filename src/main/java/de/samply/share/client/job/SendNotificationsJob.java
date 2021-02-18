package de.samply.share.client.job;

import de.samply.share.client.util.MailUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SendNotificationsJob implements Job {

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    MailUtils.checkAndSendNotifications();
  }
}

package de.samply.share.client.job;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.util.connector.IdManagerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.connector.exception.LdmConnectorException;
import de.samply.share.common.model.dto.UserAgent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * This job checks the other local components (id manager, local datamanagement) for their version
 * and status.
 */
@DisallowConcurrentExecution
public class CheckLocalComponentsJob implements Job {

  private static final Logger logger = LogManager.getLogger(CheckLocalComponentsJob.class);
  private static final IdManagerConnector idManagerConnector;
  private static final LdmConnector ldmConnector;
  private static final UserAgent userAgent;

  static {
    idManagerConnector = new IdManagerConnector();
    ldmConnector = ApplicationBean.getLdmConnector();
    userAgent = ApplicationBean.getUserAgent();
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    String ldmString = getLocalDatamanagementString();
    String idManagerString = getIdManagerString();
    UserAgent newUserAgent;

    if (userAgent == null) {
      newUserAgent = ApplicationBean.getDefaultUserAgent();
    } else {
      newUserAgent = new UserAgent(userAgent.getProjectContext(), userAgent.getShareName(),
          userAgent.getShareVersion());
    }

    if (ldmString != null && ldmString.indexOf('/') > 0) {
      String ldmName = ldmString.substring(0, ldmString.indexOf('/'));
      String ldmVersion = ldmString.substring(ldmString.indexOf('/') + 1);
      newUserAgent.setLdmName(ldmName);
      newUserAgent.setLdmVersion(ldmVersion);
    }

    if (idManagerString != null && idManagerString.indexOf('/') > 0) {
      String idManagerName = idManagerString.substring(0, idManagerString.indexOf('/'));
      String idManagerVersion = idManagerString.substring(idManagerString.indexOf('/') + 1);
      newUserAgent.setIdManagerName(idManagerName);
      newUserAgent.setIdManagerVersion(idManagerVersion);
    }

    if (newUserAgent.equals(userAgent)) {
      logger.debug("UserAgent unchanged");
    } else {
      logger.info("UserAgent changed to: " + newUserAgent.toString());
      ApplicationBean.setUserAgent(newUserAgent);
    }
  }

  /**
   * Get name and version number from local datamanagement.
   *
   * @return a String consisting of name and version number as reported by the local datamanagement.
   *        Separated by a forward slash
   */
  private String getLocalDatamanagementString() {
    try {
      return ldmConnector.getUserAgentInfo();
    } catch (LdmConnectorException e) {
      logger.warn("Could not read User Agent Info from local datamanagement.");
      return "Unknown Local Datamanagement/unknown";
    }
  }

  /**
   * Get name and version number from ID manager.
   *
   * @return a String consisting of name and version number as reported by the ID Manager. Separated
   *        by a forward slash
   */
  private String getIdManagerString() {
    try {
      return idManagerConnector.getUserAgentInfo();
    } catch (IdManagerConnectorException e) {
      logger.warn("Could not read User Agent Info from id management.");
      return "Unknown ID Management/unknown";
    }
  }


}

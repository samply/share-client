package de.samply.share.client.job;

import ca.uhn.fhir.context.FhirContext;
import de.samply.directory_sync.Sync;
import de.samply.share.client.control.ApplicationBean;
import java.util.List;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DirectorySyncJob.
 */
public class DirectorySyncJob implements Job {

  private static final Logger logger = LoggerFactory.getLogger(DirectorySyncJob.class);
  private final FhirContext ctx = FhirContext.forR4();

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    try {
      syncWithDirectory();
    } catch (Exception e) {
      throw new JobExecutionException(e);
    }
  }

  private void syncWithDirectory() {
    Sync sync = ApplicationBean.getSync();
    List<OperationOutcome> operationOutcomes = sync.syncCollectionSizesToDirectory();
    for (OperationOutcome oo : operationOutcomes) {
      logger.debug(ctx.newJsonParser().encodeResourceToString(oo));
    }
    List<OperationOutcome> oo = sync.updateAllBiobanksOnFhirServerIfNecessary();
    for (OperationOutcome operationOutcomeTmp : oo) {
      logger.debug(ctx.newJsonParser().encodeResourceToString(operationOutcomeTmp));
    }
  }

}

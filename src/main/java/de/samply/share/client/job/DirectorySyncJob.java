package de.samply.share.client.job;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import de.samply.directory_sync.Sync;
import de.samply.directory_sync.directory.DirectoryApi;
import de.samply.directory_sync.directory.DirectoryService;
import de.samply.directory_sync.fhir.FhirApi;
import de.samply.directory_sync.fhir.FhirReporting;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.util.db.CredentialsUtil;
import io.vavr.control.Either;
import java.io.IOException;
import java.util.List;
import javax.security.auth.login.CredentialNotFoundException;
import org.apache.http.impl.client.HttpClients;
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

  private void syncWithDirectory() throws CredentialNotFoundException, IOException {
    DirectoryApi directoryApi = createDirectoryApi().get();
    DirectoryService directoryService = new DirectoryService(directoryApi);
    FhirApi fhirApi = createFhirApi();
    FhirReporting fhirReporting = new FhirReporting(ctx, fhirApi);
    Sync sync = new Sync(fhirApi, fhirReporting, directoryApi, directoryService);
    List<OperationOutcome> operationOutcomes = sync.syncCollectionSizesToDirectory();
    for (OperationOutcome oo : operationOutcomes) {
      logger.debug(ctx.newJsonParser().encodeResourceToString(oo));
    }
    List<OperationOutcome> oo = sync.updateAllBiobanksOnFhirServerIfNecessary();
    for (OperationOutcome operationOutcomeTmp : oo) {
      logger.debug(ctx.newJsonParser().encodeResourceToString(operationOutcomeTmp));
    }
  }

  private Either<OperationOutcome, DirectoryApi> createDirectoryApi()
      throws CredentialNotFoundException, IOException {
    List<Credentials> credentialsList = CredentialsUtil
        .getCredentialsByTarget(TargetType.TT_DIRECTORY);
    if (credentialsList.size() < 1) {
      throw new CredentialNotFoundException("No directory credentials found");
    }
    Credentials credentials = credentialsList.get(0);
    return DirectoryApi.createWithLogin(HttpClients.createDefault(),
        ApplicationBean.getUrlsForDirectory().getDirectoryUrl(), credentials.getUsername(),
        credentials.getPasscode());
  }

  private FhirApi createFhirApi() {
    IGenericClient client = ctx
        .newRestfulGenericClient(ApplicationBean.getUrlsForDirectory().getLdmUrl());
    client.registerInterceptor(new LoggingInterceptor(true));
    return new FhirApi(client);
  }
}

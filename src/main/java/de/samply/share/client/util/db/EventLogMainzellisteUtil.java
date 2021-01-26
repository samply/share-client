package de.samply.share.client.util.db;

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.fhir.FhirResource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;

/**
 * An event log for the outcome of the communication with Mainzelliste in nNGM.
 */
public class EventLogMainzellisteUtil {

  private static final String TYPE_CODING_REST = "rest";
  private static final String TYPE_CODING_DISPLAY_RESTFUL_OPERATION = "RESTful Operation";
  private static final String TYPE_CODING_SYSTEM = "http://terminology.hl7.org/CodeSystem/audit-event-type";
  private static final String OBSERVER_DISPLAY = "check the data provider";

  /**
   * Todo.
   *
   * @param userId     the cts User-Id
   * @param statusCode statusCode of the http response
   * @return FHIR-AuditEvent in JSON
   */
  public static String getFhirAuditEvent(String userId, int statusCode) {
    AuditEvent auditEvent = new AuditEvent();
    auditEvent.setId(UUID.randomUUID().toString());
    Coding typeCoding = new Coding();
    typeCoding.setSystem(TYPE_CODING_SYSTEM);
    typeCoding.setCode(TYPE_CODING_REST);
    typeCoding.setDisplay(TYPE_CODING_DISPLAY_RESTFUL_OPERATION);
    auditEvent.setType(typeCoding);
    auditEvent.setAction(AuditEvent.AuditEventAction.C);
    auditEvent.setRecorded(new Date());
    int eventCode = getEventCodeFromStatusCode(statusCode);
    auditEvent.setOutcome(AuditEvent.AuditEventOutcome.fromCode(String.valueOf(eventCode)));
    auditEvent.setOutcomeDesc(String.valueOf(statusCode));
    List<AuditEvent.AuditEventAgentComponent> auditEventAgentComponentsList = new ArrayList<>();
    AuditEvent.AuditEventAgentComponent auditEventAgentComponent =
        new AuditEvent.AuditEventAgentComponent();
    auditEventAgentComponent.setAltId(userId);
    auditEventAgentComponent.setRequestor(true);
    auditEventAgentComponentsList.add(auditEventAgentComponent);
    auditEvent.setAgent(auditEventAgentComponentsList);
    Reference observer = new Reference();
    String siteName = ApplicationBean.getBridgeheadInfos().getName();
    if (siteName != null && !siteName.isEmpty()) {
      observer.setDisplay(siteName);
    } else {
      observer.setDisplay(OBSERVER_DISPLAY);
    }
    auditEvent.setSource(new AuditEvent.AuditEventSourceComponent(observer));
    FhirResource fhirResource = new FhirResource();
    return fhirResource.convertAuditEventToJson(auditEvent);
  }

  /**
   * Todo.
   *
   * @param statusCode statusCode from the http response
   * @return -https://terminology.hl7.org/1.0.0/ValueSet-audit-event-outcome.html
   */
  private static int getEventCodeFromStatusCode(int statusCode) {
    if (statusCode == 200 || statusCode == 201) {
      return 0; //Success
    }
    if ((statusCode >= 500 && statusCode < 600) || statusCode == 401 || statusCode == 404) {
      return 8; //Serious failure
    }
    return 4; //Minor failure
  }
}

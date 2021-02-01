package de.samply.share.client.control;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.model.centralsearch.PatientUploadResult;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.AbstractLdmConnectorView;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.CentralSearchConnector;
import de.samply.share.client.util.connector.IdManagerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCql;
import de.samply.share.client.util.connector.LdmPostQueryParameterCql;
import de.samply.share.client.util.connector.LdmPostQueryParameterView;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.CentralSearchConnectorException;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.connector.idmanagement.utils.IdManagementUtils;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Case;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.Sample;
import de.samply.share.model.common.Inquiry;
import de.samply.share.model.cql.CqlQuery;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * This class holds methods to perform connectivity tests as well as function tests.
 */
@ManagedBean(name = "testsBean")
@ViewScoped
public class TestsBean implements Serializable {

  private CheckResult idManagerCheckResult;
  private CheckResult ldmCheckResult;
  private Map<Integer, CheckResult> brokerCheckResults;
  private CheckResult centralMdsDbCheckResult;

  private Map<Integer, CheckResult> retrieveTestInquiryCheckResults;
  private Map<Integer, CheckResult> retrieveAndExecuteTestInquiryCheckResults;
  private CheckResult retrieveExportIdsCheckResult;
  private CheckResult uploadAndDeleteDummyPatientCheckResult;

  private String localIdToCheck;

  /**
   * Create a TestBean for the incoming test results.
   */
  public TestsBean() {
    brokerCheckResults = new HashMap<>();
    retrieveTestInquiryCheckResults = new HashMap<>();
    retrieveAndExecuteTestInquiryCheckResults = new HashMap<>();
  }

  public CheckResult getIdManagerCheckResult() {
    return idManagerCheckResult;
  }

  public void setIdManagerCheckResult(CheckResult idManagerCheckResult) {
    this.idManagerCheckResult = idManagerCheckResult;
  }

  public CheckResult getLdmCheckResult() {
    return ldmCheckResult;
  }

  public void setLdmCheckResult(CheckResult ldmCheckResult) {
    this.ldmCheckResult = ldmCheckResult;
  }

  public Map<Integer, CheckResult> getBrokerCheckResults() {
    return brokerCheckResults;
  }

  public void setBrokerCheckResults(Map<Integer, CheckResult> brokerCheckResults) {
    this.brokerCheckResults = brokerCheckResults;
  }

  public CheckResult getCentralMdsDbCheckResult() {
    return centralMdsDbCheckResult;
  }

  public void setCentralMdsDbCheckResult(CheckResult centralMdsDbCheckResult) {
    this.centralMdsDbCheckResult = centralMdsDbCheckResult;
  }

  public Map<Integer, CheckResult> getRetrieveTestInquiryCheckResults() {
    return retrieveTestInquiryCheckResults;
  }

  public void setRetrieveTestInquiryCheckResults(
      Map<Integer, CheckResult> retrieveTestInquiryCheckResults) {
    this.retrieveTestInquiryCheckResults = retrieveTestInquiryCheckResults;
  }

  public Map<Integer, CheckResult> getRetrieveAndExecuteTestInquiryCheckResults() {
    return retrieveAndExecuteTestInquiryCheckResults;
  }

  public void setRetrieveAndExecuteTestInquiryCheckResults(
      Map<Integer, CheckResult> retrieveAndExecuteTestInquiryCheckResults) {
    this.retrieveAndExecuteTestInquiryCheckResults = retrieveAndExecuteTestInquiryCheckResults;
  }

  public CheckResult getRetrieveExportIdsCheckResult() {
    return retrieveExportIdsCheckResult;
  }

  public void setRetrieveExportIdsCheckResult(CheckResult retrieveExportIdsCheckResult) {
    this.retrieveExportIdsCheckResult = retrieveExportIdsCheckResult;
  }

  public CheckResult getUploadAndDeleteDummyPatientCheckResult() {
    return uploadAndDeleteDummyPatientCheckResult;
  }

  public void setUploadAndDeleteDummyPatientCheckResult(
      CheckResult uploadAndDeleteDummyPatientCheckResult) {
    this.uploadAndDeleteDummyPatientCheckResult = uploadAndDeleteDummyPatientCheckResult;
  }

  public String getLocalIdToCheck() {
    return localIdToCheck;
  }

  public void setLocalIdToCheck(String localIdToCheck) {
    this.localIdToCheck = localIdToCheck;
  }

  /**
   * Perform an HTTP GET to the configured ID Manager URL.
   */
  public void performIdManagerCheck() {
    IdManagerConnector idManagerConnector = new IdManagerConnector();
    idManagerCheckResult = idManagerConnector.checkConnection();
  }

  /**
   * Perform an HTTP GET on the info resource on the local datamanagement.
   */
  public void performLdmCheck() {
    LdmConnector ldmConnector = ApplicationBean.getLdmConnector();
    ldmCheckResult = ldmConnector.checkConnection();
  }

  /**
   * Perform an HTTP GET to the root resource of a search broker.
   *
   * @param brokerId the db id of the broker to check
   */
  public void performBrokerCheck(int brokerId) {
    Broker broker = BrokerUtil.fetchBrokerById(brokerId);
    BrokerConnector brokerConnector = new BrokerConnector(broker);
    CheckResult checkResult = brokerConnector.checkConnection();
    brokerCheckResults.put(brokerId, checkResult);
  }

  /**
   * Perform an HTTP GET for the upload stats of this instance at central MDS db.
   */
  public void performCentralMdsDbCheck() {
    CentralSearchConnector centralSearchConnector = new CentralSearchConnector();
    centralMdsDbCheckResult = centralSearchConnector.checkConnection();
  }

  /**
   * Get an example inquiry from the searchbroker.
   *
   * @param brokerId the db id of the broker to check
   */
  public void performRetrieveTestInquiryCheck(int brokerId) {
    Broker broker = BrokerUtil.fetchBrokerById(brokerId);
    BrokerConnector brokerConnector = new BrokerConnector(broker);
    CheckResult checkResult = new CheckResult();
    try {
      brokerConnector.getTestInquiry(checkResult);
    } catch (BrokerConnectorException e) {
      checkResult.setSuccess(false);
      checkResult.getMessages()
          .add(new Message("BrokerConnectorException caught. Cause: "
              + e.getMessage(), "fa-bolt"));
    }
    retrieveTestInquiryCheckResults.put(brokerId, checkResult);
  }

  /**
   * Get an example inquiry from the searchbroker and post it to local datamanagement without
   * persisting it.
   *
   * @param brokerId the db id of the broker to check
   */
  public void performRetrieveAndExecuteTestInquiryCheck(int brokerId) {
    Broker broker = BrokerUtil.fetchBrokerById(brokerId);
    BrokerConnector brokerConnector = new BrokerConnector(broker);
    CheckResult checkResult = new CheckResult();
    Inquiry testInquiry;
    try {
      testInquiry = brokerConnector.getTestInquiry(checkResult);
    } catch (BrokerConnectorException e) {
      checkResult.setSuccess(false);
      checkResult.getMessages()
          .add(new Message("BrokerConnectorException caught. Cause: "
              + e.getMessage(), "fa-bolt"));
      retrieveAndExecuteTestInquiryCheckResults.put(brokerId, checkResult);
      return;
    }

    String location = "";
    try {
      if (ApplicationUtils.isLanguageQuery()) {
        AbstractLdmConnectorView<?, ?, ?, ?, ?> ldmConnector =
            (AbstractLdmConnectorView<?, ?, ?, ?, ?>) ApplicationBean.getLdmConnector();
        LdmPostQueryParameterView parameter = new LdmPostQueryParameterView(true,
            null, true, true);
        location = ldmConnector.postQuery(testInquiry.getQuery(), parameter);
      } else if (ApplicationUtils.isLanguageCql()) {
        LdmConnectorCql ldmConnector = (LdmConnectorCql) ApplicationBean.getLdmConnector();
        CqlQuery cqlQuery = testInquiry.getCqlQueryList().getQueries().get(0);
        LdmPostQueryParameterCql ldmPostQueryParameterCql = new LdmPostQueryParameterCql(
            true, cqlQuery.getEntityType());
        location = ldmConnector.postQuery(cqlQuery.getCql(), ldmPostQueryParameterCql);
      }
    } catch (Exception e) {
      checkResult.setSuccess(false);
      checkResult.getMessages().add(new Message(
          "Exception caught while trying to post to local datamanagement: " + e.getMessage(),
          "fa-bolt"));
      retrieveAndExecuteTestInquiryCheckResults.put(brokerId, checkResult);
      return;
    }
    if (!SamplyShareUtils.isNullOrEmpty(location)) {
      checkResult.setSuccess(true);
      checkResult.getMessages().add(new Message("Inquiry accepted at: "
          + location, "fa-check"));
    } else {
      checkResult.setSuccess(false);
      checkResult.getMessages().add(new Message("Got no result", "fa-bolt"));
    }

    retrieveAndExecuteTestInquiryCheckResults.put(brokerId, checkResult);
  }

  /**
   * Get the export id for a (user-entered) local id from the id manager.
   */
  public void performRetrieveExportIdCheck() {
    retrieveExportIdsCheckResult = new CheckResult();
    retrieveExportIdsCheckResult.setExecutionDate(new Date());
    String instanceId = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID);
    IdObject idObject = new IdObject(instanceId, localIdToCheck);
    retrieveExportIdsCheckResult.getMessages()
        .add(new Message("Try to get Export ID for: " + idObject, "fa-info"));
    HashMap<String, IdObject> myMap = new HashMap<>();
    myMap.put(localIdToCheck, idObject);
    IdManagerConnector idManagerConnector = new IdManagerConnector();
    try {
      Map<String, String> exportIds = idManagerConnector.getExportIds(myMap);
      if (SamplyShareUtils.isNullOrEmpty(exportIds)) {
        retrieveExportIdsCheckResult.setSuccess(false);
        retrieveExportIdsCheckResult.getMessages()
            .add(new Message("Retrieved Map is empty or null", "fa-bolt"));
      } else {
        retrieveExportIdsCheckResult.setSuccess(true);
        retrieveExportIdsCheckResult.getMessages().add(
            new Message("Got export id => " + exportIds.get(localIdToCheck),
                "fa-long-arrow-left"));
      }
    } catch (IdManagerConnectorException e) {
      retrieveExportIdsCheckResult.setSuccess(false);
      retrieveExportIdsCheckResult.getMessages()
          .add(new Message("Exception caught: " + e.getMessage(), "fa-bolt"));
    }
  }

  /**
   * Check the upload to the central mds db. 1) Create a very basic dummy patient 2) Upload it with
   * an uniquely prefixed id 3) Delete it from the central mds db
   */
  public void performUploadAndDeleteDummyPatientCheck() {
    uploadAndDeleteDummyPatientCheckResult = new CheckResult();
    uploadAndDeleteDummyPatientCheckResult.setExecutionDate(new Date());
    uploadAndDeleteDummyPatientCheckResult.setSuccess(false);

    CentralSearchConnector centralSearchConnector = new CentralSearchConnector();
    Patient patient = createDummyPatient();
    uploadAndDeleteDummyPatientCheckResult.getMessages().add(
        new Message("Created Dummy Patient: " + centralSearchConnector.marshalPatient(patient),
            "fa-code"));

    String instanceId = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID);
    String prefix = instanceId + "_UPLOADTEST_";
    String exportId = Utils
        .getRandomExportid(prefix, IdManagementUtils.CENTRAL_MDS_DB_PUBKEY_FILENAME);

    if (SamplyShareUtils.isNullOrEmpty(exportId)) {
      uploadAndDeleteDummyPatientCheckResult.getMessages()
          .add(new Message("Could not generate export id", "fa-bolt"));
      return;
    } else {
      uploadAndDeleteDummyPatientCheckResult.getMessages()
          .add(new Message("Generated export id: " + exportId, "fa-check"));
    }

    try {
      patient.setId(exportId);
      PatientUploadResult patientUploadResult = centralSearchConnector.uploadPatient(patient);
      if (!patientUploadResult.isSuccess()) {
        uploadAndDeleteDummyPatientCheckResult.getMessages()
            .add(new Message("Upload failed: " + patientUploadResult, "fa-bolt"));
      } else {
        uploadAndDeleteDummyPatientCheckResult.setSuccess(true);
        uploadAndDeleteDummyPatientCheckResult.getMessages().add(
            new Message("Upload ok, got status code " + patientUploadResult.getStatus(),
                "fa-check"));
      }
    } finally {
      try {
        int statusCode = centralSearchConnector.deletePatients(prefix);
        uploadAndDeleteDummyPatientCheckResult.getMessages().add(new Message(
            "Delete Patients with prefix \"" + prefix + "\" returned status code "
                + statusCode, "fa-exchange"));
      } catch (CentralSearchConnectorException e) {
        uploadAndDeleteDummyPatientCheckResult.getMessages()
            .add(new Message("Caught exception: " + e.getMessage(), "fa-bolt"));
      }
    }
  }

  /**
   * Create and return a dummy patient. It has one case and one sample with one attribute each.
   *
   * @return the created dummy patient
   */
  private Patient createDummyPatient() {
    ObjectFactory objectFactory = new ObjectFactory();
    Case caseCcp = new Case();

    caseCcp.setId("case1");
    Attribute caseAttribute = new Attribute();
    caseAttribute.setMdrKey("urn:dktk:dataelement:1:3");
    caseAttribute.setValue(objectFactory.createValue("M"));
    caseCcp.getAttribute().add(caseAttribute);

    Sample sample = new Sample();
    sample.setId("sample1");
    Attribute sampleAttribute = new Attribute();
    sampleAttribute.setMdrKey("urn:dktk:dataelement:97:1");
    sampleAttribute.setValue(objectFactory.createValue("DNA"));
    sample.getAttribute().add(sampleAttribute);

    Patient patient = new Patient();
    patient.getCase().add(caseCcp);
    patient.getSample().add(sample);
    patient.setId("DUMMY_UPLOAD_PATIENT");
    return patient;
  }

}

/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.job;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import de.samply.dktk.converter.PatientConverter;
import de.samply.dktk.converter.PatientConverterUtil;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.ExecuteInquiryJobParams;
import de.samply.share.client.job.params.UploadJobParams;
import de.samply.share.client.job.util.InquiryCriteriaFactory;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.EnumConfigurationTimings;
import de.samply.share.client.model.IdObject;
import de.samply.share.client.model.centralsearch.DateRestriction;
import de.samply.share.client.model.centralsearch.PatientUploadResult;
import de.samply.share.client.model.centralsearch.QueryResultWithIdMap;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.UploadStatusType;
import de.samply.share.client.model.db.tables.pojos.*;
import de.samply.share.client.util.UploadUtils;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.CentralSearchConnector;
import de.samply.share.client.util.connector.IdManagerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.CentralSearchConnectorException;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.Attribute;
import de.samply.share.model.ccp.Patient;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.utils.QueryConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_NEW;

/**
 * This Job prepares and/or performs an upload to the central mds database, depending on the status
 *
 * It is defined and scheduled by either quartz-jobs.xml, user-triggered by the admin or by CheckInquiryStatusJob
 *
 * The performed action depends on the previous state of the Upload
 *
 * 1) If the upload is in the state 'US_NEW', create an inquiry and hand it over to an ExecuteInquiryJob
 * 2) If the state is 'US_QUERY_READY', so this is rescheduled from the CheckInquiryStatusJob, perform the actual upload
 *
 * Moreover, there are 2 (or even 3) different Upload scenarios which are called via different triggers.
 *
 * 1) Upload those patients who have given their explicit consent to the DKTK case. They will have a global DKTK ID
 *    which allows to identify them across all sites.
 * 2) Upload those patients with local consent only. They will not get a global DKTK ID and are thus not identifiable
 *    across sites. Instead, their DKTK site id is used (2a) or they will get a completely randomized id (2b)
 *    The latter requires a full upload of those patients each time, so this should be scheduled less frequently since
 *    it may cause a lot of load on the system.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class UploadToCentralMdsDbJob implements Job {

    private static final Logger logger = LogManager.getLogger(UploadToCentralMdsDbJob.class);

    public static final String CENTRAL_MDS_DB_PUBKEY_FILENAME = "mds-db-key-public.der";

    private UploadJobParams jobParams;
    private Upload upload;
    private PatientConverter patientConverter;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

        jobParams = new UploadJobParams(dataMap);

        // If this should be an upload for patients that are not dktk flagged, check if the corresponding setting
        // in the config is set...otherwise abort.
        if (!jobParams.isDktkFlaggedPatients()
                && !ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.CENTRAL_MDS_DATABASE_UPLOAD_PATIENTS_WITH_LOCAL_CONSENT)) {
            logger.info("Upload for patients without DKTK flag is disabled in config but the trigger for the job is still active. This will do no harm but should be deactivated.");
            return;
        }

        // If no upload id is set in the jobdata map, store the upload to db first. The upload will be triggered by the
        // scheduler
        if (jobParams.getUploadId() < 1) {
            jobParams.setUploadId(storeUpload(jobParams.isDktkFlaggedPatients()));
        }
        upload = UploadUtil.fetchUploadById(jobParams.getUploadId());
        CentralSearchConnector csConnector = new CentralSearchConnector();

        switch (jobParams.getStatus()) {
            case US_NEW:
                prepareUpload(csConnector);
                break;
            case US_QUERY_POSTED:
                logger.info("Query was posted to local datamanagement but is not ready yet");
                break;
            case US_QUERY_READY:
                patientConverter = new PatientConverter(ApplicationBean.getMdrClient());
                handleUpload(csConnector);
                break;
                // The cases below are (currently?) not relevant
            case US_UPLOADING:
            case US_COMPLETED:
            case US_COMPLETED_WITH_ERRORS:
            case US_LDM_ERROR:
            case US_CENTRAL_MDSDB_ERROR:
            case US_IDMANAGER_ERROR:
            case US_ABANDONED:
            default:
                break;
        }
    }

    /**
     * Write the upload object to the database
     *
     * This will only be called if the job was spawned by the scheduler
     *
     * @return the database id of the upload
     */
    private int storeUpload(boolean dktkFlagged) {
        Upload upload = new Upload();
        upload.setStatus(UploadStatusType.US_NEW);
        upload.setTriggeredAt(SamplyShareUtils.getCurrentSqlTimestamp());
        upload.setTriggeredBy("scheduler");
        upload.setIsDryrun(false);
        upload.setIsFullUpload(false);
        upload.setDktkFlagged(dktkFlagged);
        upload.setFailureCount(0);
        upload.setSuccessCount(0);
        return UploadUtil.insertUpload(upload);
    }

    /**
     * Check for previous upload times. Then create an inquiry and hand it over to an ExecuteInquiryJob
     *
     * @param csConnector connector to the central mds database
     */
    private void prepareUpload(CentralSearchConnector csConnector) throws JobExecutionException {
        try {
            DateRestriction dateRestriction = csConnector.getDateRestriction();
            int inquiryId = createAndStoreInquiry();
            upload.setTimeToSet(SamplyShareUtils.convertDateStringToSqlTimestamp(dateRestriction.getServerTime(), CentralSearchConnector.DATE_FORMAT_TARGET));
            if (upload.getIsFullUpload() || ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.CENTRAL_MDS_DATABASE_UPLOAD_RANDOMIZE_EXPORT_IDS)) {
                addInquiryDetailsAndSpawnExecutionJob(inquiryId, null);
            } else {
                addInquiryDetailsAndSpawnExecutionJob(inquiryId, dateRestriction);
            }
        } catch (CentralSearchConnectorException e) {
            throw new JobExecutionException(e);
        }
    }

    /**
     * Perform the actual upload
     *
     * Iterate through the result set (list of patients) and for each patient:
     *
     * 1) Get an export id and replace the id attribute with it (also use it in the path in the PUT command)
     *  a) Use the global dktk id if this is the upload for dktk flagged patients
     *  b1) Use the local dktk site id, if the this is for the NOT dktk flagged patients
     *  b2) Use a random string, with the prefix defined via 'CENTRAL_MDS_DATABASE_ANONYMIZED_PATIENTS_PREFIX' if both
     *     'CENTRAL_MDS_DATABASE_UPLOAD_RANDOMIZE_EXPORT_IDS' and
     *     'CENTRAL_MDS_DATABASE_SHOW_UPLOAD_PATIENTS_WITH_LOCAL_CONSENT' are true
     * 2) Transform the patient object from the source format (coming from local datamanagement) to the target format
     *    (central mds db)
     * 3) Shift attributes between containers, if they are still misplaced
     * 4) Depending on whether the dryrun flag is set
     *  a) Upload the patient to central mds db if it is not
     *  b) Write the transformed data to disk // TODO
     *
     * @param csConnector connector to the central mds database
     */
    @SuppressWarnings("unchecked")
    private void handleUpload(CentralSearchConnector csConnector) throws JobExecutionException {
        try {
            LdmConnector ldmConnector = ApplicationBean.getLdmConnector();
            int successCount = 0;
            int failCount = 0;
            List<String> failedLocalIds = new ArrayList<>();

            Inquiry inquiry = InquiryUtil.fetchLatestInquiryForUpload(upload);
            InquiryDetails inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiry.getLatestDetailsId());
            InquiryResult inquiryResult = InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId());

            if (inquiryResult == null) {
                log(EventMessageType.E_COULD_NOT_GET_RESULT);
                upload.setStatus(UploadStatusType.US_LDM_ERROR);
                UploadUtil.updateUpload(upload);
                return;
            } else if (inquiryResult.getSize() < 1) {
                log(EventMessageType.E_RESULT_EMPTY);
                upload.setStatus(UploadStatusType.US_COMPLETED);
                UploadUtil.updateUpload(upload);
                return;
            }

            upload.setStatus(UploadStatusType.US_UPLOADING);
            UploadUtil.updateUpload(upload);

            String resultLocation = inquiryResult.getLocation();
            // Now that the data is ready, either upload it, or transform it and write it to disk.
            // TODO: Check if delete command has to be sent.
            int pageCount = ldmConnector.getPageCount(resultLocation);
            for (int pageNr = 0; pageNr < pageCount; pageNr++) {
                // Load result page from local datamanagement
                // TODO: check if it is necessary to support other QueryResult implementations
                QueryResult queryResultPage = (QueryResult)ldmConnector.getResultsFromPage(resultLocation, pageNr);
                QueryResultWithIdMap queryResultWithIdMap = stageQueryResultPage(queryResultPage);
                QueryResult queryResultPageToUpload = queryResultWithIdMap.getQueryResult();
                Map<String, String> idMap = queryResultWithIdMap.getIdMap();

                // Upload / Dryrun
                if (upload.getIsDryrun()) {
                    logger.debug("Write page to disk. ", pageNr);
                    ldmConnector.writeQueryResultPageToDisk(queryResultPageToUpload, pageNr);
                    logger.debug("Done writing to disk");
                } else {
                    for (Patient patient : queryResultPageToUpload.getPatient()) {
                        int attempt = 0;
                        int maxAttempts = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.UPLOAD_RETRY_PATIENT_UPLOAD_ATTEMPTS);
                        int delay = ConfigurationUtil.getConfigurationTimingsElementValue(EnumConfigurationTimings.UPLOAD_RETRY_PATIENT_UPLOAD_INTERVAL);
                        do {
                            try {
                                PatientUploadResult uploadResult = csConnector.uploadPatient(patient);
                                if (uploadResult.getStatus() >= 200 && uploadResult.getStatus() < 300) {
                                    upload.setSuccessCount(++successCount);
                                    UploadUtil.updateUpload(upload);
                                    attempt = maxAttempts;
                                } else if (uploadResult.isRetry()) {
                                    ++attempt;
                                    TimeUnit.SECONDS.sleep(delay);
                                } else {
                                    upload.setFailureCount(++failCount);
                                    UploadUtil.updateUpload(upload);
                                    failedLocalIds.add(idMap.get(patient.getId()));
                                    attempt = maxAttempts;
                                }
                            } catch (InterruptedException e) {
                                logger.info("Exception caught while trying to upload. Trying to go on anyways.", e);
                            }
                        } while (attempt < maxAttempts);
                    }
                }
            }

            if (upload.getIsDryrun()) {
                EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_DRYRUN_COMPLETE, upload.getId());
            } else {
                // If any patient uploads failed, persist the list in the database (as json array)
                if (failCount > 0 && !SamplyShareUtils.isNullOrEmpty(failedLocalIds)) {
                    JsonArray jsonArray = new JsonArray();
                    for (String id : failedLocalIds) {
                        JsonPrimitive jsonPrimitive = new JsonPrimitive(id);
                        jsonArray.add(jsonPrimitive);
                    }
                    upload.setFailedPatients(jsonArray.toString());
                    UploadUtil.updateUpload(upload);
                }

                // Set the last upload timestamp on the central mds db
                csConnector.setLastUploadTimestamp(SamplyShareUtils.convertSqlTimestampToString(upload.getTimeToSet(), CentralSearchConnector.DATE_FORMAT_HTTP_HEADER));
                EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_UPLOAD_COMPLETE,
                        upload.getId(),
                        Integer.toString(failCount),
                        Integer.toString(successCount));
            }
            UploadUtil.setUploadStatusById(upload.getId(), (failCount > 0) ? UploadStatusType.US_COMPLETED_WITH_ERRORS : UploadStatusType.US_COMPLETED);
        } catch (LDMConnectorException | IdManagerConnectorException | CentralSearchConnectorException | IOException e) {
            throw new JobExecutionException(e);
        }
    }

    /**
     * Take one page from the query result, get export ids and re-arrange patient datasets, preparing them for the upload
     *
     * @param sourceResultPage
     *          the query result page, as received from the local datamanagement
     * @return the modified query result page - ready to upload along with the mapping of export ids to local ids
     */
    private QueryResultWithIdMap stageQueryResultPage(QueryResult sourceResultPage) throws IdManagerConnectorException {
        QueryResult preparedQueryResultPage = new QueryResult();
        preparedQueryResultPage.setId(sourceResultPage.getId());
        // Replace Patient IDs with Export IDs
        Map<String, String> idMap = getExportIds(sourceResultPage);
        for (Patient patient : sourceResultPage.getPatient()) {
            // Transform Patient from Local Datamanagement Format to Central Search Format
            patient = patientConverter.centraxxToCentralsearch(patient);
            // TODO: remove this workaround when it is agreed upon that it is not needed here any more. This has been discussed.
            patient = ApplicationBean.getPatientValidator().fixOrRemoveWrongAttributes(patient);

            String localId = patient.getId();
            patient.setId(idMap.get(localId));
            patient = PatientConverterUtil.removeAttributeFromCases(patient, ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_DKTK_GLOBAL_ID));
            preparedQueryResultPage.getPatient().add(patient);
        }

        // Switch the keys with the values, to ease access from export id to local id

        return new QueryResultWithIdMap(preparedQueryResultPage, SamplyShareUtils.reverse(idMap));
    }

    /**
     * Get export ids for each patient in the list
     *
     * If they are to be randomized, just do it here (only applies to those without DKTK Flag.
     * Otherwise get them from the id manager
     *
     * @param queryResultPage a list of patients
     * @return a map, containing the local ids of the patients as keys and the export id as values
     */
    private Map<String,String> getExportIds(QueryResult queryResultPage) throws IdManagerConnectorException {
        HashMap<String, IdObject> idsFromQueryResult = getIdsFromCcpQueryResult(queryResultPage);
        if (!jobParams.isDktkFlaggedPatients() && ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.CENTRAL_MDS_DATABASE_UPLOAD_RANDOMIZE_EXPORT_IDS)) {
            return getRandomExportIds(idsFromQueryResult);
        } else {
            IdManagerConnector idManagerConnector = new IdManagerConnector();
            return idManagerConnector.getExportIds(idsFromQueryResult);    
        }
    }

    /**
     * Generate random export ids for a list of patients
     *
     * @param idsFromQueryResult a map of local dktk site ids and id objects
     * @return a map, holding the local dktk site ids as keys and a random export id as value
     */
    private Map<String,String> getRandomExportIds(HashMap<String, IdObject> idsFromQueryResult) {
        Map<String, String> ids = new HashMap<>();
        String prefix = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CENTRAL_MDS_DATABASE_ANONYMIZED_PATIENTS_PREFIX);
        for (String s : idsFromQueryResult.keySet()) {
            ids.put(s, Utils.getRandomExportid(prefix, CENTRAL_MDS_DB_PUBKEY_FILENAME));
        }
        return ids;
    }

    /**
     * Get the ids from a list of patients
     *
     * When this upload job is for dktk flagged patients, use the global dktk id - use the dktk site id otherwise
     *
     * The IdObject, which is the value of the map consists of an idtype and a value, where the id type is always the
     * instance id, but is prefixed with the network id (here: dktk) for the global dktk id
     *
     * @param queryResult the list of patients
     * @return a map with the local patient id as key and an IdObject as value
     */
    private HashMap<String,IdObject> getIdsFromCcpQueryResult(QueryResult queryResult) {
        HashMap<String, IdObject> ids = new HashMap<>();
        MdrIdDatatype mdrKeyDktkGlobal = new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_DKTK_GLOBAL_ID));
        String networkId = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.ID_MANAGER_NETWORK_ID);
        String instanceId = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.ID_MANAGER_INSTANCE_ID);

        for (Patient patient : queryResult.getPatient()) {
            if (jobParams.isDktkFlaggedPatients()) {
                for (Attribute attribute : patient.getAttribute()) {
                    MdrIdDatatype attributeMdrKey = new MdrIdDatatype(attribute.getMdrKey());
                    if (attributeMdrKey.equalsIgnoreVersion(mdrKeyDktkGlobal) && attribute.getValue() != null && !SamplyShareUtils.isNullOrEmpty(attribute.getValue().getValue())) {
                        IdObject idObject = new IdObject(networkId + "_" + instanceId, attribute.getValue().getValue());
                        ids.put(patient.getId(), idObject);
                        break;
                    }
                }
            } else {
                IdObject idObject = new IdObject(instanceId, patient.getId());
                ids.put(patient.getId(), idObject);
            }
        }
        return ids;
    }

    /**
     * Write a message, linked with the upload, to the event log
     *
     * @param message the message to log
     */
    private void log(String message) {
        EventLogUtil.insertEventLogEntryForUploadId(message, upload.getId());
    }

    /**
     * Write a message, linked with the upload, to the event log
     *
     * @param messageType pre-defined event type
     * @param params parameters that will be substituted via resource bundle and messageformat
     */
    private void log(EventMessageType messageType, String... params) {
        EventLogUtil.insertEventLogEntryForUploadId(messageType, upload.getId(), params);
    }

    /**
     * Create a new inquiry object and store it in the database
     *
     * Link it with this upload and set the broker id to null to differentiate between the two kinds of inquiries
     *
     * @return the database id of the new inquiry object
     */
    private int createAndStoreInquiry() {
        Inquiry inquiry = new Inquiry();
        inquiry.setUploadId(upload.getId());
        inquiry.setSourceId(upload.getId()); // This is a duplication, since we basically don't have a source id for uploads but it has a not null constraint
        inquiry.setBrokerId(null); // Make sure that broker_id is null, so that the constraint on the table is not violated
        inquiry.setLabel("upload_" + upload.getId());
        int inquiryId = InquiryUtil.insertInquiry(inquiry);

        EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_NEW_INQUIRY_CREATED_FOR_UPLOAD, upload.getId());
        return inquiryId;
    }

    /**
     * Create a view, store the inquiry details and hand it over to an ExecuteInquiryJob
     *
     * @param inquiryId the database id of the inquiry
     * @param dateRestriction upper and lower bounds for the patients to include. may be null for no restrictions
     */
    private void addInquiryDetailsAndSpawnExecutionJob(int inquiryId, DateRestriction dateRestriction) {
        try {
            InquiryDetails inquiryDetails = new InquiryDetails();
            inquiryDetails.setInquiryId(inquiryId);
            inquiryDetails.setRevision(1);
            Utils.setStatus(inquiryDetails, IS_NEW);

            int inquiryDetailsId = InquiryDetailsUtil.insertInquiryDetails(inquiryDetails);
            // Reload with id and received_at timestamp
            inquiryDetails = InquiryDetailsUtil.fetchInquiryDetailsById(inquiryDetailsId);

            addInquiryCriteria(dateRestriction, inquiryDetailsId);

            logger.info("Spawn ExecuteInquiryJob for upload with id " + upload.getId());
            spawnNewInquiryExecutionJob(inquiryDetails);
        } catch (Exception e) {
            logger.error("Exception caught while trying to add inquiry details", e);
        }
    }

    private void addInquiryCriteria(DateRestriction dateRestriction, int detailsId) throws JAXBException {
        if (ApplicationUtils.isLanguageCql()) {
            addInquiryCriteriaCql(dateRestriction, detailsId);
        }

        if (ApplicationUtils.isLanguageQuery()) {
            addInquiryCriteriaQuery(dateRestriction, detailsId);
        }
    }

    private void addInquiryCriteriaQuery(DateRestriction dateRestriction, int detailsId) throws JAXBException {
        InquiryCriteria inquiryCriteria = new InquiryCriteriaFactory().createForViewQuery(detailsId);

        String criteria = QueryConverter.queryToXml(UploadUtils.createUploadQuery(dateRestriction, jobParams.isDktkFlaggedPatients()));
        inquiryCriteria.setCriteriaOriginal(criteria);

        InquiryCriteriaUtil.insertInquiryCriteria(inquiryCriteria);
    }

    private void addInquiryCriteriaCql(DateRestriction dateRestriction, int detailsId) {
        // TODO: Implement CQL queries for Patient and Specimen
    }

    /**
     * Spawn a new InquiryExecutionJob for the inquiry belonging to this upload
     *
     * @param inquiryDetails the inquiry details belonging to this upload's inquiry
     */
    private void spawnNewInquiryExecutionJob(InquiryDetails inquiryDetails) {
        try {
            Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());

            // Get the (durable) Job from the scheduler
            JobKey jobKey = JobKey.jobKey(ExecuteInquiryJobParams.getJobName(), ExecuteInquiryJobParams.JOBGROUP);

            // Fill the JobDataMap
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_ID, inquiry.getId());
            jobDataMap.put(ExecuteInquiryJobParams.INQUIRY_DETAILS_ID, inquiryDetails.getId());
            jobDataMap.put(ExecuteInquiryJobParams.STATS_ONLY, false);
            jobDataMap.put(ExecuteInquiryJobParams.IS_UPLOAD, true);

            // Fire exactly once - right now
            logger.info("Give Execute Job to scheduler for inquiry with id " + inquiry.getId());
            ApplicationBean.getScheduler().getListenerManager().addJobListener(
                    new ExecuteInquiryStatusJobListener(jobKey.getGroup() + "_listener"), KeyMatcher.keyEquals(jobKey));
            ApplicationBean.getScheduler().triggerJob(jobKey, jobDataMap);
            upload.setStatus(UploadStatusType.US_QUERY_POSTED);
            UploadUtil.updateUpload(upload);
        } catch (SchedulerException e) {
            logger.error("Error spawning Inquiry Execution Job", e);
        }
    }

}

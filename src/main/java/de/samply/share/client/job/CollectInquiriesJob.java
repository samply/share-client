package de.samply.share.client.job;

import de.samply.share.client.model.EnumInquiryPresent;
import de.samply.share.client.model.db.enums.EntityType;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.*;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Inquiry;
import de.samply.share.utils.QueryConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;

import javax.xml.bind.*;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.*;

import static de.samply.share.client.model.EnumInquiryPresent.IP_DIFFERENT_REVISION;
import static de.samply.share.client.model.EnumInquiryPresent.IP_SAME_REVISION;
import static de.samply.share.client.model.EnumInquiryPresent.IP_UNAVAILABLE;
import static de.samply.share.client.model.db.enums.InquiryStatusType.IS_NEW;

/**
 * This Job collects inquiries from all connected searchbrokers
 *
 * It is defined and scheduled in the quartz-jobs.xml
 *
 * The basic steps it performs are:
 *
 * 1) Get a list of inquiries and their revision numbers from each broker
 * 2) Store any new inquiries (or newer revisions of already known inquiries) in the database
 *
 * Further handling of the inquiry is done in the ExecuteInquiriesJob
 */
@DisallowConcurrentExecution
public class CollectInquiriesJob implements Job {

    private static final Logger logger = LogManager.getLogger(CollectInquiriesJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        List<Broker> brokers = BrokerUtil.fetchBrokers();
        for (Broker broker : brokers) {
            BrokerConnector brokerConnector = new BrokerConnector(broker);

            Credentials credentials = brokerConnector.getCredentials();

            if (credentials == null || SamplyShareUtils.isNullOrEmpty(credentials.getPasscode())) {
                logger.warn("Credentials missing for broker id " + broker.getId());
                continue;
            }
            try {
                Map<String, String> inquiries = brokerConnector.getInquiryList();
                loadAndPersistInquiries(brokerConnector, inquiries);
            } catch (JAXBException e) {
                logger.error("Error loading and/or persisting inquiries", e);
            } catch (BrokerConnectorException e) {
                logger.warn("Could not connect to " + broker.getAddress());
            }
        }
    }

    /**
     * Load and persist inquiries for a given searchbroker.
     *
     * @param brokerConnector      the broker
     * @param inquiryIdAndRevision the inquiry id and revision
     */
    private void loadAndPersistInquiries(BrokerConnector brokerConnector, Map<String, String> inquiryIdAndRevision) throws BrokerConnectorException, JAXBException {
        Iterator<Map.Entry<String, String>> it = inquiryIdAndRevision.entrySet().iterator();
        Broker broker = brokerConnector.getBroker();
        int brokerId = broker.getId();

        while (it.hasNext()) {
            Map.Entry pairs = it.next();
            String inquiryIdString = pairs.getKey().toString();
            String revision = pairs.getValue().toString();
            Inquiry inquiry;
            int contactId;

            int inquiryId;
            try {
                inquiryId = Integer.parseInt(inquiryIdString);
            } catch (NumberFormatException e) {
                inquiryId = 0;
            }

            switch (isInquiryAlreadyInDb(brokerId, inquiryId, revision)) {
                case IP_UNAVAILABLE:
                    inquiry = brokerConnector.getInquiry(inquiryId);
                    if (inquiry == null) {
                        logger.error("Could not get Inquiry " + inquiryId + " from broker " + brokerId);
                        break;
                    }
                    int inquiryDbId = storeInquiry(inquiry, broker);
                    contactId = ContactUtil.insertContact(inquiry.getAuthor());
                    addInquiryDetails(inquiry, brokerId, inquiryDbId, contactId);
                    break;
                case IP_DIFFERENT_REVISION:
                    inquiry = brokerConnector.getInquiry(inquiryId);
                    if (inquiry == null) {
                        logger.error("Could not get Inquiry " + inquiryId + " from broker " + brokerId);
                        break;
                    }
                    contactId = ContactUtil.insertContact(inquiry.getAuthor());
                    addInquiryDetails(inquiry, brokerId, -1, contactId);
                    break;
                case IP_SAME_REVISION:
                default:
                    break;
            }
            it.remove();
        }
    }

    /**
     * Checks if inquiry is already in db.
     *
     * @param brokerId  the broker id
     * @param inquiryId the inquiry id
     * @param revision  the revision
     * @return <code>IP_SAME_REVISION</code> if the exact same inquiry is already stored
     *         <code>IP_DIFFERENT_REVISION</code> if the inquiry is stored in a different revision
     *         <code>IP_UNAVAILABLE</code> if the inquiry is not stored at all yet
     */
    private EnumInquiryPresent isInquiryAlreadyInDb(int brokerId, int inquiryId, String revision) {
        try {
            de.samply.share.client.model.db.tables.pojos.Inquiry inquiry = InquiryUtil.fetchInquiryBySourceIdAndBrokerId(inquiryId, brokerId);
            if (inquiry == null) {
                return IP_UNAVAILABLE;
            } else {
                InquiryDetails inquiryDetails = InquiryDetailsUtil.getInquiryDetailsForInquiryWithRevision(inquiry, Integer.parseInt(revision));
                if (inquiryDetails == null) {
                    return IP_DIFFERENT_REVISION;
                } else {
                    return IP_SAME_REVISION;
                }
            }
        } catch (NumberFormatException e) {
            logger.error("Could not parse as integer...");
            return IP_UNAVAILABLE;
        }
    }

    /**
     * Persist inquiry without result.
     *
     * @param incomingInquiry the incoming inquiry
     * @param broker        the broker from which the inquiry was loaded
     * @return the inquiry id (as in the database)
     */
    private int storeInquiry(Inquiry incomingInquiry, Broker broker) {
        de.samply.share.client.model.db.tables.pojos.Inquiry inquiry = new de.samply.share.client.model.db.tables.pojos.Inquiry();
        inquiry.setLabel(incomingInquiry.getLabel());
        inquiry.setDescription(incomingInquiry.getDescription());
        inquiry.setBrokerId(broker.getId());
        inquiry.setUploadId(null); // Make sure that upload_id is null, so that the constraint on the table is not violated
        inquiry.setSourceId(Integer.parseInt(incomingInquiry.getId()));
        int inquiryId = InquiryUtil.insertInquiry(inquiry);

        for (String requestedEntity : incomingInquiry.getSearchFor()) {
            RequestedEntity re;
            String reLower = requestedEntity.toLowerCase();
            if (reLower.contains("biomaterial")) {
                re = RequestedEntityUtil.getRequestedEntityForValue(EntityType.E_BIOMATERIAL);
            } else if (reLower.contains("klinische")) {
                re = RequestedEntityUtil.getRequestedEntityForValue(EntityType.E_CLINICAL_DATA);
            } else if (reLower.contains("patienten")) {
                re = RequestedEntityUtil.getRequestedEntityForValue(EntityType.E_PATIENT_FOR_STUDY);
            } else if (SamplyShareUtils.isNullOrEmpty(reLower)) {
                logger.debug("No specific entity requested");
                re = RequestedEntityUtil.getRequestedEntityForValue(EntityType.E_BIOMATERIAL);
            } else {
                logger.warn("Unknown requested entity: " + requestedEntity);
                //TODO: Check if UNKNOWN should be default
                re = RequestedEntityUtil.getRequestedEntityForValue(EntityType.UNKNOWN);
            }
            if (re != null) {
                RequestedEntityUtil.insertInquiryIdRequestedEntity(inquiryId, re);
            }
        }

        EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_NEW_INQUIRY_RECEIVED, inquiryId, broker.getAddress());

        return inquiryId;
    }

    /**
     * Add another inquiryDetails entry to the given inquiry
     *
     * @param incomingInquiry the incoming inquiry
     * @param brokerId        the broker id
     * @param inquiryDbId     if known, the inquiry id in db. -1 else
     */
    private void addInquiryDetails(Inquiry incomingInquiry, int brokerId, int inquiryDbId, int contactId) throws JAXBException {
        String originalCriteria = QueryConverter.queryToXml(incomingInquiry.getQuery());

        Date now = new Date();
        long time = now.getTime();

        de.samply.share.client.model.db.tables.pojos.Inquiry inquiry = InquiryUtil.fetchInquiryBySourceIdAndBrokerId(Integer.parseInt(incomingInquiry.getId()), brokerId);
        if (inquiryDbId < 0) {
            inquiryDbId = inquiry.getId();
        }
        try {
            InquiryDetails inquiryDetails = new InquiryDetails();
            inquiryDetails.setInquiryId(inquiryDbId);
            inquiryDetails.setContactId(contactId);
            inquiryDetails.setCriteriaOriginal(originalCriteria);
            inquiryDetails.setExposeLocation(incomingInquiry.getExposeURL());
            inquiryDetails.setRevision(Integer.parseInt(incomingInquiry.getRevision()));
            inquiryDetails.setReceivedAt(new Timestamp(time));
            inquiryDetails.setStatus(IS_NEW);

            InquiryDetailsUtil.insertInquiryDetails(inquiryDetails);
        } catch (Exception e) {
            logger.error("Exception caught while trying to add inquiry details", e);
        }
    }
}

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

package de.samply.share.client.util.connector;

import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.centralsearch.DateRestriction;
import de.samply.share.client.model.centralsearch.PatientUploadResult;
import de.samply.share.client.model.centralsearch.UploadStats;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.util.Utils;
import de.samply.share.client.util.connector.exception.CentralSearchConnectorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.ccp.ObjectFactory;
import de.samply.share.model.ccp.Patient;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A connector that handles all communication with the central MDS database
 */
public class CentralSearchConnector {

    private static final Logger logger = LogManager.getLogger(CentralSearchConnector.class);

    /** The path where the statistics of the last upload can be retrieved (central mds db) */
    private static final String PATH_UPLOAD_STATS = "uploadStats";
    private static final String DEFAULT_LAST_UPDATE_DATE = "Wed, 16 Nov 1994 08:12:31 CET";

    private static final String PATH_PATIENTS = "pats";

    /** The path to specify the prefix of patients to delete from the central mds db */
    private static final String PATH_PATS_PREFIX = "patsPrefix";

    public static final DateFormat DATE_FORMAT_HTTP_HEADER;
    public static final DateFormat DATE_FORMAT_TARGET;

    private static Marshaller patientMarshaller;

    private transient HttpConnector httpConnector;
    private Integer uploadId;
    private Credentials credentials;
    private HttpHost httpHost;
    private CloseableHttpClient httpClient;
    private URL centralSearchUrl;
    private RequestConfig requestConfig;
    private String anonymizedPatientPrefix;

    static {
        DATE_FORMAT_HTTP_HEADER = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        DATE_FORMAT_TARGET = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssXXX", Locale.ENGLISH);
        try {
            final JAXBContext context = JAXBContext.newInstance(Patient.class);
            patientMarshaller = context.createMarshaller();
            patientMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
            patientMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            patientMarshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            patientMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://schema.samply.de/ccp/Patient http://schema.samply.de/ccp/Patient.xsd");
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public CentralSearchConnector() {
        List<Credentials> credentialsList = CredentialsUtil.getCredentialsByTarget(TargetType.TT_CENTRALSEARCH);
        if (SamplyShareUtils.isNullOrEmpty(credentialsList)) {
            String message = "No credentials set for central search!";
            EventLogUtil.insertEventLogEntry(EventMessageType.E_NO_CREDENTIALS_CS);
            throw new RuntimeException(message);
        } else if (credentialsList.size() > 1) {
            logger.warn("More than 1 set of credentials for central search found...using the first.");
        }
        try {
            init(credentialsList.get(0), null);
        } catch (CentralSearchConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Initialize the central search connector
     *
     * @param credentials the credentials to use
     * @param uploadId the id of the upload, if applicable
     */
    private void init(Credentials credentials, Integer uploadId) throws CentralSearchConnectorException {
        if (credentials == null) {
            throw new CentralSearchConnectorException("Central Search Credentials missing");
        }
        this.credentials = credentials;
        this.uploadId = uploadId;
        this.anonymizedPatientPrefix = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.CENTRAL_MDS_DATABASE_ANONYMIZED_PATIENTS_PREFIX);
        httpConnector = ApplicationBean.createHttpConnector();
        requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        try {
            centralSearchUrl = Utils.getCentralMdsDbURL();
            httpHost = SamplyShareUtils.getAsHttpHost(centralSearchUrl);
            httpClient = httpConnector.getHttpClient(httpHost);
        } catch (MalformedURLException e) {
            throw new CentralSearchConnectorException(e);
        }
    }

    /**
     * Get the last upload timestamp as well as the current server time
     *
     * @return last upload timestamp and current server time
     */
    public DateRestriction getDateRestriction() throws CentralSearchConnectorException {
        String responseString;

        HttpGet httpGet = new HttpGet(SamplyShareUtils.addTrailingSlash(centralSearchUrl.getPath()) + PATH_UPLOAD_STATS);
        int statusCode;
        String dateHeader;
        try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
            HttpEntity entity = response.getEntity();
            responseString = EntityUtils.toString(entity, Consts.UTF_8);
            EntityUtils.consume(entity);
            statusCode = response.getStatusLine().getStatusCode();
            dateHeader = response.getFirstHeader(HttpHeaders.DATE).getValue();
        } catch (IOException e) {
            if (uploadId != null) {
                EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_CENTRALSEARCH_COULD_NOT_CONNECT, uploadId);
            }
            throw new CentralSearchConnectorException(e);
        }

        if (statusCode == HttpStatus.SC_OK) {
            Serializer serializer = new Persister();
            UploadStats uploadStats;
            try {
                uploadStats = serializer.read(UploadStats.class, responseString);
            } catch (Exception e) {
                if (uploadId != null) {
                    EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_UPLOADSTATS_UNPARSABLE, uploadId);
                }
                throw new CentralSearchConnectorException(e);
            }

            if (uploadStats.getLastUploadTimestamp() == null || uploadStats.getLastUploadTimestamp().equalsIgnoreCase("null")) {
                if (uploadId != null) {
                    EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_NO_PREVIOUS_UPLOADS, uploadId);
                }
            } else {
                if (uploadId != null) {
                    EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_PREVIOUS_UPLOAD_AT, uploadId, uploadStats.getLastUploadTimestamp());
                }
            }
            DateRestriction dateRestriction = new DateRestriction();
            try {
                dateRestriction.setLastUpload(SamplyShareUtils.convertDateStringToString(getLastUploadTimestamp(uploadStats), DATE_FORMAT_HTTP_HEADER, DATE_FORMAT_TARGET));
                dateRestriction.setServerTime(SamplyShareUtils.convertDateStringToString(dateHeader, DATE_FORMAT_HTTP_HEADER, DATE_FORMAT_TARGET));
            } catch (ParseException e) {
                throw new CentralSearchConnectorException("Parse Exception while trying to set date restriction.", e);
            }
            return dateRestriction;
        } else if (uploadId != null) {
            EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_CS_ERROR_LASTUPLOADTIMESTAMP, uploadId, Integer.toString(statusCode));
            throw new CentralSearchConnectorException("Got status code " + statusCode + " while trying to upload patient with id " + uploadId);
        } else {
            throw new CentralSearchConnectorException("Unexpected status code received while trying to get date restrictions: " + statusCode);
        }
    }

    private String getLastUploadTimestamp (UploadStats uploadStats){

        try{

            return getLastUploadTimestamp_WithoutManagementException(uploadStats);

        } catch (Exception e){
            logger.info(e);
            return DEFAULT_LAST_UPDATE_DATE;
        }

    }

    private String getLastUploadTimestamp_WithoutManagementException (UploadStats uploadStats){

        String lastUploadTimestamp = null;
        if (uploadStats != null){
            lastUploadTimestamp = uploadStats.getLastUploadTimestamp();
        }

        return (lastUploadTimestamp != null || lastUploadTimestamp.equalsIgnoreCase("null")) ? lastUploadTimestamp : DEFAULT_LAST_UPDATE_DATE;

    }


    /**
     * Set the upload time on the central mds db
     *
     * @param timestamp the new timestamp to set
     */
    public void setLastUploadTimestamp(String timestamp) throws CentralSearchConnectorException {
        try {
            int statusCode;
            HttpPut httpPut = new HttpPut(SamplyShareUtils.addTrailingSlash(centralSearchUrl.getPath()) + PATH_UPLOAD_STATS);

            UploadStats uploadStats = new UploadStats(timestamp);
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(uploadStats, writer);
            HttpEntity httpEntity = new StringEntity(writer.getBuffer().toString());
            httpPut.setEntity(httpEntity);
            logger.debug("Trying to set last upload timestamp on " + httpHost.toString());
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpPut)) {
                statusCode = response.getStatusLine().getStatusCode();
            } catch (IOException e) {
                throw new CentralSearchConnectorException(e);
            }
            if (uploadId != null) {
                EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_UPLOAD_SET_TIMESTAMP, uploadId, timestamp, Integer.toString(statusCode));
            }
        }  catch (Exception e) {
            throw new CentralSearchConnectorException(e);
        }
    }

    /**
     * Delete all patients on the central mds db, that are prefixed with a given string
     *
     * @param prefix the prefix to select the patients to delete
     * @return the http status code of the operation
     */
    public int deletePatients(String prefix) throws CentralSearchConnectorException {
        String prefixToDelete = (prefix == null ? anonymizedPatientPrefix : prefix);
        HttpDelete httpDelete = new HttpDelete(SamplyShareUtils.addTrailingSlash(centralSearchUrl.getPath()) + PATH_PATS_PREFIX + "/" + prefixToDelete);

        int statusCode;
        try (CloseableHttpResponse response = httpClient.execute(httpHost, httpDelete)) {
            statusCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            throw new CentralSearchConnectorException(e);
        }
        if (uploadId != null) {
            EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_DELETE_ANONYMIZED_PATIENTS, uploadId, Integer.toString(statusCode));
        }
        return statusCode;
    }

    /**
     * Upload one patient dataset to the central mds database
     *
     * TODO: Maybe add preemptive authentication here (manually add Authorization header - as in samply.share v1.x)
     *
     * @param patient the patient to upload
     * @return the result of the operation
     */
    public PatientUploadResult uploadPatient(Patient patient) {
        PatientUploadResult result = new PatientUploadResult();
        result.setSuccess(false);
        HttpPut httpPut;

        try {
            httpPut = new HttpPut(SamplyShareUtils.addTrailingSlash(centralSearchUrl.getPath()) + PATH_PATIENTS + "/" + URLEncoder.encode(patient.getId(), StandardCharsets.ISO_8859_1.toString()));
            HttpEntity entity = new StringEntity(marshalPatient(patient), StandardCharsets.UTF_8);
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
            httpPut.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(httpHost, httpPut);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String responseBody = EntityUtils.toString(httpEntity);
            response.close();
            result.setStatus(statusCode);
            if (statusCode >= 400) {
                result.setMessage(responseBody);
            } else {
                result.setSuccess(true);
            }
            result.setRetry(false);
        } catch (ConnectTimeoutException cte) {
            result.setRetry(true);
            result.setMessage("Connect Timeout");
        } catch (SocketTimeoutException ste) {
            result.setRetry(true);
            result.setMessage("Socket Timeout");
        }  catch (UnsupportedEncodingException e) {
            result.setRetry(false);
            result.setMessage(e.getMessage());
            EventLogUtil.insertEventLogEntryForUploadId(EventMessageType.E_PATIENT_UPLOAD_RESULT, uploadId,patient.getId(), result.toString());
        } catch (IOException e) {
            result.setRetry(true);
            result.setMessage("IO Exception");
        }
        return result;
    }

    /**
     * Get the string representation of a patient
     *
     * @param patient the patient to marshal
     * @return the string representation
     */
    public String marshalPatient(Patient patient) {
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            StringWriter stringWriter = new StringWriter();
            patientMarshaller.marshal(objectFactory.createPatient(patient), stringWriter);
            return stringWriter.toString();
        } catch (JAXBException e) {
            logger.error("Caught JAXB Exception while trying to marshal patient", e);
            return null;
        }
    }

    /**
     * Check the reachability of the central MDS database
     *
     * @return a check result object with the outcome of the connection check
     */
    public CheckResult checkConnection() {
        HttpGet httpGet = new HttpGet(centralSearchUrl.getPath() + "uploadStats");
        CheckResult result = new CheckResult();
        result.setExecutionDate(new Date());
        result.getMessages().add(new Message(httpGet.getMethod() + " " + httpHost.toString() + centralSearchUrl.getPath() + "uploadStats "  + httpGet.getProtocolVersion(), "fa-long-arrow-right"));

        try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

            result.getMessages().add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 400) {
                result.setSuccess(true);
            } else {
                result.setSuccess(false);
                result.getMessages().add(new Message(EntityUtils.toString(entity), "fa-bolt"));
            }
        } catch (IOException ioe) {
            result.getMessages().add(new Message("IOException: " + ioe.getMessage(), "fa-bolt"));
            result.setSuccess(false);
        }
        return result;
    }

//    public void uploadPatients(List<Patient> patients) {
//        for (Patient patient : patients) {
//            uploadPatient(patient);
//        }
//    }

}

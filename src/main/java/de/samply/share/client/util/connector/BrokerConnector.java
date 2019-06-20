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

import com.google.gson.Gson;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.Inquiries;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.db.enums.BrokerStatusType;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.model.db.tables.pojos.InquiryAnswer;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.*;
import de.samply.share.common.model.dto.monitoring.StatusReportItem;
import de.samply.share.common.utils.Constants;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.common.*;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.tools.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.samply.share.common.utils.Constants.AUTH_HEADER_VALUE_SAMPLY;

/**
 * A connector that handles all communication with a searchbroker
 */
public class BrokerConnector {

    private static final Logger logger = LogManager.getLogger(BrokerConnector.class);
    private transient HttpConnector httpConnector;
    private Broker broker;
    private Credentials credentials;
    private HttpHost httpHost;
    private CloseableHttpClient httpClient;
    private URL brokerUrl;
    private RequestConfig requestConfig;

    public Broker getBroker() {
        return broker;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Prevent instantiation without providing a broker
     */
    private BrokerConnector() {
    }

    /**
     * Instantiate a broker connector for a certain broker. Credentials are read from the database.
     *
     * @param broker the broker to connect to
     */
    public BrokerConnector(Broker broker) {
        this(broker, CredentialsUtil.getCredentialsForBroker(broker));
    }

    /**
     * Instantiate a broker connector for a certain broker
     *
     * @param broker      the broker to connect to
     * @param credentials the credentials to authenticate with that broker
     */
    private BrokerConnector(Broker broker, Credentials credentials) {
        this.broker = broker;
        this.credentials = credentials;
        httpConnector = ApplicationBean.createHttpConnector();
        requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        try {
            httpHost = SamplyShareUtils.getAsHttpHost(broker.getAddress());
            httpClient = httpConnector.getHttpClient(httpHost);
            brokerUrl = SamplyShareUtils.stringToURL(broker.getAddress());
        } catch (MalformedURLException e) {
            logger.error("Could not initialize BrokerConnector for broker: " + broker.getId());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the name, the searchbroker provides as its own
     *
     * @return the name of the broker
     */
    public String getBrokerName() throws BrokerConnectorException {
        if (!SamplyShareUtils.isNullOrEmpty(broker.getName())) {
            return broker.getName();
        }
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + "rest/searchbroker/name");
            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response;

            response = httpClient.execute(httpHost, httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String name = EntityUtils.toString(entity, Consts.UTF_8);
            response.close();

            if (statusCode == HttpStatus.SC_OK) {
                broker.setName(name);
                BrokerUtil.updateBroker(broker);
                return broker.getName();
            }
        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
        return broker.getAddress();
    }

//    public BrokerStatusType getStatus() {
//        // TODO: do we need this with frequent polling?
//        return BrokerStatusType.BS_OK;
//    }

    /**
     * Register with this broker
     *
     * @return a status, used for further handling. Either display a confirmation code box or show an error
     */
    public BrokerStatusType register() throws BrokerConnectorException {
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.BANKS_PATH + credentials.getUsername());

            HttpPut httpPut = new HttpPut(uri.normalize().toString());
            httpPut.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpHost, httpPut);

            int retCode = response.getStatusLine().getStatusCode();
            response.close();

            if (retCode == HttpStatus.SC_UNAUTHORIZED) {
                return BrokerStatusType.BS_ACTIVATION_PENDING;
            } else if (retCode == HttpStatus.SC_CONFLICT)
                return BrokerStatusType.BS_AUTHENTICATION_ERROR;
            else {
                return BrokerStatusType.BS_UNREACHABLE;
            }
        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Send a DELETE command in order to request deletion of this instance from the connected brokers database
     *
     * @return success information
     */
    public boolean unregister() throws BrokerConnectorException {
        logger.info("Request deletion from: " + broker.getAddress());

        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(broker.getAddress()) + Constants.BANKS_PATH + credentials.getUsername());
            HttpDelete httpDelete = new HttpDelete(uri.normalize().toString());
            httpDelete.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
            httpDelete.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpHost, httpDelete);

            int retCode = response.getStatusLine().getStatusCode();
            response.close();
            return retCode == HttpStatus.SC_NO_CONTENT;
        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Send an activation code to the searchbroker
     *
     * @param activationCode the activation code to send
     * @return the http status code received from the broker
     */
    public int activate(String activationCode) throws BrokerConnectorException {
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(broker.getAddress()) + Constants.BANKS_PATH + credentials.getUsername());
            HttpPut httpPut = new HttpPut(uri.normalize().toString());
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, Constants.AUTH_HEADER_VALUE_REGISTRATION + " " + activationCode);

            httpPut.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(httpHost, httpPut);
            HttpEntity entity = response.getEntity();
            String entityOutput = EntityUtils.toString(entity, Consts.UTF_8);

            int retCode = response.getStatusLine().getStatusCode();
            response.close();

            if (retCode == HttpStatus.SC_CREATED) {
                credentials.setPasscode(entityOutput);
                CredentialsUtil.updateCredentials(credentials);
            }

            return retCode;
        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Get the list of inquiry ids and revisions from the broker
     *
     * @return map of inquiry ids and revisions
     */
    public Map<String, String> getInquiryList() throws BrokerConnectorException {
        if (credentials == null) {
            throw new BrokerConnectorException("No credentials provided for broker " + broker.getId());
        }
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH);

            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
            httpGet.setConfig(requestConfig);
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig.setConnectTimeout(30 * 1000);
            requestConfig.setConnectionRequestTimeout(60 * 1000);
            requestConfig.setSocketTimeout(30 * 1000);
            httpGet.setConfig(requestConfig.build());
            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }
            if (statusCode == HttpStatus.SC_OK) {
                updateLastChecked();
                Serializer serializer = new Persister();
                Inquiries inquiries;
                try {
                    inquiries = serializer.read(Inquiries.class, responseString);
                } catch (Exception e) {
                    throw new BrokerConnectorException("Error reading inquiries", e);
                }
                if (SamplyShareUtils.isNullOrEmpty(inquiries.getInquiries())) {
                    return new HashMap<>();
                }

                Map<String, String> queryIds = new HashMap<>();
                for (de.samply.share.client.model.Inquiries.Inquiry inquiry : inquiries.getInquiries()) {
                    queryIds.put(inquiry.getId(), inquiry.getRevision());
                }
                return queryIds;
            }
        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
        return new HashMap<>();
    }

    /**
     * Retrieve a test inquiry from the broker
     *
     * @param result the check result object to be filled
     * @return the test inquiry
     */
    public Inquiry getTestInquiry(CheckResult result) throws BrokerConnectorException {
        result.setExecutionDate(new Date());
        if (credentials == null) {
            result.setSuccess(false);
            String message = "No credentials provided for broker " + broker.getId();
            result.getMessages().add(new Message(message, "fa-bolt"));
            throw new BrokerConnectorException(message);
        }
        try {
            String path = SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.TESTINQUIRIES_PATH + "/" + 1;
            URI uri = new URI(path);
            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
            result.getMessages().add(new Message(httpGet.getMethod() + " " + path + " " + httpGet.getProtocolVersion(),
                    "fa-long-arrow-right"));
            result.getMessages().add(new Message(httpGet.getFirstHeader(HttpHeaders.AUTHORIZATION).getName() + " " +
                    httpGet.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue()));

            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                result.getMessages().add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }

            if (statusCode == HttpStatus.SC_OK) {
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    StringReader stringReader = new StringReader(responseString);
                    JAXBElement<Inquiry> inquiryElement = unmarshaller.unmarshal(new StreamSource(stringReader), Inquiry.class);
                    Inquiry inquiry = inquiryElement.getValue();
                    result.setSuccess(true);
                    result.getMessages().add(new Message("Successfully unmarshalled inquiry", "fa-check"));
                    result.getMessages().add(new Message(responseString));
                    return inquiry;
                } catch (JAXBException e) {
                    result.setSuccess(false);
                    result.getMessages().add(new Message("JAXBException: " + e.getMessage(), "fa-bolt"));
                    throw new BrokerConnectorException(e);
                }
            } else {
                result.setSuccess(false);
                String message = "Unexpected status code received while trying to load test inquiry: " + statusCode;
                result.getMessages().add(new Message(message, "fa-long-arrow-left"));
                throw new BrokerConnectorException(message);
            }

        } catch (IOException | URISyntaxException e) {
            result.setSuccess(false);
            result.getMessages().add(new Message(e.getMessage(), "fa-bolt"));
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Retrieve a reference query from the broker
     * <p>
     * This query is used to gather performance data to report to monitoring
     *
     * @return the reference query
     */
    public Query getReferenceQuery() throws BrokerConnectorException {
        if (credentials == null) {
            String message = "No credentials provided for broker " + broker.getId();
            throw new BrokerConnectorException(message);
        }
        try {
            String path = SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.REFERENCEQUERY_PATH;
            URI uri = new URI(path);
            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }

            if (statusCode == HttpStatus.SC_OK) {
                JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                StringReader stringReader = new StringReader(responseString);
                JAXBElement<Query> queryElement = unmarshaller.unmarshal(new StreamSource(stringReader), Query.class);
                return queryElement.getValue();
            }

        } catch (IOException | URISyntaxException | JAXBException e) {
            throw new BrokerConnectorException(e);
        }
        return null;
    }

    /**
     * Get an inquiry from the broker
     *
     * @param inquiryId the inquiry id as known by the broker (source_id in the database)
     * @return the inquiry
     */
    public Inquiry getInquiry(int inquiryId) throws BrokerConnectorException {
        if (credentials == null) {
            throw new BrokerConnectorException("No credentials provided for broker " + broker.getId());
        }
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH + "/" + inquiryId);
            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }

            if (statusCode == HttpStatus.SC_OK) {
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    StringReader stringReader = new StringReader(responseString);
                    JAXBElement<Inquiry> inquiryElement = unmarshaller.unmarshal(new StreamSource(stringReader), Inquiry.class);
                    return inquiryElement.getValue();
                } catch (JAXBException e) {
                    throw new BrokerConnectorException(e);
                }
            } else {
                throw new BrokerConnectorException("Unexpected status code received while trying to load inquiry " + inquiryId + ": " + statusCode);
            }

        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Get additional information about the inquiry
     *
     * @param inquiryId the inquiry id as known by the broker (source_id in the database)
     * @return additional information about the inquriry (label, description and revision)
     */
    public Info getInquiryInfo(int inquiryId) throws BrokerConnectorException {
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH + "/" + inquiryId + "/" + Constants.INFO_PATH);
            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }

            if (statusCode == HttpStatus.SC_OK) {
                return SamplyShareUtils.unmarshal(responseString, JAXBContext.newInstance(de.samply.share.model.ccp.ObjectFactory.class), Info.class);
//                return responseString;
            } else {
                throw new BrokerConnectorException("Couldn't load info - got status code " + statusCode + " from broker " + broker.getAddress());
            }

        } catch (IOException | URISyntaxException | JAXBException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Get the contact that created the inquiry
     *
     * @param inquiryId the inquiry id as known by the broker (source_id in the database)
     * @return the contact of the inquirer
     */
    public Contact getInquiryContact(int inquiryId) throws BrokerConnectorException {
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH + "/" + inquiryId + "/" + Constants.CONTACT_PATH);
            HttpGet httpGet = new HttpGet(uri.normalize().toString());
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }

            if (statusCode == HttpStatus.SC_OK) {
                return SamplyShareUtils.unmarshal(responseString, JAXBContext.newInstance(ObjectFactory.class), Contact.class);
            } else {
                throw new BrokerConnectorException("Couldn't load contact - got status code " + statusCode + " from broker " + broker.getAddress());
            }

        } catch (IOException | URISyntaxException | JAXBException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Check if an expose is available for the inquiry
     *
     * @param inquiryId the inquiry id as known by the broker (source_id in the database)
     * @return true if an expose is available
     */
    public boolean inquiryHasExpose(int inquiryId) throws BrokerConnectorException {
        try {
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH + "/" + inquiryId + "/" + Constants.EXPOSE_CHECK_PATH);
            HttpGet httpGet = new HttpGet(uri.normalize().toString());

            int statusCode;
            String responseString;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                responseString = EntityUtils.toString(entity, Consts.UTF_8);
                EntityUtils.consume(entity);
            }

            if (statusCode == HttpStatus.SC_OK) {
                return true;
            } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return responseString == null || !responseString.equals(Constants.EXPOSE_UNAVAILABLE);
            } else {
                return false;
            }

        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }

    /**
     * Send a (disguised) reply to the broker.
     * <p>
     * Currently, the format of the reply is not defined. It might just be an integer...or some xml representation of a result set
     *
     * @param inquiryDetails the inquiry details object
     * @param reply          the reply to submit to the broker
     */
    public void reply(InquiryDetails inquiryDetails, Object reply, LdmConnector ldmConnector) throws BrokerConnectorException {
        try {
            de.samply.share.client.model.db.tables.pojos.Inquiry inquiry = InquiryUtil.fetchInquiryById(inquiryDetails.getInquiryId());
            int inquirySourceId = inquiry.getSourceId();

            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH + "/" + inquirySourceId + "/" + Constants.REPLIES_PATH
                    + "/" + credentials.getUsername());
            HttpPut httpPut = new HttpPut(uri.normalize().toString());
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());


            String replyString="";
            JSONObject stats= new JSONObject();
            if (ldmConnector instanceof LdmConnectorCentraxx) {
                if (reply.getClass() == Integer.class) {
                    replyString = Integer.toString((Integer) reply);
                } else {
                    replyString = reply.toString();
                }
            } else if (ldmConnector instanceof LdmConnectorSamplystoreBiobank) {
                BbmriResult result = (BbmriResult) reply;

                stats.put("donor", NumberDisguiser.getDisguisedNumber(result.getNumberOfDonors()));
                stats.put("sample", NumberDisguiser.getDisguisedNumber(result.getNumberOfSamples()));
                replyString = stats.toString();
            }
            StringEntity entity = new StringEntity(replyString);
            httpPut.setEntity(entity);
            int statusCode;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpPut)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity rEntity = response.getEntity();
                EntityUtils.consume(rEntity);
                logger.debug("Sending reply got us: " + statusCode);
                EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_REPLY_SENT_TO_BROKER,
                        inquiryDetails.getInquiryId(), Integer.toString(statusCode));
            }

            InquiryAnswer inquiryAnswer = new InquiryAnswer();
            inquiryAnswer.setInquiryDetailsId(inquiryDetails.getId());
            // TODO: With more ways to answer to an inquiry, this must be changed
            inquiryAnswer.setContent(replyString);
            InquiryAnswerUtil.insertInquiryAnswer(inquiryAnswer);

        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }


    /**
     * Set the last checked timestamp for this broker in the database
     */
    private void updateLastChecked() {
        broker.setLastChecked(new Timestamp(new Date().getTime()));
        broker.setStatus(BrokerStatusType.BS_OK);
        BrokerUtil.updateBroker(broker);
    }

    /**
     * Check the reachability of the broker
     *
     * @return a check result object with the outcome of the connection check
     */
    public CheckResult checkConnection() {
        CheckResult result = new CheckResult();
        result.setExecutionDate(new Date());

        try {
            HttpGet httpGet = new HttpGet(brokerUrl.getPath());
            result.getMessages().add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));
            CloseableHttpResponse response = httpClient.execute(httpHost, httpGet);
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
        } catch (IOException e) {
            result.setSuccess(false);
            result.getMessages().add(new Message(e.getMessage(), "fa-bolt"));
        }

        return result;
    }

    /**
     * Transmit a list of status report items to the broker (to relay to monitoring)
     *
     * @param statusReportItems the list of items to report
     */
    public void sendStatusReportItems(List<StatusReportItem> statusReportItems) throws BrokerConnectorException {
        try {
            Gson gson = new Gson();
            String reportString = gson.toJson(statusReportItems);
            URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.MONITORING_PATH);
            HttpPut httpPut = new HttpPut(uri.normalize().toString());
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

            StringEntity entity = new StringEntity(reportString);

            httpPut.setEntity(entity);

            int statusCode;
            try (CloseableHttpResponse response = httpClient.execute(httpHost, httpPut)) {
                statusCode = response.getStatusLine().getStatusCode();
                HttpEntity rEntity = response.getEntity();
                EntityUtils.consume(rEntity);
                logger.debug("Sending monitoring info got us: " + statusCode);
            }

        } catch (IOException | URISyntaxException e) {
            throw new BrokerConnectorException(e);
        }
    }

}

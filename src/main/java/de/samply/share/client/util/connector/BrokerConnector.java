package de.samply.share.client.util.connector;

import static de.samply.share.common.utils.Constants.AUTH_HEADER_VALUE_SAMPLY;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.samply.common.http.HttpConnector;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.check.CheckResult;
import de.samply.share.client.model.check.Message;
import de.samply.share.client.model.db.enums.BrokerStatusType;
import de.samply.share.client.model.db.enums.EventMessageType;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.model.db.tables.pojos.InquiryAnswer;
import de.samply.share.client.model.db.tables.pojos.InquiryDetails;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.client.util.db.EventLogUtil;
import de.samply.share.client.util.db.InquiryAnswerUtil;
import de.samply.share.client.util.db.InquiryUtil;
import de.samply.share.common.model.dto.monitoring.StatusReportItem;
import de.samply.share.common.utils.Constants;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.Contact;
import de.samply.share.model.common.ISamplyResult;
import de.samply.share.model.common.Info;
import de.samply.share.model.common.Inquiry;
import de.samply.share.model.common.ObjectFactory;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.inquiry.InquiriesIdList;
import de.samply.share.model.common.result.Reply;
import de.samply.share.model.common.result.ReplyEntity;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
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
import org.codehaus.jackson.map.ObjectMapper;

/**
 * A connector that handles all communication with a searchbroker.
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

  /**
   * Prevent instantiation without providing a broker.
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
   * Instantiate a broker connector for a certain broker.
   *
   * @param broker      the broker to connect to
   * @param credentials the credentials to authenticate with that broker
   */
  private BrokerConnector(Broker broker, Credentials credentials) {
    this.broker = broker;
    this.credentials = credentials;
    httpConnector = ApplicationBean.createHttpConnector();
    requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
        .setConnectionRequestTimeout(10000).build();
    try {
      httpHost = SamplyShareUtils.getAsHttpHost(broker.getAddress());
      httpClient = httpConnector.getHttpClient(httpHost);
      brokerUrl = SamplyShareUtils.stringToUrl(broker.getAddress());
    } catch (MalformedURLException e) {
      logger.error("Could not initialize BrokerConnector for broker: " + broker.getId());
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets broker.
   *
   * @return the broker
   */
  public Broker getBroker() {
    return broker;
  }

  /**
   * Gets credentials.
   *
   * @return the credentials
   */
  public Credentials getCredentials() {
    return credentials;
  }

  /**
   * Sets credentials.
   *
   * @param credentials the credentials
   */
  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  /**
   * Get the name, the searchbroker provides as its own.
   *
   * @return the name of the broker
   * @throws BrokerConnectorException the broker connector exception
   */
  public String getBrokerName() throws BrokerConnectorException {
    if (!SamplyShareUtils.isNullOrEmpty(broker.getName())) {
      return broker.getName();
    }
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + "rest/searchbroker/name");
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

  /**
   * Register with this broker.
   *
   * @return a status, used for further handling. Either display a confirmation code box or show an
   *        error
   * @throws BrokerConnectorException the broker connector exception
   */
  public BrokerStatusType register() throws BrokerConnectorException {
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.BANKS_PATH
              + credentials.getUsername());

      HttpPut httpPut = new HttpPut(uri.normalize().toString());
      httpPut.setConfig(requestConfig);
      CloseableHttpResponse response = httpClient.execute(httpHost, httpPut);

      int retCode = response.getStatusLine().getStatusCode();
      response.close();

      if (retCode == HttpStatus.SC_UNAUTHORIZED) {
        return BrokerStatusType.BS_ACTIVATION_PENDING;
      } else if (retCode == HttpStatus.SC_CONFLICT) {
        return BrokerStatusType.BS_AUTHENTICATION_ERROR;
      } else {
        return BrokerStatusType.BS_UNREACHABLE;
      }
    } catch (IOException | URISyntaxException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Send a DELETE command in order to request deletion of this instance from the connected brokers
   * database.
   *
   * @return success information
   * @throws BrokerConnectorException the broker connector exception
   */
  public boolean unregister() throws BrokerConnectorException {
    logger.info("Request deletion from: " + broker.getAddress());

    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(broker.getAddress()) + Constants.BANKS_PATH
              + credentials.getUsername());
      HttpDelete httpDelete = new HttpDelete(uri.normalize().toString());
      httpDelete.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
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
   * Send an activation code to the searchbroker.
   *
   * @param activationCode the activation code to send
   * @return the http status code received from the broker
   * @throws BrokerConnectorException the broker connector exception
   */
  public int activate(String activationCode) throws BrokerConnectorException {
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(broker.getAddress()) + Constants.BANKS_PATH
              + credentials.getUsername());
      HttpPut httpPut = new HttpPut(uri.normalize().toString());
      httpPut.setHeader(HttpHeaders.AUTHORIZATION,
          Constants.AUTH_HEADER_VALUE_REGISTRATION + " " + activationCode);

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
   * Get the list of inquiry ids and revisions from the broker.
   *
   * @return map of inquiry ids and revisions
   * @throws BrokerConnectorException the broker connector exception
   */
  public Map<String, String> getInquiryList() throws BrokerConnectorException {
    if (credentials == null) {
      throw new BrokerConnectorException("No credentials provided for broker " + broker.getId());
    }
    try {
      URI uri = new URI(SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()))
          .resolve(Constants.INQUIRIES_PATH);

      HttpGet httpGet = new HttpGet(uri.normalize().toString());
      httpGet.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

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
        InquiriesIdList inquiriesIdList;
        try {
          JAXBContext jaxbContext = JAXBContext.newInstance(InquiriesIdList.class);
          Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
          XMLInputFactory factory = XMLInputFactory.newInstance();
          XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(responseString));
          inquiriesIdList = unmarshaller.unmarshal(reader, InquiriesIdList.class).getValue();
        } catch (Exception e) {
          throw new BrokerConnectorException("Error reading inquiries", e);
        }

        if (SamplyShareUtils.isNullOrEmpty(inquiriesIdList.getInquiryIds())) {
          return new HashMap<>();
        }

        Map<String, String> queryIds = new HashMap<>();
        for (InquiriesIdList.InquiryId inquiry : inquiriesIdList.getInquiryIds()) {
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
   * Retrieve a test inquiry from the broker.
   *
   * @param result the check result object to be filled
   * @return the test inquiry
   * @throws BrokerConnectorException the broker connector exception
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
      String path =
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.TESTINQUIRIES_PATH
              + "/" + 1;
      URI uri = new URI(path);
      HttpGet httpGet = new HttpGet(uri.normalize().toString());
      httpGet.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
      httpGet.setHeader(Constants.QUERY_LANGUAGE,
          ApplicationBean.getBridgeheadInfos().getQueryLanguage());
      result.getMessages()
          .add(new Message(httpGet.getMethod() + " " + path + " " + httpGet.getProtocolVersion(),
              "fa-long-arrow-right"));
      result.getMessages()
          .add(new Message(httpGet.getFirstHeader(HttpHeaders.AUTHORIZATION).getName() + " "
              + httpGet.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue()));

      int statusCode;
      String responseString;
      try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
        result.getMessages()
            .add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
        statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        responseString = EntityUtils.toString(entity, Consts.UTF_8);
        EntityUtils.consume(entity);
      }

      if (statusCode == HttpStatus.SC_OK) {
        try {
          result.setSuccess(true);
          result.getMessages().add(new Message("Successfully unmarshalled inquiry",
              "fa-check"));
          result.getMessages().add(new Message(responseString));
          JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
          Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
          StringReader stringReader = new StringReader(responseString);
          JAXBElement<Inquiry> inquiryElement = unmarshaller
              .unmarshal(new StreamSource(stringReader), Inquiry.class);
          return inquiryElement.getValue();
        } catch (JAXBException e) {
          result.setSuccess(false);
          result.getMessages().add(new Message("JAXBException: " + e.getMessage(),
              "fa-bolt"));
          throw new BrokerConnectorException(e);
        }
      } else {
        result.setSuccess(false);
        String message =
            "Unexpected status code received while trying to load test inquiry: " + statusCode;
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
   * Get the CQL reference query from the searchbroker.
   *
   * @return the CQL reference query as String
   * @throws BrokerConnectorException IOException or URISyntaxException
   */
  public String getReferenceQueryCql() throws BrokerConnectorException {
    try (CloseableHttpResponse response = getResponse()) {
      int statusCode = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, Consts.UTF_8);
      EntityUtils.consume(entity);
      if (statusCode == HttpStatus.SC_OK) {
        return responseString;
      }
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
    return null;
  }

  private CloseableHttpResponse getResponse()
      throws BrokerConnectorException, IOException, URISyntaxException {
    if (credentials == null) {
      String message = "No credentials provided for broker " + broker.getId();
      throw new BrokerConnectorException(message);
    }
    String path =
        SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.REFERENCEQUERY_PATH;
    URI uri = new URI(path);
    HttpGet httpGet = new HttpGet(uri.normalize().toString());
    httpGet.setHeader(HttpHeaders.AUTHORIZATION,
        AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
    httpGet.addHeader(Constants.HEADER_KEY_QUERY_LANGUAGE,
        ApplicationBean.getBridgeheadInfos().getQueryLanguage());
    return httpClient.execute(httpHost, httpGet);
  }


  /**
   * Retrieve a reference query from the broker. This query is used to gather performance data to
   * report to monitoring.
   *
   * @return the reference query
   * @throws BrokerConnectorException the broker connector exception
   */
  public Query getReferenceQuery() throws BrokerConnectorException {
    try (CloseableHttpResponse response = getResponse()) {
      int statusCode = response.getStatusLine().getStatusCode();
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, Consts.UTF_8);
      EntityUtils.consume(entity);
      if (statusCode == HttpStatus.SC_OK) {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader stringReader = new StringReader(responseString);
        JAXBElement<Query> queryElement = unmarshaller
            .unmarshal(new StreamSource(stringReader), Query.class);
        return queryElement.getValue();
      }
    } catch (IOException | URISyntaxException | JAXBException e) {
      throw new BrokerConnectorException(e);
    }
    return null;
  }

  /**
   * Get an inquiry from the broker.
   *
   * @param inquiryId the inquiry id as known by the broker (source_id in the database)
   * @return the inquiry
   * @throws BrokerConnectorException the broker connector exception
   */
  public Inquiry getInquiry(int inquiryId) throws BrokerConnectorException {
    if (credentials == null) {
      throw new BrokerConnectorException("No credentials provided for broker " + broker.getId());
    }
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH
              + "/" + inquiryId);
      HttpGet httpGet = new HttpGet(uri.normalize().toString());
      httpGet.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
      httpGet.addHeader(Constants.HEADER_KEY_QUERY_LANGUAGE,
          ApplicationBean.getBridgeheadInfos().getQueryLanguage());

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
          JAXBElement<Inquiry> inquiryElement = unmarshaller
              .unmarshal(new StreamSource(stringReader), Inquiry.class);
          return inquiryElement.getValue();
        } catch (JAXBException e) {
          throw new BrokerConnectorException(e);
        }
      } else {
        throw new BrokerConnectorException(
            "Unexpected status code received while trying to load inquiry " + inquiryId + ": "
                + statusCode);
      }

    } catch (IOException | URISyntaxException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Get additional information about the inquiry.
   *
   * @param inquiryId the inquiry id as known by the broker (source_id in the database)
   * @return additional information about the inquriry (label, description and revision)
   * @throws BrokerConnectorException the broker connector exception
   */
  public Info getInquiryInfo(int inquiryId) throws BrokerConnectorException {
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH
              + "/" + inquiryId + "/" + Constants.INFO_PATH);
      HttpGet httpGet = new HttpGet(uri.normalize().toString());
      httpGet.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

      int statusCode;
      String responseString;
      try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
        statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        responseString = EntityUtils.toString(entity, Consts.UTF_8);
        EntityUtils.consume(entity);
      }

      if (statusCode == HttpStatus.SC_OK) {
        return SamplyShareUtils.unmarshal(responseString,
            JAXBContext.newInstance(de.samply.share.model.ccp.ObjectFactory.class), Info.class);
      } else {
        throw new BrokerConnectorException(
            "Couldn't load info - got status code " + statusCode + " from broker " + broker
                .getAddress());
      }

    } catch (IOException | URISyntaxException | JAXBException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Get the contact that created the inquiry.
   *
   * @param inquiryId the inquiry id as known by the broker (source_id in the database)
   * @return the contact of the inquirer
   * @throws BrokerConnectorException the broker connector exception
   */
  public Contact getInquiryContact(int inquiryId) throws BrokerConnectorException {
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH
              + "/" + inquiryId + "/" + Constants.CONTACT_PATH);
      HttpGet httpGet = new HttpGet(uri.normalize().toString());
      httpGet.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

      int statusCode;
      String responseString;
      try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
        statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        responseString = EntityUtils.toString(entity, Consts.UTF_8);
        EntityUtils.consume(entity);
      }

      if (statusCode == HttpStatus.SC_OK) {
        return SamplyShareUtils
            .unmarshal(responseString, JAXBContext.newInstance(ObjectFactory.class), Contact.class);
      } else {
        throw new BrokerConnectorException(
            "Couldn't load contact - got status code " + statusCode + " from broker " + broker
                .getAddress());
      }

    } catch (IOException | URISyntaxException | JAXBException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Check if an expose is available for the inquiry.
   *
   * @param inquiryId the inquiry id as known by the broker (source_id in the database)
   * @return true if an expose is available
   * @throws BrokerConnectorException the broker connector exception
   */
  public boolean inquiryHasExpose(int inquiryId) throws BrokerConnectorException {
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH
              + "/" + inquiryId + "/" + Constants.EXPOSE_CHECK_PATH);
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
   * Send a (disguised) reply to the broker. Currently, the format of the reply is not defined. It
   * might just be an integer...or some xml representation of a result set.
   *
   * @param inquiryDetails the inquiry details object
   * @param result         the reply to submit to the broker
   * @throws BrokerConnectorException the broker connector exception
   */
  public void reply(InquiryDetails inquiryDetails, Integer result) throws BrokerConnectorException {
    try {
      String replyString = Integer.toString(result);

      reply(inquiryDetails, replyString);
    } catch (IOException | URISyntaxException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Send a (disguised) reply to the broker. Currently, the format of the reply is not defined. It
   * might just be an integer...or some xml representation of a result set.
   *
   * @param inquiryDetails the inquiry details object
   * @param result         the reply to submit to the broker
   * @throws BrokerConnectorException the broker connector exception
   */
  public void reply(InquiryDetails inquiryDetails, ISamplyResult result)
      throws BrokerConnectorException {
    ReplyEntity replyDonor = new ReplyEntity();
    replyDonor.setLabel("Donors");
    replyDonor.setCount(NumberDisguiser.getDisguisedNumber(result.getNumberOfPatients()));
    replyDonor.setStratifications(result.getStratificationsOfPatients());

    ReplyEntity replySample = new ReplyEntity();
    replySample.setLabel("Samples");
    replySample.setCount(NumberDisguiser.getDisguisedNumber(result.getNumberOfSpecimens()));
    replySample.setStratifications(result.getStratificationsOfSpecimens());

    Reply reply = new Reply();
    reply.setDonor(replyDonor);
    reply.setSample(replySample);
    reply.setRedirectUrl(createRedirectUrl(inquiryDetails.getInquiryId()));

    ObjectMapper mapper = new ObjectMapper();
    try {
      reply(inquiryDetails, mapper.writeValueAsString(reply));
    } catch (URISyntaxException | IOException e) {
      throw new BrokerConnectorException(e);
    }
  }

  private void reply(InquiryDetails inquiryDetails, String replyString)
      throws URISyntaxException, IOException {
    de.samply.share.client.model.db.tables.pojos.Inquiry inquiry = InquiryUtil
        .fetchInquiryById(inquiryDetails.getInquiryId());

    int inquirySourceId = inquiry.getSourceId();

    URI uri = new URI(
        SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.INQUIRIES_PATH + "/"
            + inquirySourceId + "/" + Constants.REPLIES_PATH
            + "/" + credentials.getUsername());
    HttpPut httpPut = new HttpPut(uri.normalize().toString());
    httpPut.setHeader(HttpHeaders.AUTHORIZATION,
        AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());

    StringEntity entity = new StringEntity(replyString);
    httpPut.setEntity(entity);
    int statusCode;
    try (CloseableHttpResponse response = httpClient.execute(httpHost, httpPut)) {
      statusCode = response.getStatusLine().getStatusCode();
      HttpEntity entityR = response.getEntity();
      EntityUtils.consume(entityR);
      logger.debug("Sending reply got us: " + statusCode);
      EventLogUtil.insertEventLogEntryForInquiryId(EventMessageType.E_REPLY_SENT_TO_BROKER,
          inquiryDetails.getInquiryId(), Integer.toString(statusCode));
    }

    InquiryAnswer inquiryAnswer = new InquiryAnswer();
    inquiryAnswer.setInquiryDetailsId(inquiryDetails.getId());
    inquiryAnswer.setContent(replyString);
    InquiryAnswerUtil.insertInquiryAnswer(inquiryAnswer);
  }

  private String createRedirectUrl(int id) {
    return ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.SHARE_URL)
        + "/user/show_inquiry.xhtml?inquiryId=" + id + "&faces-redirect=true";
  }


  /**
   * Set the last checked timestamp for this broker in the database.
   */
  private void updateLastChecked() {
    broker.setLastChecked(new Timestamp(new Date().getTime()));
    broker.setStatus(BrokerStatusType.BS_OK);
    BrokerUtil.updateBroker(broker);
  }

  /**
   * Check the reachability of the broker.
   *
   * @return a check result object with the outcome of the connection check
   */
  public CheckResult checkConnection() {
    CheckResult result = new CheckResult();
    result.setExecutionDate(new Date());

    try {
      URI uri = new URI(brokerUrl.getPath());
      HttpGet httpGet = new HttpGet(uri.normalize().toString());
      result.getMessages()
          .add(new Message(httpGet.getRequestLine().toString(), "fa-long-arrow-right"));
      CloseableHttpResponse response = httpClient.execute(httpHost, httpGet);
      HttpEntity entity = response.getEntity();
      EntityUtils.consume(entity);
      result.getMessages()
          .add(new Message(response.getStatusLine().toString(), "fa-long-arrow-left"));
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode >= 200 && statusCode < 400) {
        result.setSuccess(true);
      } else {
        result.setSuccess(false);
        result.getMessages().add(new Message(EntityUtils.toString(entity), "fa-bolt"));
      }
    } catch (IOException | URISyntaxException e) {
      result.setSuccess(false);
      result.getMessages().add(new Message(e.getMessage(), "fa-bolt"));
    }

    return result;
  }

  /**
   * Transmit a list of status report items to the broker (to relay to monitoring).
   *
   * @param statusReportItems the list of items to report
   * @throws BrokerConnectorException the broker connector exception
   */
  public void sendStatusReportItems(List<StatusReportItem> statusReportItems)
      throws BrokerConnectorException {
    try {
      Gson gson = new GsonBuilder()
          .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
      String reportString = gson.toJson(statusReportItems);
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.MONITORING_PATH);
      HttpPut httpPut = new HttpPut(uri.normalize().toString());
      httpPut.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
      httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

      StringEntity entity = new StringEntity(reportString);

      httpPut.setEntity(entity);

      int statusCode;
      try (CloseableHttpResponse response = httpClient.execute(httpHost, httpPut)) {
        statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entityR = response.getEntity();
        EntityUtils.consume(entityR);
        logger.debug("Sending monitoring info got us: " + statusCode);
      }

    } catch (IOException | URISyntaxException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Send the site name of the bridgehead to the searchbroker.
   * @param siteName the site name of the bridgehead
   * @return http response code
   * @throws BrokerConnectorException BrokerConnectorException
   */
  public CloseableHttpResponse sendSiteName(String siteName) throws BrokerConnectorException {
    try {
      URI uri = new URI(
          SamplyShareUtils.addTrailingSlash(broker.getAddress()) + Constants.BANKS_PATH
              + credentials.getUsername() + "/site/" + siteName);
      HttpPut httpPut = new HttpPut(uri.normalize().toString());
      httpPut.setHeader(HttpHeaders.AUTHORIZATION,
          AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
      return httpClient.execute(httpHost, httpPut);
    } catch (IOException | URISyntaxException e) {
      throw new BrokerConnectorException(e);
    }
  }

  /**
   * Get all site names which are stored in the searchbroker.
   * @return site names as list
   * @throws URISyntaxException URISyntaxException
   * @throws BrokerConnectorException BrokerConnectorException
   */
  public List<String> getSiteNames() throws URISyntaxException, BrokerConnectorException {
    URI uri = new URI(
        SamplyShareUtils.addTrailingSlash(brokerUrl.getPath()) + Constants.SITES_NAME_PATH);
    HttpGet httpGet = new HttpGet(uri.normalize().toString());
    httpGet.setHeader(HttpHeaders.AUTHORIZATION,
        AUTH_HEADER_VALUE_SAMPLY + " " + credentials.getPasscode());
    int statusCode;
    String responseString;
    try (CloseableHttpResponse response = httpClient.execute(httpHost, httpGet)) {
      statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        HttpEntity entity = response.getEntity();
        responseString = EntityUtils.toString(entity, Consts.UTF_8);
        EntityUtils.consume(entity);
        return convertSiteNameResponseToList(responseString);
      }
      return new ArrayList<>();
    } catch (IOException e) {
      throw new BrokerConnectorException(e);
    }
  }

  private List<String> convertSiteNameResponseToList(String response) {
    JsonParser parser = new JsonParser();
    JsonArray sitesJsonArray = (JsonArray) parser.parse(response);
    List<String> siteNames = new ArrayList<>();
    for (int i = 0; i < sitesJsonArray.size(); i++) {
      JsonObject site = sitesJsonArray.get(i).getAsJsonObject();
      siteNames.add(site.get("name").getAsString());
    }
    return siteNames;
  }
}

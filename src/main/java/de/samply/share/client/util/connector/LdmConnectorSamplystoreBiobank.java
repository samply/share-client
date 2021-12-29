package de.samply.share.client.util.connector;

import de.samply.common.ldmclient.LdmClientException;
import de.samply.common.ldmclient.samplystorebiobank.LdmClientSamplystoreBiobank;
import de.samply.share.model.bbmri.BbmriResult;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.View;
import de.samply.share.model.common.ViewFields;
import de.samply.share.model.common.Where;
import de.samply.share.model.osse.Error;
import de.samply.share.model.osse.QueryResultStatistic;
import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the LdmConnector interface for samply store rest backends.
 */
public class LdmConnectorSamplystoreBiobank extends
    AbstractLdmConnectorView<LdmClientSamplystoreBiobank, BbmriResult, QueryResultStatistic, Error,
        de.samply.share.model.osse.View> {

  private static final Logger logger = LoggerFactory.getLogger(
      LdmConnectorSamplystoreBiobank.class);

  public LdmConnectorSamplystoreBiobank(boolean useCaching) {
    super(useCaching);
  }

  public LdmConnectorSamplystoreBiobank(boolean useCaching, int maxCacheSize) {
    super(useCaching, maxCacheSize);
  }

  @Override
  public Logger getLogger() {
    return logger;
  }

  @Override
  public boolean isLdmSamplystoreBiobank() {
    return true;
  }

  @Override
  boolean useAuthorizationForLdm() {
    return true;
  }

  @Override
  LdmClientSamplystoreBiobank createLdmClient(CloseableHttpClient httpClient, String baseUrl,
      boolean useCaching) throws LdmClientException {
    return new LdmClientSamplystoreBiobank(httpClient, baseUrl, useCaching);
  }

  LdmClientSamplystoreBiobank createLdmClient(CloseableHttpClient httpClient, String baseUrl,
      boolean useCaching, int maxCacheSize) throws LdmClientException {
    return new LdmClientSamplystoreBiobank(httpClient, baseUrl, useCaching, maxCacheSize);
  }

  @Override
  View createView(Query query, List<String> removeKeysFromView, boolean completeMdsViewFields,
      boolean includeAdditionalViewfields) {
    View view = new View();
    view.setQuery(query);
    // TODO: How to get viewfields for samply store to use?
    ViewFields viewFields = new ViewFields();

    view.setViewFields(viewFields);
    return view;
  }

  @Override
  String extractQueryResultId(BbmriResult queryResult) {
    return Integer.toString(queryResult.getQueryId());
  }

  @Override
  void marshalQueryResult(BbmriResult queryResult, File xmlFile, Marshaller marshaller)
      throws JAXBException {
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(queryResult, xmlFile);
  }

  @Override
  View createViewForMonitoring(boolean dktkFlagged) {
    View view = new View();
    Query query = new Query();
    Where where = new Where();
    query.setWhere(where);
    view.setQuery(query);
    return view;
  }

  /**
   * Create the reference view that is used to get the amount of patients for that query.
   *
   * @param referenceQuery the reference query, as received from the broker
   * @return the constructed view object that can be posted to centraxx
   */
  @Override
  View createReferenceViewForMonitoring(Query referenceQuery) {
    View view = new View();
    view.setQuery(referenceQuery);
    return view;
  }
}

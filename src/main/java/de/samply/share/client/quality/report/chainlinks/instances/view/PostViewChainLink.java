package de.samply.share.client.quality.report.chainlinks.instances.view;

import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.LdmChainLink;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterException;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;
import org.apache.http.HttpStatus;

public class PostViewChainLink<I extends ChainLinkItem & ViewContext> extends LdmChainLink<I> {


  public PostViewChainLink(LocalDataManagementRequester localDataManagementRequester) {
    super(localDataManagementRequester);
  }

  @Override
  protected LocalDataManagementResponse getLocalDataManagementResponse(I chainLinkItem)
      throws ChainLinkException {

    try {
      return localDataManagementRequester.postViewAndGetLocationUrl(chainLinkItem.getView());
    } catch (LocalDataManagementRequesterException e) {
      throw new ChainLinkException(e);
    }

  }

  @Override
  protected I process(I chainLinkItem, LocalDataManagementResponse localDataManagementResponse) {

    String locationUrl = getLocationUrl(localDataManagementResponse);

    chainLinkItem.setLocationUrl(locationUrl);

    return chainLinkItem;

  }

  private String getLocationUrl(LocalDataManagementResponse<String> localDataManagementResponse) {
    return localDataManagementResponse.getResponse();
  }

  @Override
  protected I setToBeRepeated(I item, LocalDataManagementResponse localDataManagementResponse) {

    if (!(localDataManagementResponse.isSuccessful()
        || localDataManagementResponse.getStatusCode() == HttpStatus.SC_CREATED)) {
      item.setToBeRepeated();
    }

    return item;

  }

  @Override
  protected String getChainLinkId() {
    return "Views Poster";
  }
}

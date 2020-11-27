package de.samply.share.client.quality.report.chainlinks;

import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;
import de.samply.share.model.common.Error;

public abstract class LdmChainLink<I extends ChainLinkItem> extends ChainLink<I> {

  protected LocalDataManagementRequester localDataManagementRequester;

  public LdmChainLink(LocalDataManagementRequester localDataManagementRequester) {
    this.localDataManagementRequester = localDataManagementRequester;
  }

  protected abstract LocalDataManagementResponse getLocalDataManagementResponse(I chainLinkItem)
      throws ChainLinkException;

  protected abstract I process(I chainLinkItem,
      LocalDataManagementResponse localDataManagementResponse);

  @Override
  protected I process(I item) throws ChainLinkException {

    LocalDataManagementResponse localDataManagementResponse = getLocalDataManagementResponse(item);
    item = process(item, localDataManagementResponse);
    item = addError(item, localDataManagementResponse);
    item = setToBeRepeated(item, localDataManagementResponse);

    return item;

  }

  protected I setToBeRepeated(I item, LocalDataManagementResponse localDataManagementResponse) {

    if (localDataManagementResponse != null && !localDataManagementResponse.isSuccessful()) {
      item.setToBeRepeated();
    }

    return item;
  }

  protected I addError(I item, LocalDataManagementResponse localDataManagementResponse) {

    if (localDataManagementResponse != null) {

      Error error = localDataManagementResponse.getError();

      if (error != null) {

        ChainLinkError chainLinkError = new ChainLinkError();
        chainLinkError.setError(error);
        chainLinkError.setChainLinkItem(item);

        item.setChainLinkError(chainLinkError);

      }

    }

    return item;

  }
}

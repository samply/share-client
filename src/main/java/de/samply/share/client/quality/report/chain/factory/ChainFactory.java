package de.samply.share.client.quality.report.chain.factory;

import de.samply.share.client.quality.report.chain.Chain;


public interface ChainFactory {

  Chain create(String fileId) throws ChainFactoryException;

}

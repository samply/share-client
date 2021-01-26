package de.samply.share.client.quality.report.chain;

import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;

public interface Chain extends Runnable {

  ChainStatistics getChainStatistics();

}

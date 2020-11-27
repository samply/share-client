package de.samply.share.client.quality.report.chainlinks.instances.configuration;

import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;

public class ConfigurationStarterChainLink extends ConfigurationChainLink {

  @Override
  protected String getChainLinkId() {
    return "Configuration Starter";
  }

  @Override
  protected ChainLinkItem process(ChainLinkItem item) throws ChainLinkException {

    startConfiguration();
    return item;
  }

  private void startConfiguration() {
    activateTask();
  }

  private void activateTask() {
    // List<String> activeTasks = ApplicationBean.getActiveTasks();
    //        if (activeTasks != null){
    //            activeTasks.add(getChainLinkTask());
    //        }

  }

}

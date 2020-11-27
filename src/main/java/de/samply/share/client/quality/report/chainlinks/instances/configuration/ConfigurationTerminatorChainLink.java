package de.samply.share.client.quality.report.chainlinks.instances.configuration;

import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;

public class ConfigurationTerminatorChainLink extends ConfigurationChainLink {

  @Override
  protected String getChainLinkId() {
    return "Configuration Terminator";
  }

  @Override
  protected ChainLinkItem process(ChainLinkItem item) throws ChainLinkException {

    terminateConfiguration();
    return item;
  }

  private void terminateConfiguration() {

    deactivateTask();

  }

  private void deactivateTask() {
    //        List<String> activeTasks = ApplicationBean.getActiveTasks();
    //        if (activeTasks != null){
    //            activeTasks.remove(getChainLinkTask());
    //        }

  }

  @Override
  public void finalizeChainLink() {
    terminateConfiguration();
    super.finalizeChainLink();
  }

}

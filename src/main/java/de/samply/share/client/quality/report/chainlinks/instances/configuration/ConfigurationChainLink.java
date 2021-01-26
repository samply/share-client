package de.samply.share.client.quality.report.chainlinks.instances.configuration;

import de.samply.share.client.quality.report.chainlinks.ChainLink;

public abstract class ConfigurationChainLink extends ChainLink {

  private static final String TASK = "ChainLink";

  /**
   * Todo.
   *
   * @return Todo.
   */
  public static String getChainLinkTask() {

    return TASK;

  }
}

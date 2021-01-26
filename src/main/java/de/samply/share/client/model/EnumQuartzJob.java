package de.samply.share.client.model;

public enum EnumQuartzJob {
  DIRECTORY_GROUP("DirectoryGroup"),
  CENTRAL_SEARCH_GROUP("CentralSearchGroup");

  private final String name;

  /**
   * Create a Quartz job enum by name.
   *
   * @param name name of the Quartz job.
   */
  EnumQuartzJob(final String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }
}

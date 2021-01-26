package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;

import java.util.HashMap;
import java.util.Map;

public enum ChainLinkStatisticKey {

  CONFIGURATION_STARTER("configuration-starter", "starting configuration..."),
  CONFIGURATION_TERMINATOR("configuration-terminator", "finalizing configuration"),
  QUALITY_REPORT_CSV_WRITER("quality-report-csv-writer", "writing quality results in csv file..."),
  QUALITY_REPORT_EXCEL_WRITER("quality-report-excel-writer",
      "writing quality results in Excel file..."),
  QUALITY_REPORT_METADATA_WRITER("quality-report-metadata-writer", "writing metadata file..."),
  IGNORED_ELEMENTS_SETTER("ignored-elements-setter",
      "enter MDR elements not mapped in local data management in quality report..."),
  NOT_FOUND_DATA_ELEMENTS_SETTER("not found elements-setter",
      "enter all data elements not found in quality report..."),
  LOCAL_DATA_MANAGEMENT_RESULTS_REQUESTER("localdatamanagement-results-requester",
      "requesting local data management results..."),
  LOCAL_DATA_MANAGEMENT_STATISTICS_REQUESTER("localdatamanagement-statistics-requester",
      "requesting local data management statistics..."),
  VALIDATOR("validator", "validating quality results..."),
  VIEWS_CREATOR("views-creator", "creating views for local data management..."),
  VIEWS_SENDER("views-sender", "sending views to local data management..."),
  VIEWS_ONLY_STATISTICS_SENDER("views-only-statistics-sender",
      "sending views with option 'only statistics' to local data management...");

  private static Map<String, ChainLinkStatisticKey> mapByFileKey;
  private final String message;
  private final String fileKey;

  ChainLinkStatisticKey(String fileKey, String message) {

    this.message = message;
    this.fileKey = fileKey;

  }

  public static ChainLinkStatisticKey getChainLinkStatisticKey(String fileKey) {
    return getMapByFileKey().get(fileKey);
  }

  private static Map<String, ChainLinkStatisticKey> getMapByFileKey() {

    if (mapByFileKey == null) {
      mapByFileKey = new HashMap<>();

      for (ChainLinkStatisticKey chainLinkStatisticKey : values()) {
        mapByFileKey.put(chainLinkStatisticKey.getFileKey(), chainLinkStatisticKey);
      }
    }

    return mapByFileKey;

  }

  public String getMessage() {
    return message;
  }

  public String getFileKey() {
    return fileKey;
  }

}

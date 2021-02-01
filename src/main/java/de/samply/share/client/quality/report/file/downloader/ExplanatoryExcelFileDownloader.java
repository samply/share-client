package de.samply.share.client.quality.report.file.downloader;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.io.File;


public class ExplanatoryExcelFileDownloader extends FileDownloaderImpl {

  /**
   * Downloads explanatory excel file that will be added to the quality report.
   */
  public ExplanatoryExcelFileDownloader() {

    String sourceUrl = getSourceUrl();
    String destinationFilePath = getFilePath();

    setSourceUrl(sourceUrl);
    setDestinationFilePath(destinationFilePath);

  }

  private String getSourceUrl() {
    return ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_EXCEL_INFO_URL);
  }

  /**
   * Gets file path of explanatory file.
   *
   * @return Path of explanatory file.
   */
  public String getFilePath() {

    String filePath = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
    String filename = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_EXCEL_INFO_FILENAME);

    return (filePath != null && filename != null) ? filePath + File.separator + filename : null;

  }

  /**
   * Get explanatory file.
   *
   * @return Explanatory file.
   */
  public File getFile() {

    String filename = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_EXCEL_INFO_FILENAME);

    ClassLoader classLoader = getClass().getClassLoader();
    //return new File(classLoader.getResource(filename).getFile());
    // FIXME
    return null;
  }


}

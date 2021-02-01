package de.samply.share.client.quality.report.file.id.path;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.id.filename.QualityReportFilePattern;
import de.samply.share.client.quality.report.file.id.filename.QualityReportFilenameFormat;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class IdPathManagerImpl<I extends QualityResultCsvLineManager,
    J extends ExcelPattern, K extends MetadataTxtColumnManager> implements IdPathManager {

  private final QualityReportFilenameFormat csvFormat;
  private final QualityReportFilenameFormat excelFormat;
  private final QualityReportFilenameFormat metadataFormat;
  private String mainDirectory;

  {
    mainDirectory = getMainDirectory();

    csvFormat = getQualityReportFilenameFormat(getQualityResultCsvLineManagerClass());
    excelFormat = getQualityReportFilenameFormat(getExcelPatternClass());
    metadataFormat = getQualityReportFilenameFormat(getMetadataTxtColumnManager());

  }

  public abstract Class<I> getQualityResultCsvLineManagerClass();

  public abstract Class<J> getExcelPatternClass();

  public abstract Class<K> getMetadataTxtColumnManager();

  private QualityReportFilenameFormat getQualityReportFilenameFormat(
      Class<? extends QualityReportFilePattern> patternClass) {
    return QualityReportFilenameFormat.getQualityReportFilenameFormat(patternClass);
  }

  private String getMainDirectory() {

    String mainDirectory = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
    if (mainDirectory == null) {
      mainDirectory = ".";
    }

    return mainDirectory;

  }

  public void setMainDirectory(String mainDirectory) {
    this.mainDirectory = mainDirectory;
  }

  @Override
  public String getCsvFilePath(String fileId) {
    return getFilePath(fileId, csvFormat);
  }

  @Override
  public String getExcelFilePath(String fileId) {
    return getFilePath(fileId, excelFormat);
  }

  @Override
  public String getMetadataFilePath(String fileId) {
    return getFilePath(fileId, metadataFormat);
  }

  @Override
  public String getFileId(String filePath) {

    if (filePath != null) {

      int index = filePath.lastIndexOf(File.separator);
      filePath = filePath.substring(index + 1);

      return QualityReportFilenameFormat.getFileId(filePath);

    }

    return null;

  }

  private String getFilePath(String fileId, QualityReportFilenameFormat qualityFileFormat) {

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(mainDirectory);
    stringBuilder.append(File.separator);
    stringBuilder.append(qualityFileFormat.getFileName(fileId));

    return stringBuilder.toString();

  }

  @Override
  public List<String> getAllMetadataFilePaths() {

    List<String> filePaths = new ArrayList<>();

    File mainDirectory = new File(this.mainDirectory);

    for (File file : mainDirectory.listFiles()) {

      String filename = file.getName();
      QualityReportFilenameFormat qualityReportFilenameFormat = QualityReportFilenameFormat
          .getQualityReportFilenameFormat(filename);

      if (qualityReportFilenameFormat != null && qualityReportFilenameFormat.isMetafile()) {
        filePaths.add(file.getAbsolutePath());
      }
    }

    return filePaths;

  }

  @Override
  public String getCurrentQualityReportVersion() {
    return excelFormat.getVersion();
  }
}

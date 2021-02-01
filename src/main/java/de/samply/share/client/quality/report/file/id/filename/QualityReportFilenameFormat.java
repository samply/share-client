package de.samply.share.client.quality.report.file.id.filename;

import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager001;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager002;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerImplTest1;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern001;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern002;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager001;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager002;

public enum QualityReportFilenameFormat {

  CSV_001("csv", "001", QualityResultCsvLineManager001.class),
  CSV_002("csv", "002", QualityResultCsvLineManager002.class),
  CSV_TEST1("csv", "TEST1", QualityResultCsvLineManagerImplTest1.class),
  META_001("txt", "META_001", MetadataTxtColumnManager001.class),
  META_002("txt", "META_002", MetadataTxtColumnManager002.class),
  XLSX_001("xlsx", "001", ExcelPattern001.class),
  XLSX_002("xlsx", "002", ExcelPattern002.class);

  private static final String META = "META";
  private final String extension;
  private final String version;
  private final Class<? extends QualityReportFilePattern> patternClass;

  QualityReportFilenameFormat(String extension, String version,
      Class<? extends QualityReportFilePattern> patternClass) {

    this.extension = extension;
    this.version = version;
    this.patternClass = patternClass;

  }

  /**
   * Todo.
   *
   * @param extension Todo.
   * @param version   Todo.
   * @return Todo.
   */
  public static QualityReportFilenameFormat getQualityReportFilenameFormat(String extension,
      String version) {

    for (QualityReportFilenameFormat qualityReportFilenameFormat : values()) {

      if (qualityReportFilenameFormat.extension.equals(extension)
          && qualityReportFilenameFormat.version.equals(version)) {
        return qualityReportFilenameFormat;
      }
    }

    return null;

  }

  /**
   * Todo.
   *
   * @param filename Todo.
   * @return Todo.
   */
  public static QualityReportFilenameFormat getQualityReportFilenameFormat(String filename) {

    if (filename != null) {

      int index = filename.indexOf('_');
      if (index > 0 && filename.length() > index + 2) {
        filename = filename.substring(index + 1);
        String[] split = filename.split("\\.");

        if (split.length == 2) {

          String version = split[0];
          String extension = split[1];

          return getQualityReportFilenameFormat(extension, version);
        }
      }

    }
    return null;
  }

  /**
   * Todo.
   *
   * @param patternClass Todo.
   * @return Todo.
   */
  public static QualityReportFilenameFormat getQualityReportFilenameFormat(
      Class<? extends QualityReportFilePattern> patternClass) {

    for (QualityReportFilenameFormat qualityReportFilenameFormat : values()) {
      if (patternClass.equals(qualityReportFilenameFormat.patternClass)) {
        return qualityReportFilenameFormat;
      }
    }

    return null;
  }

  /**
   * Todo.
   *
   * @param filename Todo.
   * @return Todo.
   */
  public static String getVersion(String filename) {

    QualityReportFilenameFormat qualityReportFilenameFormat = getQualityReportFilenameFormat(
        filename);
    return (qualityReportFilenameFormat != null) ? qualityReportFilenameFormat.version : null;
  }

  public String getVersion() {
    return version;
  }

  /**
   * Todo.
   *
   * @param filePath Todo.
   * @return Todo.
   */
  public static String getFileId(String filePath) {

    String fileId = null;

    if (filePath != null) {
      int index = filePath.indexOf("_");
      fileId = filePath.substring(0, index);
    }

    return fileId;

  }

  /**
   * Todo.
   *
   * @param fileId Todo.
   * @return Todo.
   */
  public String getFileName(String fileId) {

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(fileId);
    stringBuilder.append('_');
    stringBuilder.append(version);
    stringBuilder.append('.');
    stringBuilder.append(extension);

    return stringBuilder.toString();

  }

  public boolean isMetafile() {
    return version.contains(META);
  }

}

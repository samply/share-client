package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactory;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryException;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.results.QualityResults;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelQualityReportFileManager<I extends ExcelPattern> extends
    QualityReportFileManagerImpl {

  protected static final Logger logger = LoggerFactory.getLogger(
      ExcelQualityReportFileManager.class);
  private final ExcelWorkbookFactory excelWorkbookFactory;

  /**
   * Creates excel quality report file manager.
   *
   * @param excelPattern  excel pattern.
   * @param idPathManager id path manager.
   */
  public ExcelQualityReportFileManager(I excelPattern, IdPathManagerImpl<?, I, ?> idPathManager) {

    super(idPathManager);
    this.excelWorkbookFactory = excelPattern.createExcelWorkbookFactory();

  }


  @Override
  public void writeFile(QualityResults qualityResults, String fileId)
      throws QualityReportFileManagerException {

    try {
      writeFileWithoutExceptions(qualityResults, fileId);
    } catch (ExcelWorkbookFactoryException e) {
      throw new QualityReportFileManagerException(e);
    }

  }

  private void writeFileWithoutExceptions(QualityResults qualityResults, String fileId)
      throws QualityReportFileManagerException, ExcelWorkbookFactoryException {

    logger.info("Getting file path");
    String filePath = idPathManager.getExcelFilePath(fileId);
    logger.info("creating workbook");
    SXSSFWorkbook workbook = excelWorkbookFactory.createWorkbook(qualityResults);
    logger.info("writing workbook");
    writeWorkbook(workbook, filePath);
    logger.info("workbook was written");

  }

  private void writeWorkbook(SXSSFWorkbook workbook, String filePath)
      throws QualityReportFileManagerException {

    try (FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath))) {

      workbook.write(fileOutputStream);

    } catch (IOException e) {
      throw new QualityReportFileManagerException(e);
    }

  }

  @Override
  public QualityResults readFile(String fileId) throws QualityReportFileManagerException {

    //TODO
    throw new QualityReportFileManagerException(new UnsupportedOperationException());

  }

}

package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactory;
import de.samply.share.client.quality.report.file.excel.workbook.ExcelWorkbookFactoryException;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.results.QualityResults;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelQualityReportFileManager<I extends ExcelPattern> extends
    QualityReportFileManagerImpl {

  protected static final Logger logger = LogManager.getLogger(ExcelQualityReportFileManager.class);
  private final ExcelWorkbookFactory excelWorkbookFactory;

  /**
   * Todo.
   *
   * @param excelPattern  Todo.
   * @param idPathManager Todo.
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
    XSSFWorkbook workbook = excelWorkbookFactory.createWorkbook(qualityResults);
    logger.info("writing workbook");
    writeWorkbook(workbook, filePath);
    logger.info("workbook was written");

  }

  private void writeWorkbook(XSSFWorkbook workbook, String filePath)
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

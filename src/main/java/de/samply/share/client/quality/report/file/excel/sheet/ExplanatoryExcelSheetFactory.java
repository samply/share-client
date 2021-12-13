package de.samply.share.client.quality.report.file.excel.sheet;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.excel.row.context.ExcelRowContext;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExplanatoryExcelSheetFactory implements ExcelSheetFactory {


  protected static final Logger logger = LogManager.getLogger(ExplanatoryExcelSheetFactory.class);
  private Integer workbookWindow;
  //private ExplanatoryExcelFileDownloader explanatoryExcelFileDownloader =
  // new ExplanatoryExcelFileDownloader();


  @Override
  public SXSSFWorkbook addSheet(SXSSFWorkbook workbook, String sheetTitle,
      ExcelRowContext excelRowContext) throws ExcelSheetFactoryException {
    return addSheet();
  }

  private SXSSFWorkbook addSheet() throws ExcelSheetFactoryException {

    //downloadExcelInfoFile();
    //File explanatoryExcelFile = new File (explanatoryExcelFileDownloader.getFilePath());
    File explanatoryExcelFile = getExplanatoryExcelFile();
    return readWorkbook(explanatoryExcelFile);

  }

  private File getExplanatoryExcelFile() throws ExcelSheetFactoryException {
    try {

      return getExplanatoryExcelFile_withoutExceptionManagement();

    } catch (Exception e) {
      throw new ExcelSheetFactoryException(e);
    }
  }

  private File getExplanatoryExcelFile_withoutExceptionManagement() throws URISyntaxException {

    String filename = ConfigurationUtil
        .getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_EXCEL_INFO_FILENAME);

    ClassLoader classLoader = getClass().getClassLoader();
    return new File(classLoader.getResource(filename).toURI());
  }
  //    private void downloadExcelInfoFile(){
  //
  //        try {
  //
  //            logger.info("downloading explanatory excel file");
  //            explanatoryExcelFileDownloader.download();
  //
  //        } catch (FileDownloaderException e) {
  //            logger.error(e);
  //        }
  //
  //    }


  private SXSSFWorkbook readWorkbook(File explanatoryExcelFile) throws ExcelSheetFactoryException {

    try (FileInputStream fileInputStream = new FileInputStream(explanatoryExcelFile)) {

      logger.info("reading explanatory excel file");
      XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
      return (workbookWindow != null) ? new SXSSFWorkbook(workbook, workbookWindow)
          : new SXSSFWorkbook(workbook);

    } catch (IOException e) {
      throw new ExcelSheetFactoryException(e);
    }

  }

  @Override
  public void setMaxNumberOfRowsPerSheet(int maxNumberOfRowsPerSheet) {
    workbookWindow = maxNumberOfRowsPerSheet;
  }


}

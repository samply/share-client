package de.samply.share.client.quality.report.tests;

import de.samply.common.http.HttpConnector;
import de.samply.common.mdrclient.MdrClient;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.centraxx.CentraxxMapperException;
import de.samply.share.client.quality.report.centraxx.CentraxxMapperImpl;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverter;
import de.samply.share.client.quality.report.dktk.DktkIdMdrIdConverterImpl;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager002;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern;
import de.samply.share.client.quality.report.file.excel.pattern.ExcelPattern002;
import de.samply.share.client.quality.report.file.id.path.IdPathManager002;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.ExcelQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.reader.ModelReader;
import de.samply.share.client.quality.report.model.reader.ModelReaderException;
import de.samply.share.client.quality.report.model.reader.QualityReportModelReaderImpl;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.util.db.ConfigurationUtil;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/excel-test")
public class ExcelTest {


  private final ModelSearcher modelSearcher;
  private final QualityReportFileManager qualityFileManager;
  private final ExcelQualityReportFileManager excelQualityFileManager;
  private final MdrClient mdrClient;
  private final DktkIdMdrIdConverter dktkIdManager;


  /**
   * Construct Excel Test. It tests different classes for file management of the quality report.
   *
   * @throws CentraxxMapperException centraxx mapper exception.
   */
  public ExcelTest() throws CentraxxMapperException {

    Model model = getModel();

    modelSearcher = new ModelSearcher(model);
    mdrClient = getMdrClient();
    dktkIdManager = new DktkIdMdrIdConverterImpl(mdrClient);

    IdPathManager002 idPathManager = new IdPathManager002();
    qualityFileManager = new CsvQualityReportFileManager(new QualityResultCsvLineManager002(),
        idPathManager);

    ExcelPattern excelPattern = new ExcelPattern002(model, mdrClient, new CentraxxMapperImpl(),
        dktkIdManager, null);
    excelQualityFileManager = new ExcelQualityReportFileManager(excelPattern, idPathManager);

  }

  /**
   * Test "write file" of excel quality file manager .
   *
   * @param fileId file id.
   * @return new file id.
   * @throws QualityReportFileManagerException Todo.
   */
  @GET
  public String myTest(@QueryParam("fileId") String fileId)
      throws QualityReportFileManagerException {

    QualityResults qualityResults = qualityFileManager.readFile(fileId);

    excelQualityFileManager.writeFile(qualityResults, fileId);

    return fileId;

  }

  private Model getModel() {
    try {

      ModelReader modelReader = new QualityReportModelReaderImpl();
      return modelReader.getModel();

    } catch (ModelReaderException e) {
      e.printStackTrace();
      return null;
    }
  }

  private MdrClient getMdrClient() {

    try {

      String mdrUrl = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_URL);
      HttpConnector httpConnector = ApplicationBean.createHttpConnector();

      return new MdrClient(mdrUrl, httpConnector.getClient(httpConnector.getHttpClient(mdrUrl)));

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }


}

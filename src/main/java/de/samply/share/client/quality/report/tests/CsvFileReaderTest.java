package de.samply.share.client.quality.report.tests;

import de.samply.common.http.HttpConnector;
import de.samply.common.mdrclient.MdrClient;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager002;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerImplTest1;
import de.samply.share.client.quality.report.file.id.path.IdPathManager002;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerTest1;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.model.reader.ModelReader;
import de.samply.share.client.quality.report.model.reader.ModelReaderException;
import de.samply.share.client.quality.report.model.reader.QualityReportModelReaderImpl;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidDateFilter;
import de.samply.share.client.quality.report.results.filter.QualityResultsValidIntegerFilter;
import de.samply.share.client.util.db.ConfigurationUtil;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/patient-data-f100")
public class CsvFileReaderTest {

  private static final String FILE_ID_SUFFIX = "_2";

  private final ModelSearcher modelSearcher;

  private final QualityReportFileManager qualityFileManager1;
  private final QualityReportFileManager qualityFileManager2;

  {
    Model model = getModel();

    qualityFileManager1 = createQualityFileManager(new QualityResultCsvLineManager002(),
        new IdPathManager002());
    qualityFileManager2 = createQualityFileManager(
        new QualityResultCsvLineManagerImplTest1(model, getMdrClient()),
        new IdPathManagerTest1());

    modelSearcher = new ModelSearcher(model);

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


  /**
   * Todo.
   *
   * @param fileId Todo.
   * @return Todo.
   * @throws QualityReportFileManagerException Todo.
   */
  @GET
  public String myTest(@QueryParam("fileId") String fileId)
      throws QualityReportFileManagerException {

    String newFileId = getNewFileId(fileId);

    QualityResults qualityResults = qualityFileManager1.readFile(fileId);

    qualityResults = new QualityResultsValidDateFilter(qualityResults, modelSearcher);
    qualityResults = new QualityResultsValidIntegerFilter(qualityResults, modelSearcher);

    qualityFileManager2.writeFile(qualityResults, newFileId);

    return newFileId;
  }

  private String getNewFileId(String fileId) {
    return fileId + FILE_ID_SUFFIX;
  }

  private QualityReportFileManager createQualityFileManager(
      QualityResultCsvLineManager qualityResultsCsvLineManager, IdPathManagerImpl idPathManager) {

    return new CsvQualityReportFileManager(qualityResultsCsvLineManager, idPathManager);

  }


}

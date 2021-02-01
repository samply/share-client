package de.samply.share.client.quality.report.tests;

import de.dth.mdr.validator.MdrConnection;
import de.dth.mdr.validator.MdrValidator;
import de.dth.mdr.validator.exception.MdrException;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.chain.factory.ChainFactoryException;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager002;
import de.samply.share.client.quality.report.file.id.path.IdPathManager002;
import de.samply.share.client.quality.report.file.manager.CsvQualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.model.mdr.MdrConnectionFactory;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidator;
import de.samply.share.client.quality.report.results.operations.QualityResultsValidatorException;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.QueryValidator;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/validator-test")
public class ValidatorTest {

  private final QualityReportFileManager qualityFileManager;


  private final QualityResultsValidator qualityResultsValidator;


  /**
   * Todo.
   *
   * @throws ChainFactoryException Todo.
   */
  public ValidatorTest() throws ChainFactoryException {

    IdPathManager002 idPathManager = new IdPathManager002();
    qualityFileManager = new CsvQualityReportFileManager(new QualityResultCsvLineManager002(),
        idPathManager);

    MdrConnectionFactory mdrConnectionFactory = new MdrConnectionFactory();
    MdrValidator dthValidator = createDthValidator(mdrConnectionFactory);
    QueryValidator queryValidator = new QueryValidator(ApplicationBean.getMdrClient());
    qualityResultsValidator = new QualityResultsValidator(dthValidator, queryValidator);

  }


  /**
   * Todo.
   *
   * @param fileId Todo.
   * @return Todo.
   * @throws QualityReportFileManagerException Todo.
   * @throws QualityResultsValidatorException  Todo.
   */
  @GET
  public String myTest(@QueryParam("fileId") String fileId)
      throws QualityReportFileManagerException, QualityResultsValidatorException {

    QualityResults qualityResults = qualityFileManager.readFile(fileId);
    qualityResultsValidator.validate(qualityResults);

    return fileId;

  }

  private MdrValidator createDthValidator(MdrConnectionFactory mdrConnectionFactory)
      throws ChainFactoryException {

    try {
      MdrConnection mdrConnection = mdrConnectionFactory.getMdrConnection();
      return new MdrValidator(mdrConnection,
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_GRP_MDSB),
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_GRP_MDSK));
    } catch (MdrConnectionException | MdrInvalidResponseException | MdrException
        | ExecutionException e) {
      throw new ChainFactoryException(e);
    }
  }

}

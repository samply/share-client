package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManager;
import de.samply.share.client.quality.report.file.csvline.manager.QualityResultCsvLineManagerException;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.QualityResultsImpl;
import de.samply.share.common.utils.MdrIdDatatype;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class CsvQualityReportFileManager<I extends QualityResultCsvLineManager> extends
    QualityReportFileManagerImpl {

  private final QualityResultCsvLineManager qualityResultsCsvLineManager;


  /**
   * Creates csv quality report file manager.
   *
   * @param qualityResultsCsvLineManager quality results csv line manager.
   * @param idPathManager                id path manager.
   */
  public CsvQualityReportFileManager(I qualityResultsCsvLineManager,
      IdPathManagerImpl<I, ?, ?> idPathManager) {

    super(idPathManager);
    this.qualityResultsCsvLineManager = qualityResultsCsvLineManager;

  }

  @Override
  public void writeFile(QualityResults qualityResults, String fileId)
      throws QualityReportFileManagerException {

    String filePath = idPathManager.getCsvFilePath(fileId);
    writeQualityResults(qualityResults, filePath);
  }


  private void writeFile(BufferedWriter bufferedWriter, QualityResults qualityResults)
      throws IOException, QualityResultCsvLineManagerException {

    for (MdrIdDatatype mdrId : qualityResults.getMdrIds()) {

      for (String value : qualityResults.getValues(mdrId)) {

        QualityResult result = qualityResults.getResult(mdrId, value);
        writeFile(bufferedWriter, mdrId, value, result);

      }
    }

    bufferedWriter.flush();

  }

  private void writeFile(BufferedWriter bufferedWriter, MdrIdDatatype mdrId, String value,
      QualityResult qualityResult) throws IOException, QualityResultCsvLineManagerException {

    String line = qualityResultsCsvLineManager.createLine(mdrId, value, qualityResult);
    bufferedWriter.write(line);

  }

  private void writeQualityResults(QualityResults qualityResults, String filePath)
      throws QualityReportFileManagerException {

    try (BufferedWriter bufferedWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

      writeFile(bufferedWriter, qualityResults);

    } catch (IOException | QualityResultCsvLineManagerException e) {
      throw new QualityReportFileManagerException(e);
    }

  }


  @Override
  public QualityResults readFile(String fileId) throws QualityReportFileManagerException {

    String filePath = idPathManager.getCsvFilePath(fileId);
    return readQualityResults(filePath);

  }

  private QualityResults readQualityResults(String filePath)
      throws QualityReportFileManagerException {

    try (BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

      QualityResults qualityResults = createQualityResults();
      String line;

      while ((line = bufferedReader.readLine()) != null) {
        qualityResults = qualityResultsCsvLineManager
            .parseLineAndAddToQualityResults(line, qualityResults);
      }

      return qualityResults;

    } catch (IOException e) {
      throw new QualityReportFileManagerException(e);
    }

  }


  private QualityResults createQualityResults() {
    return new QualityResultsImpl();
  }

}

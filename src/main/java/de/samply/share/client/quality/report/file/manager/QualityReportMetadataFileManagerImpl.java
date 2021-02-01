package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.id.path.IdPathManager;
import de.samply.share.client.quality.report.file.id.path.IdPathManagerImpl;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class QualityReportMetadataFileManagerImpl<I extends MetadataTxtColumnManager> implements
    QualityReportMetadataFileManager {

  private final IdPathManager idPathManager;
  private final I metadataTxtColumnManager;

  /**
   * Todo.
   *
   * @param metadataTxtColumnManager Todo.
   * @param idPathManager            Todo.
   */
  public QualityReportMetadataFileManagerImpl(I metadataTxtColumnManager,
      IdPathManagerImpl<?, ?, I> idPathManager) {

    this.idPathManager = idPathManager;
    this.metadataTxtColumnManager = metadataTxtColumnManager;

  }

  @Override
  public void write(QualityReportMetadata qualityReportMetadata, String fileId)
      throws QualityReportFileManagerException {

    String metadataFilePath = idPathManager.getMetadataFilePath(fileId);
    qualityReportMetadata.setQualityReportVersion(idPathManager.getCurrentQualityReportVersion());

    writeQualityReportMetadata(qualityReportMetadata, metadataFilePath);
  }

  private void write(QualityReportMetadata qualityReportMetadata, BufferedWriter bufferedWriter)
      throws IOException {

    String column = metadataTxtColumnManager.createColumn(qualityReportMetadata);
    bufferedWriter.write(column);
    bufferedWriter.flush();

  }

  private void writeQualityReportMetadata(QualityReportMetadata qualityReportMetadata,
      String filePath) throws QualityReportFileManagerException {
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {

      write(qualityReportMetadata, bufferedWriter);

    } catch (IOException e) {
      throw new QualityReportFileManagerException(e);
    }
  }

  @Override
  public QualityReportMetadata read(String fileId) throws QualityReportFileManagerException {

    String filePath = idPathManager.getMetadataFilePath(fileId);
    return readQualityReportMetadata(filePath);
  }

  private QualityReportMetadata read(BufferedReader bufferedReader) throws IOException {

    String column = IOUtils.toString(bufferedReader);
    return metadataTxtColumnManager.parseValuesOfColumn(column);

  }

  @Override
  public List<QualityReportMetadata> readAll() throws QualityReportFileManagerException {

    List<QualityReportMetadata> qualityReportMetadatas = new ArrayList<>();

    for (String metadataFilePath : getAllMetadataFilePaths()) {

      QualityReportMetadata qualityReportMetadata = readQualityReportMetadata(metadataFilePath);
      qualityReportMetadatas.add(qualityReportMetadata);

    }

    return qualityReportMetadatas;
  }

  private List<String> getAllMetadataFilePaths() throws QualityReportFileManagerException {

    try {

      return idPathManager.getAllMetadataFilePaths();

    } catch (Exception e) {
      throw new QualityReportFileManagerException(e);
    }

  }

  private QualityReportMetadata readQualityReportMetadata(String filePath)
      throws QualityReportFileManagerException {

    try (BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

      return read(bufferedReader);

    } catch (IOException e) {
      throw new QualityReportFileManagerException(e);
    }

  }

}

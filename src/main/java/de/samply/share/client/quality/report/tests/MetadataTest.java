package de.samply.share.client.quality.report.tests;

import de.samply.share.client.quality.report.file.id.path.IdPathManager002;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManagerImpl;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import de.samply.share.client.quality.report.file.metadata.txtcolumn.MetadataTxtColumnManager002;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/metadata-test")
public class MetadataTest {

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

    MetadataTxtColumnManager002 metadataTxtColumnManager = new MetadataTxtColumnManager002();
    IdPathManager002 idPathManager = new IdPathManager002();
    QualityReportMetadataFileManager qualityReportMetadataFileManager =
        new QualityReportMetadataFileManagerImpl<>(metadataTxtColumnManager, idPathManager);

    QualityReportMetadata qualityReportMetadata = new QualityReportMetadata();
    qualityReportMetadata.setCreationTimestamp(new Date());
    qualityReportMetadata.setFileId(fileId);

    qualityReportMetadataFileManager.write(qualityReportMetadata, fileId);

    QualityReportMetadata qualityReportMetadata1 = qualityReportMetadataFileManager.read(fileId);
    List<QualityReportMetadata> qualityReportMetadatas = qualityReportMetadataFileManager.readAll();

    return "Metadata Test!";

  }

}

package de.samply.share.client.quality.report.chainlinks.instances.file;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.file.manager.QualityReportMetadataFileManager;
import de.samply.share.client.quality.report.file.metadata.QualityReportMetadata;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterException;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;
import java.util.Date;

public class MetadataQualityReportFileWriterChainLink<I extends ChainLinkItem & FileContext> extends
    ChainLink<I> {


  private final QualityReportMetadataFileManager qualityReportMetadataFileManager;
  private final LocalDataManagementRequester localDataManagementRequester;

  public MetadataQualityReportFileWriterChainLink(
      QualityReportMetadataFileManager qualityReportMetadataFileManager,
      LocalDataManagementRequester localDataManagementRequester) {
    this.qualityReportMetadataFileManager = qualityReportMetadataFileManager;
    this.localDataManagementRequester = localDataManagementRequester;
  }

  @Override
  protected String getChainLinkId() {
    return "Metadata File Writer";
  }

  @Override
  protected I process(I item) throws ChainLinkException {

    try {

      writeMetadataFile(item);
      return item;

    } catch (QualityReportFileManagerException e) {
      throw new ChainLinkException(e);
    }

  }

  private void writeMetadataFile(I item) throws QualityReportFileManagerException {

    String fileId = item.getFileId();
    QualityReportMetadata metadata = createMetadata(item, fileId);

    qualityReportMetadataFileManager.write(metadata, fileId);
  }

  private QualityReportMetadata createMetadata(I item, String fileId) {

    String sqlMappingVersion = getSqlMappingVersion();

    QualityReportMetadata qualityReportMetadata = new QualityReportMetadata();
    qualityReportMetadata.setCreationTimestamp(new Date());
    qualityReportMetadata.setFileId(fileId);
    qualityReportMetadata.setSqlMappingVersion(sqlMappingVersion);

    return qualityReportMetadata;

  }

  private String getSqlMappingVersion() {

    try {

      return getSqlMappingVersion_withoutExceptionManagement();

    } catch (LocalDataManagementRequesterException e) {

      logger.error(e);
      return null;

    }

  }

  private String getSqlMappingVersion_withoutExceptionManagement()
      throws LocalDataManagementRequesterException {

    LocalDataManagementResponse<String> localDataManagementResponse = localDataManagementRequester
        .getSqlMappingVersion();
    return localDataManagementResponse.getResponse();

  }


}

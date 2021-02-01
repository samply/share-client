package de.samply.share.client.quality.report.chainlinks.instances.file;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.instances.validator.ValidatorContext;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManager;
import de.samply.share.client.quality.report.file.manager.QualityReportFileManagerException;
import de.samply.share.client.quality.report.results.QualityResults;

public class QualityReportFileWriterChainLink<I extends ChainLinkItem & FileContext
    & ValidatorContext> extends ChainLink<I> {

  private final QualityReportFileManager qualityReportFileManager;

  public QualityReportFileWriterChainLink(QualityReportFileManager qualityReportFileManager) {
    this.qualityReportFileManager = qualityReportFileManager;
  }

  @Override
  protected String getChainLinkId() {
    return "Quality Report File Writer";
  }

  @Override
  protected I process(I item) throws ChainLinkException {

    try {

      writefile(item);
      return item;

    } catch (QualityReportFileManagerException e) {
      e.printStackTrace();
      throw new ChainLinkException(e);
    }

  }

  private void writefile(I item) throws QualityReportFileManagerException {

    logger.info("getting quality results");
    QualityResults qualityResults = item.getQualityResults();

    logger.info("writing quality report");
    qualityReportFileManager.writeFile(qualityResults, item.getFileId());

    logger.info("quality report finished");

  }


}

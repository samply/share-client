package de.samply.share.client.quality.report.file.manager;

import de.samply.share.client.quality.report.file.id.path.IdPathManager;

public abstract class QualityReportFileManagerImpl implements QualityReportFileManager {

  protected IdPathManager idPathManager;

  public QualityReportFileManagerImpl(IdPathManager idPathManager) {
    this.idPathManager = idPathManager;
  }

}

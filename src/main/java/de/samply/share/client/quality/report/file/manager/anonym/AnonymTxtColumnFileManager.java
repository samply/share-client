package de.samply.share.client.quality.report.file.manager.anonym;

import de.samply.share.client.quality.report.file.txtcolumn.AnonymTxtColumn;

public interface AnonymTxtColumnFileManager {

  public void write(AnonymTxtColumn anonymTxtColumn) throws AnonymTxtColumnFileManagerException;

  public AnonymTxtColumn read() throws AnonymTxtColumnFileManagerException;

}

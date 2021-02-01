package de.samply.share.client.quality.report.file.manager.anonym;

import de.samply.share.client.quality.report.file.txtcolumn.AnonymTxtColumn;

public interface AnonymTxtColumnFileManager {

  void write(AnonymTxtColumn anonymTxtColumn) throws AnonymTxtColumnFileManagerException;

  AnonymTxtColumn read() throws AnonymTxtColumnFileManagerException;

}

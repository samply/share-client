package de.samply.share.client.quality.report.file.manager.anonym;

import de.samply.share.client.quality.report.file.txtcolumn.AnonymTxtColumn;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

public class AnonymTxtColumnFileManagerImpl implements AnonymTxtColumnFileManager {

  String filePath;

  public AnonymTxtColumnFileManagerImpl(String filePath) {
    this.filePath = filePath;
  }

  @Override
  public void write(AnonymTxtColumn anonymTxtColumn) throws AnonymTxtColumnFileManagerException {

    try (BufferedWriter bufferedWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

      bufferedWriter.write(anonymTxtColumn.createColumn());

    } catch (IOException e) {
      throw new AnonymTxtColumnFileManagerException(e);
    }

  }

  @Override
  public AnonymTxtColumn read() throws AnonymTxtColumnFileManagerException {

    try (BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

      return read(bufferedReader);

    } catch (IOException e) {
      throw new AnonymTxtColumnFileManagerException(e);
    }

  }

  private AnonymTxtColumn read(BufferedReader bufferedReader) throws IOException {

    String column = IOUtils.toString(bufferedReader);

    AnonymTxtColumn anonymTxtColumn = new AnonymTxtColumn();
    anonymTxtColumn.parseValuesOfColumn(column);

    return anonymTxtColumn;

  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
}

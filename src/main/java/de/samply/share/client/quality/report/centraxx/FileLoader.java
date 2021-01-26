package de.samply.share.client.quality.report.centraxx;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileLoader {

  /**
   * Todo.
   *
   * @param filenameReader Todo.
   * @param loader         Todo.
   * @throws CentraxxMapperException Todo.
   */
  public void load(FilenameReader filenameReader, LineLoader loader)
      throws CentraxxMapperException {

    File file = getFile(filenameReader);
    load(file, loader);

  }

  private void load(File file, LineLoader lineLoader) throws CentraxxMapperException {

    try (BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

      load(bufferedReader, lineLoader);

    } catch (IOException e) {
      throw new CentraxxMapperException(e);
    }

  }

  private void load(BufferedReader bufferedReader, LineLoader lineLoader) throws IOException {

    String line = null;

    while ((line = bufferedReader.readLine()) != null) {
      lineLoader.load(line);
    }

  }

  public String getConfigurationFilename(EnumConfiguration enumConfigurationFilename) {
    return ConfigurationUtil.getConfigurationElementValue(enumConfigurationFilename);
  }

  private File getFile(FilenameReader filenameReader) throws CentraxxMapperException {

    String filename = filenameReader.getFilename();
    return getConfigFile(filename);

  }

  private File getConfigFile(String filename) throws CentraxxMapperException {

    try {

      ClassLoader classLoader = getClass().getClassLoader();
      return new File(classLoader.getResource(filename).toURI());

    } catch (Exception e) {
      throw new CentraxxMapperException(e);
    }

  }

  public interface FilenameReader {

    String getFilename();
  }

  public interface LineLoader {

    void load(String line);
  }

}

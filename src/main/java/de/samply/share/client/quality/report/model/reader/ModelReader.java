package de.samply.share.client.quality.report.model.reader;

import de.samply.share.client.quality.report.model.Model;


public interface ModelReader {

  public Model getModel() throws ModelReaderException;

}

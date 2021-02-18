package de.samply.share.client.mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MappingFileReader {

  private final String cxxMdrCxxRepresentationsFilename = "CENTRAXX_MDRCXXREPRESENTATION.csv";
  private final String cxxMdrRepresentationsFilename = "CENTRAXX_MDRREPRESENTATION.csv";
  private final String teilerBaseViewColumnsFilename = "";


  public List<CxxMdrCxxRepresentation> readCxxMdrCxxRepresentations() {
    return readFileAndGetResults(cxxMdrCxxRepresentationsFilename,
        new CxxMdrCxxRepresentationsLineAnalyzer());
  }


  public List<CxxMdrRepresentation> readCxxMdrRepresentations() {
    return readFileAndGetResults(cxxMdrRepresentationsFilename,
        new CxxMdrRepresentationsLineAnalyzer());
  }

  public List<TeilerBaseViewColumn> readTeilerBaseViewColumns() {
    return readFileAndGetResults(teilerBaseViewColumnsFilename,
        new TeilerBaseViewColumnsLineAnalyzer());
  }

  private Integer getInteger(String numberS) {

    try {
      return new Integer(numberS);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private List readFileAndGetResults(String filename, LineAnalyzer lineAnalyzer) {

    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

      String currentLineS;

      while ((currentLineS = br.readLine()) != null) {
        lineAnalyzer.analyzeLine(currentLineS);
      }

      return lineAnalyzer.getResults();

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

  private interface LineAnalyzer {

    void analyzeLine(String line);

    List getResults();
  }

  private class CxxMdrCxxRepresentationsLineAnalyzer implements LineAnalyzer {

    private final List<CxxMdrCxxRepresentation> cxxMdrCxxRepresentations = new ArrayList<>();
    private Integer counter = 1;

    @Override
    public void analyzeLine(String line) {

      String[] split = line.split(";");
      if (split.length >= 3) {

        CxxMdrCxxRepresentation cxxMdrCxxRepresentation = new CxxMdrCxxRepresentation();

        cxxMdrCxxRepresentation.setOid(counter++);
        cxxMdrCxxRepresentation.setMdrRepresentationOid(getInteger(split[0]));
        cxxMdrCxxRepresentation.setCxxClassName(split[1]);
        cxxMdrCxxRepresentation.setCxxValueName(split[2]);

        cxxMdrCxxRepresentations.add(cxxMdrCxxRepresentation);

      }

    }

    public List<de.samply.share.client.mapper.CxxMdrCxxRepresentation> getResults() {
      return cxxMdrCxxRepresentations;
    }

  }

  private class CxxMdrRepresentationsLineAnalyzer implements LineAnalyzer {

    private final List<CxxMdrRepresentation> cxxMdrRepresentations = new ArrayList<>();

    @Override
    public void analyzeLine(String line) {

      String[] split = line.split(";");
      if (split.length >= 3) {

        CxxMdrRepresentation cxxMdrRepresentation = new CxxMdrRepresentation();

        cxxMdrRepresentation.setOid(getInteger(split[0]));
        cxxMdrRepresentation.setMdrMappingOid(getInteger(split[1]));
        cxxMdrRepresentation.setMdrPermittedValue(split[2]);
      }

    }

    public List<CxxMdrRepresentation> getResults() {
      return cxxMdrRepresentations;
    }

  }

  private class TeilerBaseViewColumnsLineAnalyzer implements LineAnalyzer {

    private final List<TeilerBaseViewColumn> teilerBaseViewColumns = new ArrayList<>();

    @Override
    public void analyzeLine(String line) {

      String[] split = line.split(";");

      //TODO
    }

    public List<TeilerBaseViewColumn> getResults() {
      return teilerBaseViewColumns;
    }

  }


}

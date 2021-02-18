package de.samply.share.client.quality.report.file.excel.row.context;

import de.samply.share.client.quality.report.file.excel.row.elements.ExcelRowElements;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ExcelRowContextImpl<ExcelRowParametersT> implements ExcelRowContext {


  protected List<ExcelRowParametersT> excelRowParametersList = new ArrayList<>();

  protected abstract ExcelRowElements convert(ExcelRowParametersT excelRowParameters)
      throws Exception;

  protected abstract org.apache.logging.log4j.Logger getLogger();

  public Integer getNumberOfRows() {
    return excelRowParametersList.size();
  }

  @Override
  public Iterator<ExcelRowElements> iterator() {
    return new ExcelRowContextIterator(excelRowParametersList.iterator());
  }

  protected class ExcelRowContextIterator implements Iterator<ExcelRowElements> {

    private final Iterator<ExcelRowParametersT> excelRowParametersIterator;

    public ExcelRowContextIterator(Iterator<ExcelRowParametersT> excelRowParametersIterator) {
      this.excelRowParametersIterator = excelRowParametersIterator;
    }

    @Override
    public boolean hasNext() {
      return excelRowParametersIterator.hasNext();
    }

    @Override
    public ExcelRowElements next() {

      ExcelRowParametersT next = excelRowParametersIterator.next();
      return convertParametersToElements(next);

    }

    private ExcelRowElements convertParametersToElements(ExcelRowParametersT excelRowParameters) {
      try {

        return convert(excelRowParameters);

      } catch (Exception e) {

        getLogger().error(e.getMessage(), e);
        return createEmptyExcelRowElements();// If there is an exception, the exception is logged
        // and the row is left clean in QB.

      }
    }

    @Override
    public void remove() {
      excelRowParametersIterator.remove();
    }

  }

}

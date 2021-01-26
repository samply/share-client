package de.samply.share.client.quality.report.file.metadata.txtcolumn;

import de.samply.share.client.quality.report.file.txtcolumn.TxtColumnImpl;
import de.samply.share.client.util.Utils;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MetadataTxtColumn extends TxtColumnImpl {

  public MetadataTxtColumn() {
    super(ElementOrder.values().length);
  }

  @Override
  protected String getElementTitle(int order) {
    return (order >= 0 && order < ElementOrder.values().length) ? ElementOrder.values()[order]
        .getTitle() : null;
  }

  @Override
  protected Integer getElementTitleOrder(String elementTitle) {
    return ElementOrder.getOrdinal(elementTitle);
  }

  private String getElement(ElementOrder order) {
    return getElement(order.ordinal());
  }

  private void addElement(ElementOrder order, String element) {
    addElement(order.ordinal(), element);
  }

  /**
   * Todo.
   *
   * @return Todo.
   */
  public Date getTimestamp() {

    String dateS = getElement(ElementOrder.TIMESTAMP);
    return (dateS != null) ? convert(dateS) : null;

  }

  /**
   * Todo.
   *
   * @param date Todo.
   */
  public void setTimestamp(Date date) {

    String dateS = Utils.convertDate3(date);
    addElement(ElementOrder.TIMESTAMP, dateS);

  }

  public String getSqlMappingVersion() {
    return getElement(ElementOrder.SQL_MAPPING_VERSION);
  }

  public void setSqlMappingVersion(String sqlMappingVersion) {
    addElement(ElementOrder.SQL_MAPPING_VERSION, sqlMappingVersion);
  }

  private Date convert(String date) {
    try {
      return Utils.convertDate3(date);
    } catch (ParseException e) {
      return null;
    }
  }

  public String getFileId() {
    return getElement(ElementOrder.FILE_ID);
  }

  public void setFileId(String fileId) {
    addElement(ElementOrder.FILE_ID, fileId);
  }

  public String getQualityReportVersion() {
    return getElement(ElementOrder.QUALITY_REPORT_VERSION);
  }

  public void setQualityReportVersion(String qualityReportVersion) {
    addElement(ElementOrder.QUALITY_REPORT_VERSION, qualityReportVersion);
  }

  private enum ElementOrder {

    FILE_ID("file-id"),
    TIMESTAMP("timestamp"),
    SQL_MAPPING_VERSION("sql-mapping-version"),
    QUALITY_REPORT_VERSION("quality-report-version");

    private static Map<String, Integer> titleAndOrdinals;
    private final String title;

    ElementOrder(String title) {
      this.title = title;
    }

    public static Integer getOrdinal(String title) {

      if (titleAndOrdinals == null) {

        titleAndOrdinals = new HashMap<>();

        for (ElementOrder elementOrder : values()) {
          titleAndOrdinals.put(elementOrder.getTitle(), elementOrder.ordinal());
        }

      }

      return titleAndOrdinals.get(title);
    }

    public String getTitle() {
      return title;
    }


  }

}

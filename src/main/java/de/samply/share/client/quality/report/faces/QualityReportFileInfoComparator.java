package de.samply.share.client.quality.report.faces;

import java.util.Comparator;


public class QualityReportFileInfoComparator implements Comparator<QualityReportFileInfo> {

  @Override
  public int compare(QualityReportFileInfo o1, QualityReportFileInfo o2) {

    return o1 == null ? (o2 == null ? 0 : Integer.MIN_VALUE) :
        (o2 == null ? Integer.MAX_VALUE : o1.getTimestamp().compareTo(o2.getTimestamp()));

  }

}

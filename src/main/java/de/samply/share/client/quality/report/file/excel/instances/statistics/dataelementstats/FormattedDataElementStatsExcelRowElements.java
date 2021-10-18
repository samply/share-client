package de.samply.share.client.quality.report.file.excel.instances.statistics.dataelementstats;

import de.samply.share.client.quality.report.file.excel.cell.style.ExcelCellStyle;
import de.samply.share.client.quality.report.file.excel.cell.style.GreenBackgroundCellStyle;

public class FormattedDataElementStatsExcelRowElements extends DataElementStatsExcelRowElements {

  private ExcelCellStyle greenBackgroundCellStyle = new GreenBackgroundCellStyle();

  @Override
  public void setGeneralRehearsalAContainedInQR(boolean value) {

    super.setGeneralRehearsalAContainedInQR(value);
    if (value) {
      addExcelCellStyle(greenBackgroundCellStyle,
          ElementOrder.GENERAL_REHEARSAL_A_CONTAINED_IN_QR.ordinal());
    }

  }

  @Override
  public void setGeneralRehearsalBLowMismatch(boolean value) {

    super.setGeneralRehearsalBLowMismatch(value);
    if (value) {
      addExcelCellStyle(greenBackgroundCellStyle,
          ElementOrder.GENERAL_REHEARSAL_B_LOW_MISMATCH.ordinal());
    }

  }

  @Override
  public void setGeneralRehearsalAAndB(boolean value) {

    super.setGeneralRehearsalAAndB(value);
    if (value) {
      addExcelCellStyle(greenBackgroundCellStyle, ElementOrder.GENERAL_REHEARSAL_A_AND_B.ordinal());
    }

  }


}

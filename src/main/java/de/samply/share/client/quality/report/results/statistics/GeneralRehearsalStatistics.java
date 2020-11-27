package de.samply.share.client.quality.report.results.statistics;

import de.samply.share.common.utils.MdrIdDatatype;

public interface GeneralRehearsalStatistics {

  public boolean getGeneralRehearsalAContainedInQR(MdrIdDatatype mdrId);

  public boolean getGeneralRehearsalBLowMismatch(MdrIdDatatype mdrId);

  public boolean getGeneralRehearsalAAndB(MdrIdDatatype mdrId);

}

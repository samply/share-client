package de.samply.share.client.quality.report.results.statistics;

import de.samply.share.common.utils.MdrIdDatatype;

public interface GeneralRehearsalStatistics {

  boolean getGeneralRehearsalAContainedInQR(MdrIdDatatype mdrId);

  boolean getGeneralRehearsalBLowMismatch(MdrIdDatatype mdrId);

  boolean getGeneralRehearsalAAndB(MdrIdDatatype mdrId);

}

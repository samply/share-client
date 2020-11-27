package de.samply.share.client.quality.report.centraxx;

import de.samply.share.common.utils.MdrIdDatatype;

public interface CentraxxMapper {

  public String getCentraXxAttribute(MdrIdDatatype mdrId);

  public String getCentraXxValue(MdrIdDatatype mdrId, String mdrValue);

  public String getGeneralRehearsalPriorization(MdrIdDatatype mdrId);

}

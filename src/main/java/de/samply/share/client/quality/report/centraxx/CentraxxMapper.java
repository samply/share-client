package de.samply.share.client.quality.report.centraxx;

import de.samply.share.common.utils.MdrIdDatatype;

public interface CentraxxMapper {

  String getCentraXxAttribute(MdrIdDatatype mdrId);

  String getCentraXxValue(MdrIdDatatype mdrId, String mdrValue);

  String getGeneralRehearsalPriorization(MdrIdDatatype mdrId);

}

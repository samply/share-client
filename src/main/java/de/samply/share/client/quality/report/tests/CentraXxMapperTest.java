package de.samply.share.client.quality.report.tests;

import de.samply.share.client.quality.report.centraxx.CentraxxMapper;
import de.samply.share.client.quality.report.centraxx.CentraxxMapperException;
import de.samply.share.client.quality.report.centraxx.CentraxxMapperImpl;
import de.samply.share.common.utils.MdrIdDatatype;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/centraxx-mapper-test")
public class CentraXxMapperTest {

  /**
   * Todo.
   *
   * @return Todo.
   * @throws CentraxxMapperException Todo.
   */
  @GET
  public String myTest() throws CentraxxMapperException {

    CentraxxMapper centraXxMapper = new CentraxxMapperImpl();

    String centraXxAttribute = centraXxMapper
        .getCentraXxAttribute(new MdrIdDatatype("urn:dktk:dataelement:33:2"));
    String centraXxValue = centraXxMapper
        .getCentraXxValue(new MdrIdDatatype("urn:dktk:dataelement:77:1"), "nicht erfasst");

    return "CentraXX Mapper Test!";
  }

}

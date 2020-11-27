package de.samply.share.client.util.jooq;

import java.util.Date;
import org.jooq.Converter;

/**
 * Used in JOOQ to be able to use java.util.Date values in the pojos. See {@code <forcedTypes>} in
 * jooq.xml.
 */
public class DateConverter implements Converter<java.sql.Date, Date> {

  @Override
  public Date from(java.sql.Date databaseObject) {
    if (databaseObject == null) {
      return null;
    } else {
      return new Date(databaseObject.getTime());
    }
  }

  @Override
  public java.sql.Date to(Date date) {
    if (date == null) {
      return null;
    } else {
      return new java.sql.Date(date.getTime());
    }
  }

  @Override
  public Class<java.sql.Date> fromType() {
    return java.sql.Date.class;
  }

  @Override
  public Class<Date> toType() {
    return Date.class;
  }
}

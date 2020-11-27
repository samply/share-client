package de.samply.share.client.util;

import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.model.centralsearch.DateRestriction;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.common.utils.SamplyShareUtils;
import de.samply.share.model.common.And;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Eq;
import de.samply.share.model.common.Gt;
import de.samply.share.model.common.IsNotNull;
import de.samply.share.model.common.Leq;
import de.samply.share.model.common.ObjectFactory;
import de.samply.share.model.common.Or;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.View;
import de.samply.share.model.common.Where;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for methods to create Views and Queries.
 */
public class UploadUtils {

  private static final Logger logger = LogManager.getLogger(UploadUtils.class);

  private UploadUtils() {

  }

  /**
   * Create a View without any date restrictions.
   *
   * @param dktkFlagged when set to true, only patients WITH explicit DKTK consent are requested
   *                    when set to false, only patients WITHOUT explicit DKTK consent are
   *                    requested
   * @return the view
   */
  public static View createFullUploadView(boolean dktkFlagged) throws MdrConnectionException {
    return createUploadView(null, dktkFlagged);
  }

  /**
   * Create a View with the given date restrictions.
   *
   * @param dateRestriction the date restriction object, specifying upper and lower bounds
   * @param dktkFlagged     when set to true, only patients WITH explicit DKTK consent are requested
   *                        when set to false, only patients WITHOUT explicit DKTK consent are
   *                        requested
   * @return the view
   */
  public static View createUploadView(DateRestriction dateRestriction, boolean dktkFlagged)
      throws MdrConnectionException {
    try {
      View view = new View();
      view.setQuery(createUploadQuery(dateRestriction, dktkFlagged));
      view.setViewFields(MdrUtils.getViewFields(true));
      return view;
    } catch (MdrConnectionException | ExecutionException e) {
      throw new MdrConnectionException(e.getMessage());
    }
  }

  /**
   * Create a Query without any date restrictions.
   *
   * @param dktkFlagged when set to true, only patients WITH explicit DKTK consent are requested
   *                    when set to false, only patients WITHOUT explicit DKTK consent are
   *                    requested
   * @return the view
   */
  public static Query createFullUploadQuery(boolean dktkFlagged) {
    return createUploadQuery(null, dktkFlagged);
  }

  /**
   * Create a Query with the given date restrictions.
   *
   * @param dateRestriction the date restriction object, specifying upper and lower bounds
   * @param dktkFlagged     when set to true, only patients WITH explicit DKTK consent are requested
   *                        when set to false, only patients WITHOUT explicit DKTK consent are
   *                        requested
   * @return the view
   */
  public static Query createUploadQuery(DateRestriction dateRestriction, boolean dktkFlagged) {
    ObjectFactory objectFactory = new ObjectFactory();

    Attribute attrDktkFlag = new Attribute();
    MdrIdDatatype mdrKeyDktkConsent = new MdrIdDatatype(
        ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_CONSENT_DKTK));
    attrDktkFlag.setMdrKey(mdrKeyDktkConsent.getLatestCentraxx());
    attrDktkFlag.setValue(objectFactory.createValue(Boolean.toString(dktkFlagged)));

    Eq equals = new Eq();
    equals.setAttribute(attrDktkFlag);
    And and = new And();
    // If the patients without explicit consent are wanted, check for dktkflag is false OR null
    if (dktkFlagged) {
      and.getAndOrEqOrLike().add(equals);
    } else {
      Or or = new Or();
      IsNotNull isNotNull = new IsNotNull();
      isNotNull.setMdrKey(mdrKeyDktkConsent.getLatestCentraxx());

      or.getAndOrEqOrLike().add(equals);
      or.getAndOrEqOrLike().add(isNotNull);
      and.getAndOrEqOrLike().add(or);
    }

    // Set the upper and lower bounds for the query if the date restrictions are set
    if (dateRestriction != null) {
      MdrIdDatatype mdrKeyUploadFrom = new MdrIdDatatype(
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_UPLOAD_FROM));
      Attribute attrFrom = new Attribute();
      attrFrom.setMdrKey(mdrKeyUploadFrom.getLatestCentraxx());
      attrFrom.setValue(objectFactory.createValue(dateRestriction.getLastUpload()));

      Gt greaterThan = new Gt();
      greaterThan.setAttribute(attrFrom);
      and.getAndOrEqOrLike().add(greaterThan);
      MdrIdDatatype mdrKeyUploadTo = new MdrIdDatatype(
          ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.MDR_KEY_UPLOAD_TO));
      Attribute attrTo = new Attribute();
      attrTo.setMdrKey(mdrKeyUploadTo.getLatestCentraxx());
      attrTo.setValue(objectFactory.createValue(dateRestriction.getServerTime()));

      Leq lessOrEqual = new Leq();
      lessOrEqual.setAttribute(attrTo);
      and.getAndOrEqOrLike().add(lessOrEqual);
    }

    Where where = new Where();
    if (!SamplyShareUtils.isNullOrEmpty(and.getAndOrEqOrLike())) {
      where.getAndOrEqOrLike().add(and);
    }
    Query query = new Query();
    query.setWhere(where);
    return query;
  }
}

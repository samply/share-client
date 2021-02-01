package de.samply.share.client.util.connector.idmanagement.utils;

import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Between;
import de.samply.share.model.common.ConditionType;
import de.samply.share.model.common.Eq;
import de.samply.share.model.common.Geq;
import de.samply.share.model.common.Gt;
import de.samply.share.model.common.In;
import de.samply.share.model.common.IsNotNull;
import de.samply.share.model.common.IsNull;
import de.samply.share.model.common.Leq;
import de.samply.share.model.common.Like;
import de.samply.share.model.common.Lt;
import de.samply.share.model.common.MultivalueAttribute;
import de.samply.share.model.common.Neq;
import de.samply.share.model.common.RangeAttribute;
import java.io.Serializable;

public class TermFactory {


  /**
   * Todo David.
   * @param serializable Todo David
   * @return Todo David
   */
  public static Term createTerm(Serializable serializable) {

    Term term = null;

    if (serializable != null) {

      if (serializable instanceof Eq) {

        Eq eqTerm = (Eq) serializable;
        term = new Term<Attribute>(attribute -> eqTerm.setAttribute(attribute),
            () -> eqTerm.getAttribute());

      } else if (serializable instanceof Like) {

        Like likeTerm = (Like) serializable;
        term = new Term<Attribute>(attribute -> likeTerm.setAttribute(attribute),
            () -> likeTerm.getAttribute());

      } else if (serializable instanceof Geq) {

        Geq geqTerm = (Geq) serializable;
        term = new Term<Attribute>(attribute -> geqTerm.setAttribute(attribute),
            () -> geqTerm.getAttribute());

      } else if (serializable instanceof Gt) {

        Gt gtTerm = (Gt) serializable;
        term = new Term<Attribute>(attribute -> gtTerm.setAttribute(attribute),
            () -> gtTerm.getAttribute());

      } else if (serializable instanceof Leq) {

        Leq leqTerm = (Leq) serializable;
        term = new Term<Attribute>(attribute -> leqTerm.setAttribute(attribute),
            () -> leqTerm.getAttribute());

      } else if (serializable instanceof Lt) {

        Lt ltTerm = (Lt) serializable;
        term = new Term<Attribute>(attribute -> ltTerm.setAttribute(attribute),
            () -> ltTerm.getAttribute());

      } else if (serializable instanceof Neq) {

        Neq neqTerm = (Neq) serializable;
        term = new Term<Attribute>(attribute -> neqTerm.setAttribute(attribute),
            () -> neqTerm.getAttribute());

      } else if (serializable instanceof In) {

        In inTerm = (In) serializable;
        term = new Term<MultivalueAttribute>(attribute -> inTerm.setMultivalueAttribute(attribute),
            () -> inTerm.getMultivalueAttribute());

      } else if (serializable instanceof Between) {

        Between betweenTerm = (Between) serializable;
        term = new Term<RangeAttribute>(attribute -> betweenTerm.setRangeAttribute(attribute),
            () -> betweenTerm.getRangeAttribute());

      } else if (serializable instanceof IsNull) {

        IsNull isNullTerm = (IsNull) serializable;
        term = new Term<String>(mdrKey -> isNullTerm.setMdrKey(mdrKey),
            () -> isNullTerm.getMdrKey());

      } else if (serializable instanceof IsNotNull) {

        IsNotNull isNotNullTerm = (IsNotNull) serializable;
        term = new Term<String>(mdrKey -> isNotNullTerm.setMdrKey(mdrKey),
            () -> isNotNullTerm.getMdrKey());

      }

    }

    return term;

  }

  /**
   * Todo David.
   * @param serializable Todo David
   * @return Todo David
   */
  public static Attribute getAttribute(Serializable serializable) {

    Term term = createTerm(serializable);
    Object attributeO = (term != null) ? term.getAttribute() : null;

    return (attributeO instanceof Attribute) ? ((Attribute) attributeO) : null;

  }

  /**
   * Todo David.
   * @param serializable Todo David
   * @return Todo David
   */
  public static MultivalueAttribute getMultivalueAttribute(Serializable serializable) {

    Term term = createTerm(serializable);
    Object attributeO = (term != null) ? term.getAttribute() : null;

    return (attributeO instanceof MultivalueAttribute) ? ((MultivalueAttribute) attributeO) : null;

  }

  /**
   * Todo David.
   * @param serializable Todo David
   * @return Todo David
   */
  public static RangeAttribute getRangeAttribute(Serializable serializable) {

    Term term = createTerm(serializable);
    Object attributeO = (term != null) ? term.getAttribute() : null;

    return (attributeO instanceof Attribute) ? ((RangeAttribute) attributeO) : null;

  }

  /**
   * Todo David.
   * @param serializable Todo David
   * @return Todo David
   */
  public static String getMdrKey(Serializable serializable) {

    Term term = createTerm(serializable);
    Object attributeO = (term != null) ? term.getAttribute() : null;

    String mdrKey = null;

    if (attributeO != null) {

      if (attributeO instanceof Attribute) {

        Attribute attribute = (Attribute) attributeO;
        mdrKey = attribute.getMdrKey();

      } else if (attributeO instanceof MultivalueAttribute) {

        MultivalueAttribute multivalueAttribute = (MultivalueAttribute) attributeO;
        mdrKey = multivalueAttribute.getMdrKey();

      } else if (attributeO instanceof RangeAttribute) {

        RangeAttribute rangeAttribute = (RangeAttribute) attributeO;
        mdrKey = rangeAttribute.getMdrKey();

      }

    }

    return mdrKey;

  }


  public static ConditionType getConditionType(Serializable serializable) {
    return (serializable != null && serializable instanceof ConditionType)
        ? ((ConditionType) serializable) : null;
  }


}

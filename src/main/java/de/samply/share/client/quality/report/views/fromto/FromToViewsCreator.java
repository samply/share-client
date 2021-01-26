package de.samply.share.client.quality.report.views.fromto;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.client.quality.report.MdrMappedElements;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.views.ViewsCreator;
import de.samply.share.client.quality.report.views.fromto.scheduler.ViewFromToScheduler;
import de.samply.share.client.util.db.ConfigurationUtil;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.model.common.And;
import de.samply.share.model.common.Attribute;
import de.samply.share.model.common.Geq;
import de.samply.share.model.common.Lt;
import de.samply.share.model.common.ObjectFactory;
import de.samply.share.model.common.Query;
import de.samply.share.model.common.View;
import de.samply.share.model.common.ViewFields;
import de.samply.share.model.common.Where;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;

public class FromToViewsCreator implements ViewsCreator {


  private final ViewFromToScheduler viewFromToScheduler;
  private final ObjectFactory objectFactory = new ObjectFactory();
  private MdrIgnoredElements ignoredElements;
  private MdrMappedElements mdrMappedElements;

  public FromToViewsCreator(ViewFromToScheduler viewFromToScheduler) {
    this.viewFromToScheduler = viewFromToScheduler;
  }

  @Override
  public List<View> createViews(Model model) {

    List<ViewFromTo> viewFromTos = viewFromToScheduler.createViewFromTos();
    return createViews(viewFromTos, model);

  }

  private List<View> createViews(List<ViewFromTo> viewFromTos, Model model) {

    List<View> viewsList = new ArrayList<>();

    ViewFields viewFields = createViewFields(model);

    for (ViewFromTo viewFromTo : viewFromTos) {

      View view = createView(viewFromTo, viewFields);
      viewsList.add(view);

    }

    return viewsList;

  }


  private ViewFields createViewFields(Model model) {

    ViewFields viewFields = objectFactory.createViewFields();

    for (MdrIdAndValidations mdrIdAndValidations : model.getMdrIdAndValidations()) {

      if (!isIgnoredElement(mdrIdAndValidations.getMdrId()) && isMappedElement(
          mdrIdAndValidations.getMdrId())) {

        String mdrKey = getMdrKey(mdrIdAndValidations.getMdrId());
        viewFields.getMdrKey().add(mdrKey);

      }

    }

    return viewFields;

  }

  private boolean isMappedElement(MdrIdDatatype mdrId) {
    return mdrMappedElements == null || mdrMappedElements.isMapped(mdrId);
  }

  private boolean isIgnoredElement(MdrIdDatatype mdrId) {
    return ignoredElements != null && ignoredElements.isIgnored(mdrId);
  }

  private boolean isSameMdrId(MdrIdDatatype m1, MdrIdDatatype m2) {
    return m1 != null && m2 != null && m1.getMajor().equalsIgnoreCase(m2.getMajor());
  }


  private View createView(ViewFromTo viewFromTo, ViewFields viewFields) {

    Query viewFromToQuery = createViewFromToQuery(viewFromTo);
    return createView(viewFromToQuery, viewFields);

  }

  private View createView(Query query, ViewFields viewFields) {

    View view = objectFactory.createView();

    view.setQuery(query);
    view.setViewFields(viewFields);

    return view;

  }

  private Query createViewFromToQuery(ViewFromTo viewFromTo) {

    Attribute fromAttribute = createFromAttribute(viewFromTo);
    Attribute toAttribute = createToAttribute(viewFromTo);

    Geq geq = objectFactory.createGeq();
    geq.setAttribute(fromAttribute);

    Lt lt = objectFactory.createLt();
    lt.setAttribute(toAttribute);

    And and = objectFactory.createAnd();
    List<Serializable> andOrEqOrLike = and.getAndOrEqOrLike();
    andOrEqOrLike.add(geq);
    andOrEqOrLike.add(lt);

    Where where = objectFactory.createWhere();
    List<Serializable> andOrEqOrLike1 = where.getAndOrEqOrLike();
    andOrEqOrLike1.add(and);

    Query query = objectFactory.createQuery();
    query.setWhere(where);

    return query;

  }

  private Attribute createFromAttribute(ViewFromTo viewFromTo) {

    MdrIdDatatype mdrId = getMdrKeyUploadFrom();
    return createAttribute(mdrId, viewFromTo.getFrom());

  }

  private Attribute createToAttribute(ViewFromTo viewFromTo) {

    MdrIdDatatype mdrId = getMdrKeyUploadTo();
    return createAttribute(mdrId, viewFromTo.getTo());

  }

  private MdrIdDatatype getMdrKeyUploadFrom() {
    return getMdrKey(EnumConfiguration.MDR_KEY_UPLOAD_FROM);
  }

  private MdrIdDatatype getMdrKeyUploadTo() {
    return getMdrKey(EnumConfiguration.MDR_KEY_UPLOAD_TO);
  }

  private MdrIdDatatype getMdrKey(EnumConfiguration enumConfiguration) {
    return new MdrIdDatatype(ConfigurationUtil.getConfigurationElementValue(enumConfiguration));
  }

  private String getMdrKey(MdrIdDatatype mdrIdDatatype) {
    return mdrIdDatatype.getLatestCentraxx();
  }

  private Attribute createAttribute(MdrIdDatatype mdrId, String value) {

    Attribute attribute = objectFactory.createAttribute();
    attribute.setMdrKey(getMdrKey(mdrId));

    JAXBElement<String> value1 = objectFactory.createValue(value);
    attribute.setValue(value1);

    return attribute;

  }


  public void setIgnoredElements(MdrIgnoredElements ignoredElements) {
    this.ignoredElements = ignoredElements;
  }

  public void setMdrMappedElements(MdrMappedElements mdrMappedElements) {
    this.mdrMappedElements = mdrMappedElements;
  }

}

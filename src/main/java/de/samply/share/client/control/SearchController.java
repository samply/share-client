package de.samply.share.client.control;

import de.samply.share.common.control.uiquerybuilder.AbstractSearchController;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * The only purpose of this is to offer an implementation of the search controller, so that the
 * operator list is found. TODO change this in share common!
 */
@ManagedBean(name = "SearchController")
@ViewScoped
public class SearchController extends AbstractSearchController {

  @Override
  public String getSerializedQuery() {
    return null;
  }

  @Override
  public void setSerializedQuery(String s) {

  }

  @Override
  public String onStoreAndRelease() {
    return null;
  }

  @Override
  public String onSubmit() {
    return null;
  }
}

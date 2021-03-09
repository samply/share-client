package de.samply.share.client.feature;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum ClientFeature implements Feature {

  @Label("DKTK Central Search")
  DKTK_CENTRAL_SEARCH,

  @Label("BBMRI Directory Sync")
  BBMRI_DIRECTORY_SYNC,

  @Label("NNGM CTS")
  NNGM_CTS,

  @Label("NNGM ENCRYPT_ID")
  NNGM_ENCRYPT_ID,

  @Label("Set Site Name")
  SET_SITE_NAME;

  public boolean isActive() {
    return FeatureContext.getFeatureManager().isActive(this);
  }

}

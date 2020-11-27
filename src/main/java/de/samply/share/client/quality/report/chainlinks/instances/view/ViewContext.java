package de.samply.share.client.quality.report.chainlinks.instances.view;

import de.samply.share.model.common.View;

public interface ViewContext {

  public View getView();

  public void setView(View view);

  public void setLocationUrl(String locationUrl);

}

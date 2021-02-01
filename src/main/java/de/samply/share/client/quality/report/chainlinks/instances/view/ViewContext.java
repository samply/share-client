package de.samply.share.client.quality.report.chainlinks.instances.view;

import de.samply.share.model.common.View;

public interface ViewContext {

  View getView();

  void setView(View view);

  void setLocationUrl(String locationUrl);

}

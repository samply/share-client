package de.samply.share.client.quality.report.views;

import de.samply.share.client.quality.report.model.Model;
import de.samply.share.model.common.View;
import java.util.List;

public interface ViewsCreator {

  List<View> createViews(Model model);

}

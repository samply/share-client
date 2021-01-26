package de.samply.share.client.quality.report.chainlinks.instances.view;

import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.views.ViewsCreator;
import de.samply.share.model.common.View;
import java.util.List;

public class CreateViewsChainLink<I extends ChainLinkItem & ViewsContext> extends ChainLink<I> {

  private final Model model;
  private final ViewsCreator viewsCreator;

  /**
   * Todo.
   *
   * @param model        Todo.
   * @param viewsCreator Todo.
   */
  public CreateViewsChainLink(Model model, ViewsCreator viewsCreator) {

    this.model = model;
    this.viewsCreator = viewsCreator;

  }

  @Override
  protected String getChainLinkId() {
    return "Views Creator";
  }

  @Override
  protected I process(I item) throws ChainLinkException {

    List<View> views = viewsCreator.createViews(model);
    item.setViews(views);

    return item;

  }


}

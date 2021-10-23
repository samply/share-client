package de.samply.share.client.quality.report.views.fromto.scheduler;

import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import java.util.Collections;
import java.util.List;

/**
 * A scheduler that always creates one {@link ViewFromTo} with a date range of 1900 - 2100.
 *
 * <p>It's meant to load all data at once.
 */
public enum ViewFromToSchedulerAll implements ViewFromToScheduler {

  INSTANCE;

  @Override
  public List<ViewFromTo> createViewFromTos() {
    return Collections.singletonList(new ViewFromTo("1900-01-01T00:00:00+00:00",
        "2100-01-01T00:00:00+00:00"));
  }
}

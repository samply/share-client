package de.samply.share.client.quality.report.views.fromto.scheduler;

import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class ViewFromToSchedulerByYear implements ViewFromToScheduler {

  private final ViewFromToFactory viewFromToFactory;
  private int years = 20;

  public ViewFromToSchedulerByYear(ViewFromToFactory viewFromToFactory) {
    this.viewFromToFactory = Objects.requireNonNull(viewFromToFactory);
  }

  @Override
  public List<ViewFromTo> createViewFromTos() {
    List<ViewFromTo> viewFromToList = new ArrayList<>();

    Calendar calendar = new GregorianCalendar();
    int currentYear = calendar.get(Calendar.YEAR);

    for (int i = 0; i < years; i++) {
      ViewFromTo viewFromToYear = viewFromToFactory.createYear(currentYear - i);
      viewFromToList.add(viewFromToYear);
    }

    return viewFromToList;
  }


  public void setYears(int years) {
    this.years = years;
  }
}

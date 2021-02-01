package de.samply.share.client.quality.report.views.fromto.scheduler;

import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ViewFromToSchedulerByYearImpl implements ViewFromToScheduler {

  private int years = 20;
  private final ViewFromToFactory viewFromToFactory;

  public ViewFromToSchedulerByYearImpl(ViewFromToFactory viewFromToFactory) {
    this.viewFromToFactory = viewFromToFactory;
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

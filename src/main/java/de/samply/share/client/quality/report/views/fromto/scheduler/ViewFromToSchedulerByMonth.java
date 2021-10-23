package de.samply.share.client.quality.report.views.fromto.scheduler;

import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class ViewFromToSchedulerByMonth implements ViewFromToScheduler {

  private final ViewFromToFactory viewFromToFactory;
  private int numberOfYears = 20;
  private int groupsModul = 5;

  public ViewFromToSchedulerByMonth(ViewFromToFactory viewFromToFactory) {
    this.viewFromToFactory = Objects.requireNonNull(viewFromToFactory);
  }

  @Override
  public List<ViewFromTo> createViewFromTos() {
    return createViewFroms(getLastYearsSorted());
  }

  private List<ViewFromTo> createViewFroms(List<Integer> lastYearsSorted) {
    List<ViewFromTo> viewFromToList = new ArrayList<>();

    Calendar calendar = new GregorianCalendar();
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentYear = calendar.get(Calendar.YEAR);

    for (int month = 0; month < 12; month++) {
      for (int year : lastYearsSorted) {
        if (year < currentYear || month <= currentMonth) {
          viewFromToList.add(viewFromToFactory.createMonth(year, month));
        }
      }
    }

    return viewFromToList;
  }

  private List<Integer> getLastYearsSorted() {
    List<Integer> years = new ArrayList<>();

    Calendar calendar = new GregorianCalendar();
    int year = calendar.get(Calendar.YEAR);

    for (int groupNumber = 0; groupNumber < groupsModul; groupNumber++) {
      for (int i = year; i > year - numberOfYears; i--) {
        if (i % groupsModul == groupNumber) {
          years.add(i);
        }
      }
    }

    return years;
  }

  public void setGroupsModul(int groupsModul) {
    this.groupsModul = groupsModul;
  }

  public void setNumberOfYears(int numberOfYears) {
    this.numberOfYears = numberOfYears;
  }
}

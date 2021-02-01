package de.samply.share.client.quality.report.views.fromto.scheduler;

import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ViewFromToSchedulerByMonthImpl implements ViewFromToScheduler {

  private int numberOfYears = 20;
  private int groupsModul = 5;
  private final ViewFromToFactory viewFromToFactory;

  public ViewFromToSchedulerByMonthImpl(ViewFromToFactory viewFromToFactory) {
    this.viewFromToFactory = viewFromToFactory;
  }

  @Override
  public List<ViewFromTo> createViewFromTos() {

    List<Integer> lastYearsSorted = getLastYearsSorted();
    return createViewFroms(lastYearsSorted);

  }

  private List<ViewFromTo> createViewFroms(List<Integer> lastYearsSorted) {

    List<ViewFromTo> viewFromToList = new ArrayList<>();

    Calendar calendar = new GregorianCalendar();
    int currentMonth = calendar.get(Calendar.MONTH);
    int currentYear = calendar.get(Calendar.YEAR);

    for (int month = 0; month < 12; month++) {

      for (int year : lastYearsSorted) {

        if (year < currentYear || month <= currentMonth) {

          ViewFromTo viewFromTo = viewFromToFactory.createMonth(month, year);
          viewFromToList.add(viewFromTo);

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

  //    public static void main(String[] args) {
  //
  //        BasicViewFromToScheduler basicViewFromToScheduler = new BasicViewFromToScheduler();
  //        basicViewFromToScheduler.setViewFromToFactory(new ViewFromToFactory());
  //
  //        List<ViewFromTo> viewFroms = basicViewFromToScheduler.createViewFroms();
  //
  //    }

}

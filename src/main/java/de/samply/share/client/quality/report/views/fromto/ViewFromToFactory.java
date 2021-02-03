package de.samply.share.client.quality.report.views.fromto;

import de.samply.share.client.util.Utils;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ViewFromToFactory {


  /**
   * Creates View between two dates.
   *
   * @param from lower date.
   * @param to   upper date.
   * @return View From - To.
   */
  public ViewFromTo createViewFromTo(Date from, Date to) {

    String fromS = convert(from);
    String toS = convert(to);

    return new ViewFromTo(fromS, toS);

  }

  /**
   * Create View between dates for first day to last day of month of a year.
   *
   * @param month month.
   * @param year  year.
   * @return View From-To.
   */
  public ViewFromTo createMonth(int month, int year) {

    Calendar calendarStart = createFirstDayOfMonth(month, year);
    Calendar calendarEnd = (Calendar) calendarStart.clone();
    calendarEnd.add(Calendar.MONTH, 1);

    return createViewFromTo(calendarStart.getTime(), calendarEnd.getTime());

  }

  /**
   * Create View between first day of a year and last day of the year.
   *
   * @param year year.
   * @return View from-to.
   */
  public ViewFromTo createYear(int year) {

    Calendar calendarStart = createFirstDayOfYear(year);
    Calendar calendarEnd = createFirstDayOfYear(year + 1);

    return createViewFromTo(calendarStart.getTime(), calendarEnd.getTime());

  }

  private Calendar createFirstDayOfYear(int year) {

    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);

    return calendar;

  }


  private Calendar createFirstDayOfMonth(int month, int year) {

    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);

    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);

    return calendar;

  }

  private String convert(Date date) {
    return Utils.convertDate2(date);
  }


}

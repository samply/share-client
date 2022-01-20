package de.samply.share.client.quality.report.views.fromto;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

public class ViewFromToFactory {

  /**
   * Creates a view between the first day of {@code year} and the first day of the next year.
   *
   * @param year year.
   * @return View from-to.
   */
  public ViewFromTo createYear(int year) {
    Year start = Year.of(year);
    return createViewFromTo(start.atDay(1), start.plusYears(1).atDay(1));
  }

  /**
   * Create a view between the first day of {@code month} in {@code year} and the first day of the
   * next month.
   *
   * @param year  year.
   * @param month month.
   * @return View From-To.
   */
  public ViewFromTo createMonth(int year, int month) {
    YearMonth start = YearMonth.of(year, month);
    return createViewFromTo(start.atDay(1), start.plusMonths(1).atDay(1));
  }

  private ViewFromTo createViewFromTo(LocalDate from, LocalDate to) {
    return new ViewFromTo(from.atStartOfDay().toInstant(UTC).toString(),
        to.atStartOfDay().toInstant(UTC).toString());
  }
}

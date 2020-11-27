package de.samply.share.client.quality.report.views.fromto;/*
* Copyright (C) 2017 Medizinische Informatik in der Translationalen Onkologie,
* Deutsches Krebsforschungszentrum in Heidelberg
*
* This program is free software; you can redistribute it and/or modify it under
* the terms of the GNU Affero General Public License as published by the Free
* Software Foundation; either version 3 of the License, or (at your option) any
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program; if not, see http://www.gnu.org/licenses.
*
* Additional permission under GNU GPL version 3 section 7:
*
* If you modify this Program, or any covered work, by linking or combining it
* with Jersey (https://jersey.java.net) (or a modified version of that
* library), containing parts covered by the terms of the General Public
* License, version 2.0, the licensors of this Program grant you additional
* permission to convey the resulting work.
*/

import de.samply.share.client.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ViewFromToFactory {


    public ViewFromTo createViewFromTo (Date from, Date to) {

        String sFrom = convert(from);
        String sTo = convert(to);

        return new ViewFromTo(sFrom, sTo);

    }

    public ViewFromTo createMonth (int month, int year) {

        Calendar calendarStart = createFirstDayOfMonth(month, year);
        Calendar calendarEnd = (Calendar) calendarStart.clone();
        calendarEnd.add(Calendar.MONTH, 1);

        return createViewFromTo(calendarStart.getTime(), calendarEnd.getTime());

    }

    public ViewFromTo createYear (int year){

        Calendar calendarStart = createFirstDayOfYear(year);
        Calendar calendarEnd = createFirstDayOfYear(year + 1);

        return createViewFromTo(calendarStart.getTime(), calendarEnd.getTime());

    }

    private Calendar createFirstDayOfYear (int year){

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;

    }



    private Calendar createFirstDayOfMonth (int month, int year){


        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;

    }

    private String convert (Date date) {
        return Utils.convertDate2(date);
    }


}

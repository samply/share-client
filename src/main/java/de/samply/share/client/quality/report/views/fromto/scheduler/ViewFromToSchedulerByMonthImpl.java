package de.samply.share.client.quality.report.views.fromto.scheduler;/*
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


import de.samply.share.client.quality.report.views.fromto.ViewFromTo;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ViewFromToSchedulerByMonthImpl implements ViewFromToScheduler {

    private int numberOfYears = 20;
    private int groupsModul = 5;
    private ViewFromToFactory viewFromToFactory;

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

        for (int month = 0; month < 12; month++){

            for (int year : lastYearsSorted){

                if (year < currentYear || month <= currentMonth) {

                    ViewFromTo viewFromTo = viewFromToFactory.createMonth(month, year);
                    viewFromToList.add(viewFromTo);

                }

            }
        }

        return viewFromToList;
    }

    private List<Integer> getLastYearsSorted (){

        List<Integer> years = new ArrayList<>();

        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);

        for (int groupNumber = 0; groupNumber < groupsModul; groupNumber++){

            for (int i = year ; i > year - numberOfYears; i--){
                if (i % groupsModul == groupNumber){
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

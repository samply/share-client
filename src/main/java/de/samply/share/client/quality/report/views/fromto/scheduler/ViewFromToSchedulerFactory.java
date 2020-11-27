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

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.quality.report.views.fromto.ViewFromToFactory;
import de.samply.share.client.util.db.ConfigurationUtil;

public class ViewFromToSchedulerFactory {


    private ViewFromToFactory viewFromToFactory = new ViewFromToFactory();


    public enum ViewFromToSchedulerFormat{

        BY_YEAR (EnumConfiguration.QUALITY_REPORT_SCHEDULER_BY_YEAR.name()),
        BY_MONTH (EnumConfiguration.QUALITY_REPORT_SCHEDULER_BY_MONTH.name());

        String title;

        ViewFromToSchedulerFormat(String title) {
            this.title = title;
        }

        public static ViewFromToSchedulerFormat getDefault(){
            return BY_YEAR;
        }

        public static ViewFromToSchedulerFormat getViewFromToSchedulerFormat(String format){

            for (ViewFromToSchedulerFormat viewFromToSchedulerFormat : values()){

                if (viewFromToSchedulerFormat.title.equals(format)){
                    return viewFromToSchedulerFormat;
                }
            }

            return null;

        }

    }

    public ViewFromToScheduler createViewFromToScheduler (){

        String format = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_SCHEDULER_FORMAT);

        ViewFromToSchedulerFormat viewFromToSchedulerFormat = ViewFromToSchedulerFormat.getViewFromToSchedulerFormat(format);
        if (viewFromToSchedulerFormat == null){
            viewFromToSchedulerFormat = ViewFromToSchedulerFormat.getDefault();
        }

        switch (viewFromToSchedulerFormat){

            case BY_MONTH :
                return createViewFromToSchedulerByMonthImpl();
            case BY_YEAR:
                return createViewFromToSchedulerByYearImpl();
            default:
                return null;

        }


    }

    private ViewFromToScheduler createViewFromToSchedulerByMonthImpl(){

        ViewFromToSchedulerByMonthImpl viewFromToSchedulerByMonth = new ViewFromToSchedulerByMonthImpl(viewFromToFactory);

        String sGroupsModul = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_GROUP_MODUL);
        Integer groupsModul = convert(sGroupsModul);
        String sYears = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_SCHEDULER_YEARS);
        Integer years = convert (sYears);

        if (groupsModul != null){
            viewFromToSchedulerByMonth.setGroupsModul(groupsModul);
        }

        if (years != null){
            viewFromToSchedulerByMonth.setNumberOfYears(years);
        }


        return viewFromToSchedulerByMonth;
    }

    private ViewFromToScheduler createViewFromToSchedulerByYearImpl(){

        ViewFromToSchedulerByYearImpl viewFromToSchedulerByYear = new ViewFromToSchedulerByYearImpl(viewFromToFactory);

        String sYears = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_SCHEDULER_YEARS);
        Integer years = convert (sYears);

        if (years != null){
            viewFromToSchedulerByYear.setYears(years);
        }

        return viewFromToSchedulerByYear;

    }

    Integer convert (String number){

        try{
            return Integer.valueOf(number);

        } catch (Exception e){
            return null;
        }
    }





}

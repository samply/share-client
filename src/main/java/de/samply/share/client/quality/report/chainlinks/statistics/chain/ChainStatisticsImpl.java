package de.samply.share.client.quality.report.chainlinks.statistics.chain;/*
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

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticsConsumer;
import de.samply.share.client.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChainStatisticsImpl implements ChainStatistics {

    private final static double SECONDS_IN_A_MINUTE = 60.0;
    private final static int MINUTES_IN_AN_HOUR = 60;
    private final static int MINUTES_IN_A_DAY = MINUTES_IN_AN_HOUR * 24;
    private final static double NANOSECONDS_IN_A_SECOND = 1000000000.0;


    private List<ChainLinkStatisticsConsumer> chainLinkStatisticsConsumerList = new ArrayList<>();
    private long startNanoTime;
    private Date startDate;


    {
        startNanoTime = System.nanoTime();
        startDate = new Date();

    }

    public void addChainLinkStatisticsConsumer (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer){

        if (chainLinkStatisticsConsumer != null){
            chainLinkStatisticsConsumerList.add(chainLinkStatisticsConsumer);
        }

    }


    @Override
    public int getPercentage() {

        long elapsedTime = System.nanoTime() - startNanoTime;
        long estimatedNanoTimeToBeCompleted = getEstimatedNanoTimeToBeCompleted();

        Double percentage = 100.0 * elapsedTime / (elapsedTime + estimatedNanoTimeToBeCompleted);

        return percentage.intValue();

    }

    @Override
    public String getEstimatedTimeToBeCompleted() {

        int estimatedTimeToBeCompletedInMinutes = getEstimatedTimeToBeCompletedInMinutes();
        return printTimeInMinutes(estimatedTimeToBeCompletedInMinutes);

    }

    private String printTimeInMinutes(int timeInMinutes){

        StringBuilder stringBuilder = new StringBuilder();

        if (timeInMinutes >= MINUTES_IN_A_DAY) {

            int estimatedTimeToBeCompletedInDays = timeInMinutes / MINUTES_IN_A_DAY;

            stringBuilder.append(estimatedTimeToBeCompletedInDays);
            stringBuilder.append(" days ");

            timeInMinutes = timeInMinutes - estimatedTimeToBeCompletedInDays * MINUTES_IN_A_DAY;

        }

        if (timeInMinutes >= MINUTES_IN_AN_HOUR){

            int estimatedTimeToBeCompletedInHours = timeInMinutes / MINUTES_IN_AN_HOUR;

            stringBuilder.append(estimatedTimeToBeCompletedInHours);
            stringBuilder.append(" h ");

            timeInMinutes = timeInMinutes - estimatedTimeToBeCompletedInHours * MINUTES_IN_AN_HOUR;


        }

        stringBuilder.append(timeInMinutes);
        stringBuilder.append(" min");


        return stringBuilder.toString();

    }


    private int getEstimatedTimeToBeCompletedInMinutes() {

        long estimatedNanoTimeToBeCompleted = getEstimatedNanoTimeToBeCompleted();
        return convertNanosecondsToMinutes(estimatedNanoTimeToBeCompleted);

    }

    private int convertNanosecondsToMinutes (long nanoseconds){

        double dNanoseconds = (double) nanoseconds;

        double dMinutes = dNanoseconds / (SECONDS_IN_A_MINUTE * NANOSECONDS_IN_A_SECOND);

        return Double.valueOf(dMinutes).intValue();

    }


    private long getEstimatedNanoTimeToBeCompleted() {

        long estimatedTimeToBeCompleted = 0;


        for (int i = chainLinkStatisticsConsumerList.size() - 1 ; i >= 0; i--){

            ChainLinkStatisticsConsumer chainLinkStatisticsConsumer = chainLinkStatisticsConsumerList.get(i);

            if (!chainLinkStatisticsConsumer.isFinalized()){

                estimatedTimeToBeCompleted += chainLinkStatisticsConsumer.getRemainingNanoTime();

                if (chainLinkStatisticsConsumer.isProcessingElements()){
                    return estimatedTimeToBeCompleted;
                }

            }

        }

        return estimatedTimeToBeCompleted;
    }

    @Override
    public boolean isFinalized() {

        for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer : chainLinkStatisticsConsumerList){

            if (!chainLinkStatisticsConsumer.isFinalized()){
                return false;
            }

        }

        return true;
    }

    @Override
    public List<String> getMessages() {

        List<String> messages = new ArrayList<>();
        for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer : chainLinkStatisticsConsumerList){

            if (!chainLinkStatisticsConsumer.isFinalized() && chainLinkStatisticsConsumer.isProcessingElements()){
                messages.add(chainLinkStatisticsConsumer.getMessage());
            }

        }

        return messages;

    }

    @Override
    public boolean isAccurate() {

        return isConsumerWithMaximalWorkloadBeingProcessed();

    }

    private boolean isConsumerWithMaximalWorkloadBeingProcessed(){

        ChainLinkStatisticsConsumer consumerWithMaximalWorkload = null;

        for (ChainLinkStatisticsConsumer chainLinkStatisticsConsumer : chainLinkStatisticsConsumerList){


            if (consumerWithMaximalWorkload == null){

                consumerWithMaximalWorkload = chainLinkStatisticsConsumer;

            } else{

                if (chainLinkStatisticsConsumer.getNumberOfItems() > consumerWithMaximalWorkload.getNumberOfItems()){
                    consumerWithMaximalWorkload = chainLinkStatisticsConsumer;
                }

            }


        }

        return consumerWithMaximalWorkload.isProcessingElements() || consumerWithMaximalWorkload.isFinalized();

    }

    @Override
    public String getTimeConsumed(){

        long currentNanoTime = System.nanoTime();
        long elapsedNanoTime = currentNanoTime - startNanoTime;

        int elapsedTimeInMinutes = convertNanosecondsToMinutes(elapsedNanoTime);

        return printTimeInMinutes(elapsedTimeInMinutes);

    }

    @Override
    public String getStartTime(){
        return Utils.convertDate(startDate);
    }

}

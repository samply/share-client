package de.samply.share.client.quality.report.chainlinks.statistics.chainlink;/*
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

import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManager;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManagerException;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsParameters;

public class ChainLinkStatistics implements ChainLinkStatisticsConsumer, ChainLinkStatisticsProducer {

    private ChainLinkStatisticKey chainLinkStatisticKey;
    private ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager;

    private long averageNanoTimeOfProcess = 18L * 1000000000L;
    private long totalProcessedNanoTime;
    private int numberOfProcessedItems;
    private int numberOfItemsToBeProcessed = 1;
    private boolean isFinalized = false;
    private boolean isFirstElementBeingProcessed = false;


    public ChainLinkStatistics(ChainLinkStatisticKey chainLinkStatisticKey, ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager) {

        this.chainLinkStatisticKey = chainLinkStatisticKey;
        this.chainLinkStaticStatisticsFileManager = chainLinkStaticStatisticsFileManager;

    }


    @Override
    public void addTimeProProcess(long timeProProcess, boolean isToBeRepeated) {

        totalProcessedNanoTime += timeProProcess;
        if (!isToBeRepeated) {
            numberOfProcessedItems++;
        }

    }

    @Override
    public void setNumberOfElementsToBeProcessed(int numberOfElementsToBeProcessed) {
        this.numberOfItemsToBeProcessed = numberOfElementsToBeProcessed;
    }

    @Override
    public void finalizeProducer() throws ChainLinkStatisticsException {

        isFinalized = true;
        updateChainLinkStaticStatisticsFileManager();

    }

    public boolean isProcessingElements(){
        return isFirstElementBeingProcessed && numberOfItemsToBeProcessed > 0;
    }

    @Override
    public int getNumberOfItems() {
        return numberOfItemsToBeProcessed + numberOfProcessedItems;
    }

    private void updateChainLinkStaticStatisticsFileManager() throws ChainLinkStatisticsException {

        try {

            updateChainLinkStaticStatisticsFileManagerWithoutExceptionManagement();

        } catch (ChainLinkStaticStatisticsFileManagerException e) {
            throw new ChainLinkStatisticsException(e);
        }
    }

    private void updateChainLinkStaticStatisticsFileManagerWithoutExceptionManagement () throws ChainLinkStaticStatisticsFileManagerException {

        ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters = new ChainLinkStaticStatisticsParameters();

        chainLinkStaticStatisticsParameters.setAverageNanoTime(getAverageNanoTimeOfProcess());
        chainLinkStaticStatisticsParameters.setAverageNumberOfItems(numberOfProcessedItems);

        chainLinkStaticStatisticsFileManager.update(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);

    }


    @Override
    public long getRemainingNanoTime() {

        long averageNanoTimeOfProcess = getAverageNanoTimeOfProcess();

        return (!isFinalized && numberOfItemsToBeProcessed == 0) ?  averageNanoTimeOfProcess : numberOfItemsToBeProcessed * averageNanoTimeOfProcess;

    }

    @Override
    public String getMessage() {
        return chainLinkStatisticKey.getMessage();
    }

    private long getAverageNanoTimeOfProcess (){


        return (numberOfProcessedItems == 0) ? averageNanoTimeOfProcess : totalProcessedNanoTime / numberOfProcessedItems;

    }

    @Override
    public boolean isFinalized() {
        return isFinalized;
    }

    public void setAverageNanoTimeOfProcess(Long averageNanoTimeOfProcess) {

        if (averageNanoTimeOfProcess != null) {
            this.averageNanoTimeOfProcess = averageNanoTimeOfProcess;
        }

    }

    public void setFirstElementBeingProcessed() {
        isFirstElementBeingProcessed = true;
    }

}

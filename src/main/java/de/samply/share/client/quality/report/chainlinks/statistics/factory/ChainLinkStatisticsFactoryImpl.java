package de.samply.share.client.quality.report.chainlinks.statistics.factory;/*
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

import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatistics;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManager;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsFileManagerException;
import de.samply.share.client.quality.report.chainlinks.statistics.file.ChainLinkStaticStatisticsParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChainLinkStatisticsFactoryImpl implements ChainLinkStatisticsFactory{

    protected static final Logger logger = LogManager.getLogger(ChainLinkStatisticsFactoryImpl.class);
    private ChainLinkStaticStatisticsFileManager chainLinkStaticStatisticsFileManager;


    public ChainLinkStatisticsFactoryImpl(ChainLinkStaticStatisticsFileManager statisticsFileManager) {
        this.chainLinkStaticStatisticsFileManager = statisticsFileManager;
    }


    @Override
    public ChainLinkStatistics createChainLinkStatistics(ChainLinkStatisticKey chainLinkStatisticKey) throws ChainLinkStatisticsFactoryException {

        ChainLinkStatistics chainLinkStatistics = new ChainLinkStatistics(chainLinkStatisticKey, chainLinkStaticStatisticsFileManager);

        ChainLinkStaticStatistics chainLinkStaticStatistics = readChainLinkStaticStatistics();
        ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters = chainLinkStaticStatistics.get(chainLinkStatisticKey);

        if (chainLinkStatistics != null && chainLinkStaticStatisticsParameters != null){

            Long averageNanoTime = chainLinkStaticStatisticsParameters.getAverageNanoTime();
            Integer averageNumberOfItems = chainLinkStaticStatisticsParameters.getAverageNumberOfItems();

            chainLinkStatistics.setNumberOfElementsToBeProcessed(averageNumberOfItems);
            chainLinkStatistics.setAverageNanoTimeOfProcess(averageNanoTime);

        }



        return chainLinkStatistics;

    }

    private ChainLinkStaticStatistics readChainLinkStaticStatistics() throws ChainLinkStatisticsFactoryException {

        try {
            return chainLinkStaticStatisticsFileManager.read();
        } catch (ChainLinkStaticStatisticsFileManagerException e) {

            logger.info("Static statistics file could not be read");
            logger.info("Create new static statistics file");


            return createNewChainLinkStaticStatisticsFile();

            //throw new ChainLinkStatisticsFactoryException(e);

        }

    }

    private ChainLinkStaticStatistics createNewChainLinkStaticStatisticsFile () throws ChainLinkStatisticsFactoryException {


        try {
            return createNewChainLinkStaticStatisticsFile_withoutExceptionManagement();
        } catch (ChainLinkStaticStatisticsFileManagerException e) {

            logger.error("Error creating static statistics file");
            throw new ChainLinkStatisticsFactoryException(e);
        }

    }

    private ChainLinkStaticStatistics createNewChainLinkStaticStatisticsFile_withoutExceptionManagement () throws ChainLinkStaticStatisticsFileManagerException {

        ChainLinkStaticStatistics chainLinkStaticStatistics = new ChainLinkStaticStatistics();
        chainLinkStaticStatisticsFileManager.write(chainLinkStaticStatistics);

        return chainLinkStaticStatistics;

    }

}

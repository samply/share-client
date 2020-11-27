package de.samply.share.client.quality.report.chainlinks.statistics.file;/*
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
import de.samply.share.client.quality.report.chainlinks.statistics.chainlink.ChainLinkStatisticKey;
import de.samply.share.client.quality.report.file.manager.anonym.AnonymTxtColumnFileManager;
import de.samply.share.client.quality.report.file.manager.anonym.AnonymTxtColumnFileManagerException;
import de.samply.share.client.quality.report.file.manager.anonym.AnonymTxtColumnFileManagerImpl;
import de.samply.share.client.quality.report.file.txtcolumn.AnonymTxtColumn;
import de.samply.share.client.util.db.ConfigurationUtil;

import java.io.File;


public class ChainLinkStaticStatisticsFileManager {


    private AnonymTxtColumnFileManager anonymTxtColumnFileManager;

    public ChainLinkStaticStatisticsFileManager() {

        String filePath = getFilePath();
        anonymTxtColumnFileManager = new AnonymTxtColumnFileManagerImpl(filePath);

    }


    public synchronized void write(ChainLinkStaticStatistics chainLinkStatisticTxtColumn) throws ChainLinkStaticStatisticsFileManagerException {

        try {

            AnonymTxtColumn anonymTxtColumn = convert(chainLinkStatisticTxtColumn);
            anonymTxtColumnFileManager.write(anonymTxtColumn);

        } catch (AnonymTxtColumnFileManagerException e) {
            throw new ChainLinkStaticStatisticsFileManagerException(e);
        }

    }

    private AnonymTxtColumn convert (ChainLinkStaticStatistics chainLinkStaticStatistics){

        AnonymTxtColumn anonymTxtColumn = new AnonymTxtColumn();

        for (ChainLinkStatisticKey chainLinkStatisticKey : ChainLinkStatisticKey.values()){

            ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters = chainLinkStaticStatistics.get(chainLinkStatisticKey);

            if (chainLinkStaticStatisticsParameters != null){
                anonymTxtColumn.addElement(chainLinkStatisticKey.getFileKey(), chainLinkStaticStatisticsParameters.toString());
            }

        }

        return anonymTxtColumn;

    }

    private ChainLinkStaticStatistics convert (AnonymTxtColumn anonymTxtColumn){

        ChainLinkStaticStatistics chainLinkStaticStatistics = new ChainLinkStaticStatistics();

        if (anonymTxtColumn != null){

            for (ChainLinkStatisticKey chainLinkStatisticKey : ChainLinkStatisticKey.values()){
                String element = anonymTxtColumn.getElement(chainLinkStatisticKey.getFileKey());
                if (element != null){

                    ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters = new ChainLinkStaticStatisticsParameters(element);
                    chainLinkStaticStatistics.put(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);

                }
            }

        }

        return chainLinkStaticStatistics;
    }


    public synchronized void update (ChainLinkStatisticKey chainLinkStatisticKey, ChainLinkStaticStatisticsParameters chainLinkStaticStatisticsParameters) throws ChainLinkStaticStatisticsFileManagerException {

        if (chainLinkStaticStatisticsParameters != null) {

            ChainLinkStaticStatistics chainLinkStaticStatistics = read();
            chainLinkStaticStatistics.put(chainLinkStatisticKey, chainLinkStaticStatisticsParameters);
            write(chainLinkStaticStatistics);

        }

    }

    public synchronized ChainLinkStaticStatistics read () throws ChainLinkStaticStatisticsFileManagerException {


        try {

            AnonymTxtColumn anonymTxtColumn = anonymTxtColumnFileManager.read();
            return convert(anonymTxtColumn);


        } catch (AnonymTxtColumnFileManagerException e) {
            throw new ChainLinkStaticStatisticsFileManagerException(e);
        }

    }


    private String getFilePath() {

        String filePath = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_DIRECTORY);
        String filename = ConfigurationUtil.getConfigurationElementValue(EnumConfiguration.QUALITY_REPORT_STATISTICS_FILENAME);

        return  (filePath != null && filename != null) ? filePath + File.separator + filename : null;

    }





}

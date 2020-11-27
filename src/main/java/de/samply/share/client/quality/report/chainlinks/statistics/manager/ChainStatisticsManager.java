package de.samply.share.client.quality.report.chainlinks.statistics.manager;/*
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

import de.samply.share.client.quality.report.chainlinks.statistics.chain.ChainStatistics;

public class ChainStatisticsManager {

    private static final int MAX_NUMBER_OF_CALLS_TO_SIGNALIZE_CHANGED_STATUS = 10;

    private ChainStatistics chainStatistics;
    private boolean isStatusChanged = false;
    private int numberOfCallsToSignalizeChangedStatus = 0;

    public ChainStatistics getChainStatistics(){

        if (chainStatistics != null && chainStatistics.isFinalized()){

            chainStatistics = null;
            setStatusChanged();

        } else {

            if (numberOfCallsToSignalizeChangedStatus > 0){
                numberOfCallsToSignalizeChangedStatus --;
            }else {
                isStatusChanged = false;
            }

        }

        return chainStatistics;
    }

    public void setChainStatistics(ChainStatistics chainStatistics) {

        this.chainStatistics = chainStatistics;
        setStatusChanged();

    }

    private void setStatusChanged(){
        isStatusChanged = true;
        numberOfCallsToSignalizeChangedStatus = MAX_NUMBER_OF_CALLS_TO_SIGNALIZE_CHANGED_STATUS;
    }

    public boolean isStatusChanged(){

        getChainStatistics(); // update status of statistics

        return this.isStatusChanged;

    }

}

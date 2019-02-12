package de.samply.share.client.quality.report.chainlinks.instances.result;/*
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


import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.LdmChainLink;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequesterException;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;
import de.samply.share.model.ccp.QueryResult;


public class GetResults_ChainLink<I extends ChainLinkItem & ResultContext> extends LdmChainLink<I> {



    public GetResults_ChainLink(LocalDataManagementRequester localDataManagementRequester) {

        super(localDataManagementRequester);

    }

    @Override
    protected LocalDataManagementResponse getLocalDataManagementResponse(I chainLinkItem) throws ChainLinkException {

        try {
            return (chainLinkItem.getMaxPages() > 0) ? localDataManagementRequester.getQueryResult(chainLinkItem.getLocationUrl(), chainLinkItem.getPage()) : null;
        } catch (LocalDataManagementRequesterException e) {
            throw new ChainLinkException(e);
        }

    }

    @Override
    protected I process(I chainLinkItem, LocalDataManagementResponse localDataManagementResponse) {

        if (localDataManagementResponse != null ) {

            chainLinkItem.setQueryResult((QueryResult) localDataManagementResponse.getResponse());

            if (localDataManagementResponse.isSuccessful()) {

                chainLinkItem.incrPage();
                if (!chainLinkItem.areResultsCompleted()) {
                    chainLinkItem.setToBeReused();
                }

            }

        }

        return chainLinkItem;

    }

    @Override
    protected String getChainLinkId() {
        return "Local Data Management Results Getter";
    }

    @Override
    protected int getNumberOfItemsToBeProcessed(){

        int numberOfItemsToBeProcessed = 0;

        for (ChainLinkItem item : deque){

            I getResultsItem = (I) item;
            int remainingPages = getResultsItem.getMaxPages() - getResultsItem.getPage();

            numberOfItemsToBeProcessed += remainingPages;
        }

        return numberOfItemsToBeProcessed;

    }



}

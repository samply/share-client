package de.samply.share.client.quality.report.chainlinks;/*
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

import de.samply.share.model.common.Error;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementRequester;
import de.samply.share.client.quality.report.localdatamanagement.LocalDataManagementResponse;

public abstract class LdmChainLink<I extends ChainLinkItem> extends ChainLink<I> {

    protected LocalDataManagementRequester localDataManagementRequester;

    public LdmChainLink(LocalDataManagementRequester localDataManagementRequester) {
        this.localDataManagementRequester = localDataManagementRequester;
    }

    protected abstract LocalDataManagementResponse getLocalDataManagementResponse (I chainLinkItem) throws ChainLinkException;
    protected abstract I process (I chainLinkItem, LocalDataManagementResponse localDataManagementResponse);

    @Override
    protected I process(I item) throws ChainLinkException {

        LocalDataManagementResponse localDataManagementResponse = getLocalDataManagementResponse(item);
        item = process(item, localDataManagementResponse);
        item = addError(item, localDataManagementResponse);
        item = setToBeRepeated(item, localDataManagementResponse);

        return item;

    }

    protected I setToBeRepeated(I item, LocalDataManagementResponse localDataManagementResponse){

        if (localDataManagementResponse != null && !localDataManagementResponse.isSuccessful()){
            item.setToBeRepeated();
        }

        return item;
    }

    protected I addError(I item, LocalDataManagementResponse localDataManagementResponse){

        if (localDataManagementResponse != null) {

            Error error = localDataManagementResponse.getError();

            if (error != null) {

                ChainLinkError chainLinkError = new ChainLinkError();
                chainLinkError.setError(error);
                chainLinkError.setChainLinkItem(item);

                item.setChainLinkError(chainLinkError);

            }

        }

        return item;

    }
}

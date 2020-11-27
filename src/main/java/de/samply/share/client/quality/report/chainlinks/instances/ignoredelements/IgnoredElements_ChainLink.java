package de.samply.share.client.quality.report.chainlinks.instances.ignoredelements;/*
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

import de.samply.common.mdrclient.domain.PermissibleValue;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.client.quality.report.MdrIgnoredElements;
import de.samply.share.common.utils.MdrIdDatatype;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.model.searcher.ModelSearcher;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;

import java.util.ArrayList;
import java.util.List;

public class IgnoredElements_ChainLink<I extends ChainLinkItem & QualityResultsContext> extends ChainLink<I>  {

    private static final String EMPTY_VALUE = "";
    private MdrIgnoredElements ignoredElements;
    private ModelSearcher modelSearcher;

    public IgnoredElements_ChainLink(MdrIgnoredElements ignoredElements, ModelSearcher modelSearcher) {

        this.ignoredElements = ignoredElements;
        this.modelSearcher = modelSearcher;

    }

    @Override
    protected String getChainLinkId() {
        return "Ignored Elements Setter";
    }

    @Override
    protected I process(I item) throws ChainLinkException {

        QualityResults qualityResults = item.getQualityResults();

        addIgnoredElementsToQualityResults(qualityResults);

        return item;
    }

    private QualityResults addIgnoredElementsToQualityResults (QualityResults qualityResults){

        for (MdrIdDatatype mdrId : ignoredElements){

            for (String value : getValues(mdrId)){

                qualityResults.put(mdrId, value, new QualityResult());

            }

        }

        return qualityResults;
    }

    private List<String> getValues (MdrIdDatatype mdrIdDatatype){

        List<String> values = new ArrayList<>();
        Validations validations = modelSearcher.getValidations(mdrIdDatatype);

        List<PermissibleValue> permissibleValues = validations.getPermissibleValues();
        if (permissibleValues != null && permissibleValues.size() > 0){

            for (PermissibleValue permissibleValue : permissibleValues){
                values.add(permissibleValue.getValue());
            }

        } else{

            values.add(EMPTY_VALUE);

        }

        return values;

    }


}

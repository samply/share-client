package de.samply.share.client.quality.report.chainlinks.instances.allelements;/*
 * Copyright (C) 2018 Medizinische Informatik in der Translationalen Onkologie,
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

import de.samply.share.client.quality.report.MdrIdAndValidations;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkException;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.instances.ignoredelements.QualityResultsContext;
import de.samply.share.client.quality.report.model.Model;
import de.samply.share.client.quality.report.results.QualityResult;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.common.utils.MdrIdDatatype;

import java.util.Set;

public class NotFoundDataElements_ChainLink<I extends ChainLinkItem & QualityResultsContext> extends ChainLink<I> {


    private static final String EMPTY_VALUE = "";
    private Model model;

    public NotFoundDataElements_ChainLink(Model model) {
        this.model = model;
    }

    @Override
    protected String getChainLinkId() {
        return "Not found data elements";
    }

    @Override
    protected I process(I item) throws ChainLinkException {
        

        QualityResults qualityResults = item.getQualityResults();


        for (MdrIdAndValidations mdrIdAndValidations : model.getMdrIdAndValidations()){


            MdrIdDatatype mdrId = mdrIdAndValidations.getMdrId();
            Set<String> values = qualityResults.getValues(mdrId);
            if (values == null || values.size() == 0){
                qualityResults.put(mdrId, EMPTY_VALUE, new QualityResult());
            }

        }

        return item;

    }

}

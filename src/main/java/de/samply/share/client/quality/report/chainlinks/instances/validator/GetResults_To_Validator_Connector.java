package de.samply.share.client.quality.report.chainlinks.instances.validator;/*
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

import de.samply.share.model.ccp.QueryResult;
import de.samply.share.client.quality.report.chainlinks.ChainLink;
import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.connector.ChainLinkConnectorFunnel;
import de.samply.share.client.quality.report.chainlinks.instances.result.ResultContext;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.operations.QualityResultsAnalyzer;

public class GetResults_To_Validator_Connector<I extends ChainLinkItem & ValidatorContext> extends ChainLinkConnectorFunnel {


    private I item;
    private QualityResultsAnalyzer qualityResultsAnalyzer;

    public GetResults_To_Validator_Connector(ChainLink nextChainLink, QualityResultsAnalyzer qualityResultsAnalyzer) {
        super(nextChainLink);
        this.qualityResultsAnalyzer = qualityResultsAnalyzer;
    }

    @Override
    protected void keepItemForLater(ChainLinkItem chainLinkItem) {
        item = analyze((I) chainLinkItem);
    }

    @Override
    protected ChainLinkItem getItemForNextChainLink() {
        return item;
    }

    private I analyze (I chainLinkItem) {

        QualityResults qualityResults = ((ValidatorContext) chainLinkItem).getQualityResults();
        QueryResult queryResult = ((ResultContext) chainLinkItem).getQueryResult();

        analyze(qualityResults, queryResult);

        return chainLinkItem;
    }

    private void analyze (QualityResults qualityResults, QueryResult queryResult){
        qualityResultsAnalyzer.analyze(qualityResults, queryResult);
    }

}

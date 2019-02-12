package de.samply.share.client.quality.report.chain;/*
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

import de.samply.share.client.quality.report.chainlinks.ChainLinkItem;
import de.samply.share.client.quality.report.chainlinks.instances.file.FileContext;
import de.samply.share.client.quality.report.chainlinks.instances.ignoredelements.QualityResultsContext;
import de.samply.share.client.quality.report.chainlinks.instances.result.ResultContext;
import de.samply.share.client.quality.report.chainlinks.instances.statistic.StatisticContext;
import de.samply.share.client.quality.report.chainlinks.instances.validator.ValidatorContext;
import de.samply.share.client.quality.report.chainlinks.instances.view.ViewContext;
import de.samply.share.client.quality.report.chainlinks.instances.view.ViewsContext;
import de.samply.share.client.quality.report.results.QualityResults;
import de.samply.share.client.quality.report.results.QualityResultsImpl;
import de.samply.share.model.ccp.QueryResult;
import de.samply.share.model.common.View;

import java.util.List;

public class ChainLinkContext extends ChainLinkItem implements ResultContext, StatisticContext, ViewContext, ValidatorContext, FileContext, ViewsContext, QualityResultsContext {

    private String fileId;
    private int page = 0;
    private String locationUrl;
    private View view;
    private int maxPages;
    private QueryResult queryResult;
    private QualityResults qualityResults = new QualityResultsImpl();
    private List<View> views;


    @Override
    public String getLocationUrl() {
        return locationUrl;
    }

    @Override
    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    @Override
    public int getMaxPages() {
        return maxPages;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void incrPage() {
        page++;
    }

    @Override
    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }

    @Override
    public QueryResult getQueryResult() {
        return queryResult;
    }

    @Override
    public boolean areResultsCompleted() {
        return page >= maxPages;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public void setView(View view){
        this.view = view;
    }

    @Override
    public QualityResults getQualityResults() {
        return qualityResults;
    }

    @Override
    public String getFileId() {
        return fileId;
    }

    @Override
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public void setViews(List<View> views) {
        this.views = views;
    }

    @Override
    public List<View> getViews() {
        return views;
    }
}

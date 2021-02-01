package de.samply.share.client.quality.report.chain;

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

public class ChainLinkContext extends ChainLinkItem implements ResultContext, StatisticContext,
    ViewContext, ValidatorContext, FileContext, ViewsContext, QualityResultsContext {

  private String fileId;
  private int page = 0;
  private String locationUrl;
  private View view;
  private int maxPages;
  private QueryResult queryResult;
  private final QualityResults qualityResults = new QualityResultsImpl();
  private List<View> views;


  @Override
  public String getLocationUrl() {
    return locationUrl;
  }

  @Override
  public void setLocationUrl(String locationUrl) {
    this.locationUrl = locationUrl;
  }

  @Override
  public int getMaxPages() {
    return maxPages;
  }

  @Override
  public void setMaxPages(int maxPages) {
    this.maxPages = maxPages;
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
  public QueryResult getQueryResult() {
    return queryResult;
  }

  @Override
  public void setQueryResult(QueryResult queryResult) {
    this.queryResult = queryResult;
  }

  @Override
  public boolean areResultsCompleted() {
    return page >= maxPages;
  }

  @Override
  public View getView() {
    return view;
  }

  public void setView(View view) {
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
  public List<View> getViews() {
    return views;
  }

  @Override
  public void setViews(List<View> views) {
    this.views = views;
  }
}

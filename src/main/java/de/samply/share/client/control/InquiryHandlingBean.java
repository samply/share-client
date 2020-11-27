package de.samply.share.client.control;

import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.pojos.InquiryHandlingRule;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.InquiryHandlingRuleUtil;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * ViewScoped backing bean, used to manage inquiry handling rules.
 */
@ManagedBean(name = "inquiryHandlingBean")
@SessionScoped
public class InquiryHandlingBean implements Serializable {

  private List<Broker> brokers;
  private List<InquiryHandlingRule> inquiryHandlingRules;

  public List<Broker> getBrokers() {
    return brokers;
  }

  public void setBrokers(List<Broker> brokers) {
    this.brokers = brokers;
  }

  public List<InquiryHandlingRule> getInquiryHandlingRules() {
    return inquiryHandlingRules;
  }

  public void setInquiryHandlingRules(List<InquiryHandlingRule> inquiryHandlingRules) {
    this.inquiryHandlingRules = inquiryHandlingRules;
  }

  @PostConstruct
  public void init() {
    refreshLists();
  }

  /**
   * Fetch the current lists of brokers and inquiry handling rules from the database.
   */
  private void refreshLists() {
    brokers = BrokerUtil.fetchBrokers();
    inquiryHandlingRules = InquiryHandlingRuleUtil.fetchInquiryHandlingRules();
  }

  /**
   * Get the broker that is associated with an inquiry handling rule.
   *
   * @param rule the inquiry handling rule for which the broker is wanted
   * @return the broker
   */
  public Broker getBrokerForRule(InquiryHandlingRule rule) {
    return BrokerUtil.fetchBrokerById(rule.getBrokerId());
  }

  /**
   * Update the reply rules in the database.
   */
  public void store() {
    InquiryHandlingRuleUtil.updateInquiryHandlingRules(inquiryHandlingRules);
  }

}




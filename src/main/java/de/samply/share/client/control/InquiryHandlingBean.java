/*
 * Copyright (c) 2017 Medical Informatics Group (MIG),
 * Universitätsklinikum Frankfurt
 *
 * Contact: www.mig-frankfurt.de
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
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with Jersey (https://jersey.java.net) (or a modified version of that
 * library), containing parts covered by the terms of the General Public
 * License, version 2.0, the licensors of this Program grant you additional
 * permission to convey the resulting work.
 */

package de.samply.share.client.control;

import com.google.gson.Gson;
import de.samply.share.client.messages.Messages;
import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.tables.pojos.*;
import de.samply.share.client.model.line.EventLogLine;
import de.samply.share.client.model.line.InquiryLine;
import de.samply.share.client.util.db.*;
import de.samply.share.common.utils.SamplyShareUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewScoped backing bean, used to manage inquiry handling rules
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
     * Fetch the current lists of brokers and inquiry handling rules from the database
     */
    private void refreshLists() {
        brokers = BrokerUtil.fetchBrokers();
        inquiryHandlingRules = InquiryHandlingRuleUtil.fetchInquiryHandlingRules();
    }

    /**
     * Get the broker that is associated with an inquiry handling rule
     *
     * @param rule the inquiry handling rule for which the broker is wanted
     * @return the broker
     */
    public Broker getBrokerForRule(InquiryHandlingRule rule) {
        return BrokerUtil.fetchBrokerById(rule.getBrokerId());
    }

    /**
     * Update the reply rules in the database
     */
    public void store() {
        InquiryHandlingRuleUtil.updateInquiryHandlingRules(inquiryHandlingRules);
    }

}




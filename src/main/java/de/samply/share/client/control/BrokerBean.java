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

import de.samply.share.client.model.db.enums.AuthSchemeType;
import de.samply.share.client.model.db.enums.BrokerStatusType;
import de.samply.share.client.model.db.enums.ReplyRuleType;
import de.samply.share.client.model.db.enums.TargetType;
import de.samply.share.client.model.db.tables.pojos.Broker;
import de.samply.share.client.model.db.tables.pojos.Credentials;
import de.samply.share.client.model.db.tables.pojos.InquiryHandlingRule;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.client.util.db.CredentialsUtil;
import de.samply.share.client.util.db.InquiryHandlingRuleUtil;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omnifaces.util.Messages;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

import static de.samply.share.client.model.db.enums.BrokerStatusType.BS_OK;

/**
 * A ViewScoped backing bean that is used on pages dealing with interaction with searchbrokers
 */
@ManagedBean(name = "brokerBean")
@ViewScoped
public class BrokerBean implements Serializable {

    private static final Logger logger = LogManager.getLogger(BrokerBean.class);

    private List<Broker> brokerList;

    private Broker newBroker;

    private String newBrokerEmail;

    private boolean newBrokerFullResult;

    private ReplyRuleType newBrokerReplyRule;

    public List<Broker> getBrokerList() {
        return brokerList;
    }

    public void setBrokerList(List<Broker> brokerList) {
        this.brokerList = brokerList;
    }

    public Broker getNewBroker() {
        return newBroker;
    }

    public void setNewBroker(Broker newBroker) {
        this.newBroker = newBroker;
    }

    public String getNewBrokerEmail() {
        return newBrokerEmail;
    }

    public void setNewBrokerEmail(String newBrokerEmail) {
        this.newBrokerEmail = newBrokerEmail;
    }

    public boolean isNewBrokerFullResult() {
        return newBrokerFullResult;
    }

    public void setNewBrokerFullResult(boolean newBrokerFullResult) {
        this.newBrokerFullResult = newBrokerFullResult;
    }

    public ReplyRuleType getNewBrokerReplyRule() {
        return newBrokerReplyRule;
    }

    public void setNewBrokerReplyRule(ReplyRuleType newBrokerReplyRule) {
        this.newBrokerReplyRule = newBrokerReplyRule;
    }

    @PostConstruct
    public void init() {
        refreshBrokerList();
        newBroker = new Broker();
        newBrokerEmail = "";
        newBrokerFullResult = false;
        newBrokerReplyRule = ReplyRuleType.RR_NO_AUTOMATIC_ACTION;
    }

    public Credentials getCredentials(Broker broker) {
        return CredentialsUtil.getCredentialsForBroker(broker);
    }

    private void refreshBrokerList() {
        brokerList = BrokerUtil.fetchBrokers();
    }

    /**
     * Send an activation code to the broker in order to complete registration
     *
     * @param broker the broker to send the code to
     * @param activationCode the code to send to the broker
     * @return navigation outcome
     */
    public String sendActivationCode(Broker broker, String activationCode) {
        try {
            BrokerConnector brokerConnector = new BrokerConnector(broker);
            int retCode = brokerConnector.activate(activationCode);
            if (retCode != HttpStatus.SC_CREATED) {
                Messages.create("bl.activationError")
                        .detail(Integer.toString(retCode))
                        .error().add();
                return "";
            } else {
                broker.setStatus(BS_OK);
                BrokerUtil.updateBroker(broker);
                return "broker_list?faces-redirect=true";
            }
        } catch (BrokerConnectorException e) {
            logger.error("Caught BrokerConnectorException when trying to register to broker", e);
            return "";
        }
    }

    /**
     * Remove a broker and the corresponding credentials
     *
     * Send a command to delete this instance of the client from the broker and delete the broker information from db
     *
     * @param broker the broker to delete
     * @return navigation information
     */
    public String deleteBroker(Broker broker) {
        sendDeleteCommand(broker);
        CredentialsUtil.deleteCredentialsForBroker(broker);
        BrokerUtil.deleteBroker(broker);
        return "broker_list?faces-redirect=true";
    }

    /**
     * Send the delete command to a broker in order to remove this client from the brokers database
     *
     * @param broker the broker to send the delete command to
     * @return true if the broker sent an acknowledging reply, false otherwise
     */
    private boolean sendDeleteCommand(Broker broker) {
        BrokerConnector brokerConnector = new BrokerConnector(broker);
        try {
            return brokerConnector.unregister();
        } catch (BrokerConnectorException e) {
            logger.debug("Exception caught while trying to delete this client from broker " + broker.getAddress(), e);
            return false;
        }
    }

    /**
     * Join a new searchbroker
     *
     * @return navigation information
     */
    public String join() {
        Credentials credentials = new Credentials();
        credentials.setUsername(newBrokerEmail);
        credentials.setAuthScheme(AuthSchemeType.AS_APIKEY);
        credentials.setTarget(TargetType.TT_BROKER);
        credentials.setPasscode("");
        int credentialsId = CredentialsUtil.insertCredentials(credentials);

        newBroker.setCredentialsId(credentialsId);
        int brokerId = BrokerUtil.insertBroker(newBroker);
        newBroker = BrokerUtil.fetchBrokerById(brokerId); // refresh broker object with newly assigned id

        try {
            BrokerConnector brokerConnector = new BrokerConnector(newBroker);
            BrokerStatusType brokerStatus = brokerConnector.register();
            newBroker.setStatus(brokerStatus);
            BrokerUtil.updateBroker(newBroker);

            // Insert a new default handling rule for this broker
            InquiryHandlingRule inquiryHandlingRule = new InquiryHandlingRule();
            inquiryHandlingRule.setBrokerId(brokerId);
            inquiryHandlingRule.setFullResult(newBrokerFullResult);
            inquiryHandlingRule.setAutomaticReply(newBrokerReplyRule);
            InquiryHandlingRuleUtil.insertInquiryHandlingRule(inquiryHandlingRule);
        } catch (BrokerConnectorException e) {
            logger.error("Caught BrokerConnectorException when trying to join broker", e);
            return "";
        }

        // TODO faces message on error and success?
        return "broker_list?faces-redirect=true";
    }
}

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

package de.samply.share.client.job;

import de.samply.share.client.model.db.enums.InquiryStatusType;
import de.samply.share.client.model.db.enums.QueryLanguageType;
import de.samply.share.client.model.db.tables.pojos.InquiryCriteria;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnectorCql;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.client.util.db.InquiryCriteriaUtil;
import de.samply.share.client.util.db.InquiryResultUtil;
import de.samply.share.model.cql.CqlResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CheckInquiryStatusJobCql extends AbstractCheckInquiryStatusJob<LdmConnectorCql> {

    private static final Logger logger = LogManager.getLogger(CheckInquiryStatusJobCql.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        prepareExecute(jobExecutionContext);

        if (!jobParams.isStatsDone()) {
            logger.debug("Stats were not available before. Checking again.");
            checkForStatsResult(jobExecutionContext);
        }
    }

    boolean applyReplyRulesImmediately(boolean isStats) {
        return false;
    }

    InquiryCriteria getInquiryCriteria() {
        return InquiryCriteriaUtil.getFirstCriteriaOriginal(inquiryDetails, QueryLanguageType.QL_CQL, jobParams.getEntityType());
    }


    void handleInquiryStatusReady() {
        inquiryDetails.setStatus(InquiryStatusType.IS_PARTIALLY_READY);

        CheckInquiryStatusReadyForMultipleCriteriaJob.spawnNewJob(inquiryDetails);
    }

    void processReplyRule(BrokerConnector brokerConnector) throws BrokerConnectorException {
        try {
            CqlResult queryResult = ldmConnector.getResults(InquiryResultUtil.fetchLatestInquiryResultForInquiryDetailsById(inquiryDetails.getId()).getLocation());
            brokerConnector.reply(inquiryDetails, queryResult);
        } catch (LDMConnectorException e) {
            e.printStackTrace();
        }
    }
}

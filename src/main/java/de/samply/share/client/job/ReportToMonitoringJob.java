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

import de.samply.share.client.control.ApplicationBean;
import de.samply.share.client.control.ApplicationUtils;
import de.samply.share.client.job.params.ReportToMonitoringJobParams;
import de.samply.share.client.model.check.ReferenceQueryCheckResult;
import de.samply.share.client.util.connector.BrokerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.LdmConnectorCentraxx;
import de.samply.share.client.util.connector.exception.BrokerConnectorException;
import de.samply.share.client.util.db.BrokerUtil;
import de.samply.share.common.model.dto.monitoring.StatusReportItem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This job gathers the amount of patients (total, dktk flagged and for a reference query) and the time it takes to
 * get the results (with an uncertainty of ~15s in order to avoid polling too frequently) for the reference query
 */
@DisallowConcurrentExecution
public class ReportToMonitoringJob implements Job {

    private static final Logger LOGGER = LogManager.getLogger(ReportToMonitoringJob.class);

    private final LdmConnector ldmConnector;
    private final List<BrokerConnector> brokerConnectors;
    private final ReportToMonitoringJobParams jobParams;

    public ReportToMonitoringJob() {
        ldmConnector = ApplicationBean.getLdmConnector();
        brokerConnectors = BrokerUtil.fetchBrokers().stream().map(BrokerConnector::new).collect(Collectors.toList());
        jobParams = new ReportToMonitoringJobParams();

        LOGGER.debug(ReportToMonitoringJob.class.getName() + " created");
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        if (jobParams.anyCheckToPerform()) {
            for (BrokerConnector brokerConnector : brokerConnectors) {
                LOGGER.debug("sending report to broker: " + brokerConnector.getBroker().getAddress());
                List<StatusReportItem> statusReportItems = gatherStatusReportItems(brokerConnector);
                try {
                    brokerConnector.sendStatusReportItems(statusReportItems);
                } catch (BrokerConnectorException e) {
                    LOGGER.warn("Caught exception while trying to report to monitoring", e);
                }
            }
        }
    }

    /**
     * Delegate to all check methods that are enabled
     *
     * @param brokerConnector the broker connector used to get the reference query
     * @return a list of items to be reported
     */
    private List<StatusReportItem> gatherStatusReportItems(BrokerConnector brokerConnector) {
        List<StatusReportItem> statusReportItems = new ArrayList<>();


        if (ApplicationUtils.isDktk()) {
            if (jobParams.isCountTotal()) {
                StatusReportItem totalCount = getTotalCount();
                statusReportItems.add(totalCount);
            }

            if (jobParams.isCountDktkFlagged()) {
                StatusReportItem dktkCount = getDktkCount();
                statusReportItems.add(dktkCount);
            }

            if (jobParams.isCountReferenceQuery() || jobParams.isTimeReferenceQuery()) {
                ReferenceQueryCheckResult referenceQueryCheckResult = getReferenceQueryResult(brokerConnector);
                if (jobParams.isCountReferenceQuery()) {
                    StatusReportItem referenceQueryCount = getReferenceQueryCount(referenceQueryCheckResult);
                    statusReportItems.add(referenceQueryCount);
                }
                if (jobParams.isTimeReferenceQuery()) {
                    StatusReportItem referenceQueryTime = getReferenceQueryTime(referenceQueryCheckResult);
                    statusReportItems.add(referenceQueryTime);
                }
            }

            if (jobParams.isCentraxxMappingInformation()) {
                StatusReportItem centraxxMappingVersion = getCentraxxMappingVersion();
                statusReportItems.add(centraxxMappingVersion);
                StatusReportItem centraxxMappingDate = getCentraxxMappingDate();
                statusReportItems.add(centraxxMappingDate);
            }
        }

        if (ApplicationUtils.isSamply()) {
            if (jobParams.isCountTotal()) {
                StatusReportItem totalCount = getTotalCount();
                statusReportItems.add(totalCount);
            }
            if (jobParams.isCountReferenceQuery() || jobParams.isTimeReferenceQuery()) {
                ReferenceQueryCheckResult referenceQueryCheckResult = getReferenceQueryResult(brokerConnector);
                if (jobParams.isCountReferenceQuery()) {
                    StatusReportItem referenceQueryCount = getReferenceQueryCount(referenceQueryCheckResult);
                    statusReportItems.add(referenceQueryCount);
                }
                if (jobParams.isTimeReferenceQuery()) {
                    StatusReportItem referenceQueryTime = getReferenceQueryTime(referenceQueryCheckResult);
                    statusReportItems.add(referenceQueryTime);
                }
            }
        }
        return statusReportItems;
    }

    /**
     * Get the amount of patients that have the DKTK consent flag set from local datamanagement
     *
     * @return the amount of patients and the exit status
     */
    private StatusReportItem getDktkCount() {
        StatusReportItem dktkCount = new StatusReportItem();
        dktkCount.setParameter_name(StatusReportItem.PARAMETER_PATIENTS_DKTKFLAGGED_COUNT);
        try {
            int count = ldmConnector.getPatientCount(true);
            dktkCount.setExit_status("0");
            dktkCount.setStatus_text(Integer.toString(count));
        } catch (Exception e) {
            dktkCount.setExit_status("1");
        }

        return dktkCount;
    }

    /**
     * Get the total amount of patients from local datamanagement
     * <p>
     * This will only count patients with a DKTK site pseudonym that fit the ldm-defined criteria.
     * As of now, this includes patients with a C.* or certain D.* diagnoses
     *
     * @return the amount of patients and the exit status
     */
    private StatusReportItem getTotalCount() {
        StatusReportItem totalCount = new StatusReportItem();
        totalCount.setParameter_name(StatusReportItem.PARAMETER_PATIENTS_TOTAL_COUNT);
        try {
            int count = ldmConnector.getPatientCount(false);
            totalCount.setExit_status("0");
            totalCount.setStatus_text(Integer.toString(count));
        } catch (Exception e) {
            LOGGER.error(e);
            totalCount.setExit_status("1");
        }

        return totalCount;
    }

    /**
     * Execute the reference query and receive the patient count and the execution time
     *
     * @param brokerConnector the broker connector from which to get the reference query
     * @return patient count and the execution time
     */
    private ReferenceQueryCheckResult getReferenceQueryResult(BrokerConnector brokerConnector) {
        ReferenceQueryCheckResult referenceQueryCheckResult = null;
        try {
            if(ApplicationUtils.isLanguageQuery()) {
                referenceQueryCheckResult = ldmConnector.getReferenceQueryCheckResult(brokerConnector.getReferenceQuery());
            }else if(ApplicationUtils.isLanguageCql()){
                referenceQueryCheckResult= ldmConnector.getReferenceQueryCheckResult(brokerConnector.getReferenceQueryCql());
            }
        } catch (Exception e) {
            referenceQueryCheckResult = new ReferenceQueryCheckResult();
        }

        return referenceQueryCheckResult;
    }

    /**
     * Get the StatusReportItem for the Amount of patients from a ReferenceQueryCheckResult
     *
     * @param referenceQueryCheckResult the result that was generated while executing the reference query
     * @return a status report item, containing the total amount of patients found in local datamanagement for the reference query
     */
    private static StatusReportItem getReferenceQueryCount(ReferenceQueryCheckResult referenceQueryCheckResult) {
        StatusReportItem referenceQueryCount = new StatusReportItem();
        referenceQueryCount.setParameter_name(StatusReportItem.PARAMETER_REFERENCE_QUERY_RESULTCOUNT);

        if (referenceQueryCheckResult != null && referenceQueryCheckResult.getCount() >= 0) {
            referenceQueryCount.setExit_status("0");
            referenceQueryCount.setStatus_text(Integer.toString(referenceQueryCheckResult.getCount()));
        } else {
            referenceQueryCount.setExit_status("1");
        }

        return referenceQueryCount;
    }

    /**
     * Get the StatusReportItem for the time it took to execute the reference query from a ReferenceQueryCheckResult
     *
     * @param referenceQueryCheckResult the result that was generated while executing the reference query
     * @return a status report item, containing the time it took to execute the reference query (containing a vagueness
     * of 15 seconds)
     */
    private static StatusReportItem getReferenceQueryTime(ReferenceQueryCheckResult referenceQueryCheckResult) {
        StatusReportItem referenceQueryTime = new StatusReportItem();
        referenceQueryTime.setParameter_name(StatusReportItem.PARAMETER_REFERENCE_QUERY_RUNTIME);

        if (referenceQueryCheckResult != null && referenceQueryCheckResult.getExecutionTimeMilis() >= 0) {
            referenceQueryTime.setExit_status("0");
            referenceQueryTime.setStatus_text(Long.toString(referenceQueryCheckResult.getExecutionTimeMilis()));
        } else {
            referenceQueryTime.setExit_status("1");
        }

        return referenceQueryTime;
    }

    /**
     * Get the version string of the centraxx mdr mapping script as StatusReportItem
     *
     * @return the version string of mdr mapping script
     */
    private StatusReportItem getCentraxxMappingVersion() {
        StatusReportItem centraxxMappingVersion = new StatusReportItem();
        centraxxMappingVersion.setParameter_name(StatusReportItem.PARAMETER_CENTRAXX_MAPPING_VERSION);
        if (!ldmConnector.isLdmCentraxx()) {
            centraxxMappingVersion.setExit_status("1");
            centraxxMappingVersion.setStatus_text("Does not apply");
        } else {
            try {
                String mappingVersion = ((LdmConnectorCentraxx)ldmConnector).getMappingVersion();
                centraxxMappingVersion.setExit_status("0");
                centraxxMappingVersion.setStatus_text(mappingVersion);
            } catch (Exception e) {
                LOGGER.error(e);
                centraxxMappingVersion.setExit_status("1");
            }
        }

        return centraxxMappingVersion;
    }

    /**
     * Get the date string of the centraxx mdr mapping script as StatusReportItem
     *
     * @return the date string of mdr mapping script
     */
    private StatusReportItem getCentraxxMappingDate() {
        StatusReportItem centraxxMappingDate = new StatusReportItem();
        centraxxMappingDate.setParameter_name(StatusReportItem.PARAMETER_CENTRAXX_MAPPING_DATE);
        if (!ldmConnector.isLdmCentraxx()) {
            centraxxMappingDate.setExit_status("1");
            centraxxMappingDate.setStatus_text("Does not apply");
        } else {
            try {
                String mappingDate = ((LdmConnectorCentraxx)ldmConnector).getMappingDate();
                centraxxMappingDate.setExit_status("0");
                centraxxMappingDate.setStatus_text(mappingDate);
            } catch (Exception e) {
                LOGGER.error(e);
                centraxxMappingDate.setExit_status("1");
            }
        }

        return centraxxMappingDate;
    }
}

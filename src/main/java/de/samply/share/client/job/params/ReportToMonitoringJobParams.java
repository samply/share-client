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

package de.samply.share.client.job.params;

import de.samply.share.client.model.EnumConfiguration;
import de.samply.share.client.util.db.ConfigurationUtil;

/**
 * The settings for an ReportToMonitoringJob are kept in an instance of this class
 *
 * Reads the settings for checks to perform from the database
 */
public class ReportToMonitoringJobParams {

    public static final String JOBGROUP = "MaintenanceGroup";
    public static final String JOBNAME = "ReportToMonitoringJob";
    public static final String TRIGGERNAME = "ReportToMonitoringJobTrigger";

    private boolean countTotal;
    private boolean countDktkFlagged;
    private boolean countReferenceQuery;
    private boolean timeReferenceQuery;
    private boolean centraxxMappingInformation;

    public ReportToMonitoringJobParams() {
        this.countTotal = ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_COUNT_TOTAL);
        this.countDktkFlagged = ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_COUNT_DKTKFLAG);
        this.countReferenceQuery = ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_COUNT_REFERENCEQUERY);
        this.timeReferenceQuery = ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_TIME_REFERENCEQUERY);
        this.centraxxMappingInformation = ConfigurationUtil.getConfigurationElementValueAsBoolean(EnumConfiguration.MONITORING_REPORT_CENTRAXX_MAPPING_INFORMATION);
    }

    public boolean isCountTotal() {
        return countTotal;
    }

    public void setCountTotal(boolean countTotal) {
        this.countTotal = countTotal;
    }

    public boolean isCountDktkFlagged() {
        return countDktkFlagged;
    }

    public void setCountDktkFlagged(boolean countDktkFlagged) {
        this.countDktkFlagged = countDktkFlagged;
    }

    public boolean isCountReferenceQuery() {
        return countReferenceQuery;
    }

    public void setCountReferenceQuery(boolean countReferenceQuery) {
        this.countReferenceQuery = countReferenceQuery;
    }

    public boolean isTimeReferenceQuery() {
        return timeReferenceQuery;
    }

    public void setTimeReferenceQuery(boolean timeReferenceQuery) {
        this.timeReferenceQuery = timeReferenceQuery;
    }

    public boolean isCentraxxMappingInformation() {
        return centraxxMappingInformation;
    }

    public void setCentraxxMappingInformation(boolean centraxxMappingInformation) {
        this.centraxxMappingInformation = centraxxMappingInformation;
    }

    /**
     * Is any of the (currently) four checks enabled?
     *
     * @return true if one ore more are enabled, false otherwise
     */
    public boolean anyCheckToPerform() {
        return (countTotal || countDktkFlagged || countReferenceQuery || timeReferenceQuery || centraxxMappingInformation);
    }

    @Override
    public String toString() {
        return "ReportToMonitoringJobParams{" +
                "countTotal=" + countTotal +
                ", countDktkFlagged=" + countDktkFlagged +
                ", countReferenceQuery=" + countReferenceQuery +
                ", timeReferenceQuery=" + timeReferenceQuery +
                ", centraxxMappingVersion=" + centraxxMappingInformation +
                '}';
    }
}

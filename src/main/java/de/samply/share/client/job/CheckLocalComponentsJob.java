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
import de.samply.share.client.util.connector.IdManagerConnector;
import de.samply.share.client.util.connector.LdmConnector;
import de.samply.share.client.util.connector.exception.IdManagerConnectorException;
import de.samply.share.client.util.connector.exception.LDMConnectorException;
import de.samply.share.common.model.dto.UserAgent;
import de.samply.share.common.utils.ProjectInfo;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;

/**
 * This job checks the other local components (id manager, local datamanagement) for their version and status
 */
@DisallowConcurrentExecution
public class CheckLocalComponentsJob implements Job {

    private static final Logger logger = LogManager.getLogger(CheckLocalComponentsJob.class);
    private static IdManagerConnector idManagerConnector;
    private static LdmConnector ldmConnector;
    private static UserAgent userAgent;

    static {
        idManagerConnector = new IdManagerConnector();
        ldmConnector = ApplicationBean.getLdmConnector();
        userAgent = ApplicationBean.getUserAgent();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String ldmString = getLocalDatamanagementString();
        String idManagerString = getIdManagerString();
        UserAgent newUserAgent;

        if (userAgent == null) {
            newUserAgent = new UserAgent(ProjectInfo.INSTANCE.getProjectName(), "Samply.Share", ProjectInfo.INSTANCE.getVersionString());
        } else {
            newUserAgent = new UserAgent(userAgent.getProjectContext(), userAgent.getShareName(), userAgent.getShareVersion());
        }

        if (ldmString != null && ldmString.indexOf('/') > 0) {
            String ldmName = ldmString.substring(0, ldmString.indexOf('/'));
            String ldmVersion = ldmString.substring(ldmString.indexOf('/') + 1);
            newUserAgent.setLdmName(ldmName);
            newUserAgent.setLdmVersion(ldmVersion);
        }

        if (idManagerString != null && idManagerString.indexOf('/') > 0) {
            String idManagerName = idManagerString.substring(0, idManagerString.indexOf('/'));
            String idManagerVersion = idManagerString.substring(idManagerString.indexOf('/') + 1);
            newUserAgent.setIdManagerName(idManagerName);
            newUserAgent.setIdManagerVersion(idManagerVersion);
        }

        if (newUserAgent.equals(userAgent)) {
            logger.debug("UserAgent unchanged");
        } else {
            logger.info("UserAgent changed to: " + newUserAgent.toString());
            ApplicationBean.setUserAgent(newUserAgent);
        }
    }

    /**
     * Get name and version number from local datamanagement
     *
     * @return a String consisting of name and version number as reported by the local datamanagement. Separated by a forward slash
     */
    private String getLocalDatamanagementString() {
        try {
            return ldmConnector.getUserAgentInfo();
        } catch (LDMConnectorException e) {
            logger.warn("Could not read User Agent Info from local datamanagement.");
            return "Unknown Local Datamanagement/unknown";
        }
    }

    /**
     * Get name and version number from ID manager
     *
     * @return a String consisting of name and version number as reported by the ID Manager. Separated by a forward slash
     */
    private String getIdManagerString() {
        try {
            return idManagerConnector.getUserAgentInfo();
        } catch (IdManagerConnectorException e) {
            logger.warn("Could not read User Agent Info from id management.");
            return "Unknown ID Management/unknown";
        }
    }


}

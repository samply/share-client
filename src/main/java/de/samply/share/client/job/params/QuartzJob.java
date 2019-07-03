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

import java.util.Date;

/**
 * A representation of a quartz job, keeping the information that is needed on the job list page
 */
public class QuartzJob {

    private final String jobName;
    private final String jobGroup;
    private final Date started;
    private final Date nextFireTime;
    private final Date previousFireTime;
    private final String cronExpression;
    private final boolean paused;
    private final String description;

    public QuartzJob(String jobName, String jobGroup, Date started, Date nextFireTime, Date previousFireTime, String cronExpression, boolean paused, String description) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.started = started;
        this.nextFireTime = nextFireTime;
        this.previousFireTime = previousFireTime;
        this.cronExpression = cronExpression;
        this.paused = paused;
        this.description = description;
    }

    public QuartzJob(String jobKey, Date started, Date nextFireTime, Date previousFireTime, String cronExpression, boolean paused, String description) {
        this.jobName = jobKey.substring(jobKey.indexOf('.') + 1);
        this.jobGroup = jobKey.substring(0, jobKey.indexOf('.'));
        this.started = started;
        this.nextFireTime = nextFireTime;
        this.previousFireTime = previousFireTime;
        this.cronExpression = cronExpression;
        this.paused = paused;
        this.description = description;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public Date getStarted() {
        return started;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public String getJobKey() {
        return jobGroup + "." + jobName;
    }

    public boolean isPaused() {
        return paused;
    }

    public String getDescription() {
        return description;
    }
}
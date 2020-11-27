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

package de.samply.share.client.util.db;

import de.samply.share.client.model.db.tables.daos.JobScheduleDao;
import de.samply.share.client.model.db.tables.pojos.JobSchedule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;

import java.sql.SQLException;
import java.util.List;

/**
 * Helper Class for CRUD operations with job scheduling objects
 */
public class JobScheduleUtil {

    private static final Logger logger = LogManager.getLogger(JobScheduleUtil.class);

    private static JobScheduleDao jobScheduleDao;

    static {
        jobScheduleDao = new JobScheduleDao(ResourceManager.getConfiguration());
    }

    // Prevent instantiation
    private JobScheduleUtil() {
    }

    /**
     * Get the job scheduling DAO
     *
     * @return the job scheduling DAO
     */
    public static JobScheduleDao getJobScheduleDao() {
        return jobScheduleDao;
    }

    /**
     * Get all Jobs to be scheduled from the database
     *
     * @return the list of all jobs with their scheduling info
     */
    public static List<JobSchedule> getJobSchedules() {
        return jobScheduleDao.findAll();
    }

    /**
     * Get one job by its id
     *
     * @param id the id of the job to get
     * @return the job and its scheduling info
     */
    public static JobSchedule fetchJobScheduleById(int id) {
        return jobScheduleDao.fetchOneById(id);
    }

    /**
     * Get one job by its job key
     *
     * @param jobKey the job key of the job to get
     * @return the job and its scheduling info
     */
    public static JobSchedule fetchJobScheduleByJobKey(String jobKey) {
        return jobScheduleDao.fetchOneByJobKey(jobKey);
    }

    /**
     * Insert a new job with scheduling info into the database
     *
     * @param jobSchedule the new job to insert
     * @return the assigned database id of the newly inserted job
     */
    public static void insertJobSchedule(JobSchedule jobSchedule) {
        jobScheduleDao.insert(jobSchedule);
    }

    /**
     * Update a job in the database
     *
     * @param jobSchedule the job to update
     */
    public static void updateJobSchedule(JobSchedule jobSchedule) {
        jobScheduleDao.update(jobSchedule);
    }

    /**
     * Delete a job from the database
     *
     * @param jobSchedule the job to delete
     */
    public static void deleteJobSchedule(JobSchedule jobSchedule) {
        jobScheduleDao.delete(jobSchedule);
    }

    /**
     * Delete a job from the database
     *
     * @param jobKey the job key of the job to delete
     */
    public static void deleteJobScheduleByJobKey(String jobKey) {
        jobScheduleDao.delete(fetchJobScheduleByJobKey(jobKey));
    }

    /**
     * Set the suspended state of a job
     *
     * @param jobKey the job key of the job to suspend/resume
     * @param suspended true if the job shall be suspended, false if it shall be running
     */
    public static void setSuspended(JobKey jobKey, boolean suspended) {
        JobSchedule jobSchedule = fetchJobScheduleByJobKey(jobKey.toString());
        jobSchedule.setPaused(suspended);
        jobScheduleDao.update(jobSchedule);
    }
}

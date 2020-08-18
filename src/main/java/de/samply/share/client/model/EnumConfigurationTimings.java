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

package de.samply.share.client.model;

/**
 * Timing related configuration items. Used to control intervals in job scheduling.
 */
public enum EnumConfigurationTimings {
    JOB_CHECK_INQUIRY_STATUS_INITIAL_DELAY_SECONDS,
    JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_INTERVAL_SECONDS,
    JOB_CHECK_INQUIRY_STATUS_STATS_RETRY_ATTEMPTS,
    JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_INTERVAL_SECONDS,
    JOB_CHECK_INQUIRY_STATUS_RESULTS_RETRY_ATTEMPTS,
    JOB_CHECK_SCHEDULED_INQUIRY_INTERVAL_MINUTES,
    JOB_MOVE_INQUIRIES_TO_ARCHIVE_AFTER_DAYS,
    UPLOAD_RETRY_PATIENT_UPLOAD_ATTEMPTS,
    UPLOAD_RETRY_PATIENT_UPLOAD_INTERVAL
}

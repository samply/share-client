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

/**
 * Used as result to be interpreted by the CheckInquiryStatusJobListener
 */
public class CheckInquiryStatusJobResult {
    private boolean isRescheduled;
    private boolean resetStatusFlags;

    public CheckInquiryStatusJobResult() {
        this.isRescheduled = false;
        this.resetStatusFlags = false;
    }

    public CheckInquiryStatusJobResult(boolean isRescheduled, boolean resetStatusFlags) {
        this.isRescheduled = isRescheduled;
        this.resetStatusFlags = resetStatusFlags;
    }

    public boolean isRescheduled() {
        return isRescheduled;
    }

    public void setRescheduled(boolean rescheduled) {
        isRescheduled = rescheduled;
    }

    public boolean isResetStatusFlags() {
        return resetStatusFlags;
    }

    public void setResetStatusFlags(boolean resetStatusFlags) {
        this.resetStatusFlags = resetStatusFlags;
    }
}

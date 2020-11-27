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

package de.samply.share.client.model.line;

import java.io.Serializable;

/**
 * Log element to show on the Log Viewer page.
 *
 */
public class InquiryLine implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The inquiry id*/
	private int id;

	/** The inquiry name. */
	private String name;

	/** What entities are searched for. */
	private String searchFor;

	/** When was the inquiry received from the broker. */
	private String receivedAt;

    /** When was the inquiry archived. */
    private String archivedAt;

	/** How many results were found. */
	private String found;

	/** When were the results found. */
	private String asOf;
	
	/** What is the name of the broker that delivered this inquiry*/
	private String brokerName;

	/** If there was an error code...show it */
	private String errorCode;

	/** Has the user already seen this? */
	private boolean seen;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the searchFor
     */
    public String getSearchFor() {
        return searchFor;
    }

    /**
     * @param searchFor the searchFor to set
     */
    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }

    /**
     * @return the receivedAt
     */
    public String getReceivedAt() {
        return receivedAt;
    }

    /**
     * @param receivedAt the receivedAt to set
     */
    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(String archivedAt) {
        this.archivedAt = archivedAt;
    }

    /**
     * @return the found
     */
    public String getFound() {
        return found;
    }

    /**
     * @param found the found to set
     */
    public void setFound(String found) {
        this.found = found;
    }

    /**
     * @return the asOf
     */
    public String getAsOf() {
        return asOf;
    }

    /**
     * @param asOf the asOf to set
     */
    public void setAsOf(String asOf) {
        this.asOf = asOf;
    }

    /**
     * @return the brokerName
     */
    public String getBrokerName() {
        return brokerName;
    }

    /**
     * @param brokerName the brokerName to set
     */
    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
